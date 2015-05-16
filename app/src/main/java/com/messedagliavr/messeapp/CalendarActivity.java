package com.messedagliavr.messeapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.messedagliavr.messeapp.AsyncTasks.EventParser;
import com.messedagliavr.messeapp.AsyncTasks.SEvents;
import com.messedagliavr.messeapp.Databases.MainDB;

import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity {

    public static String nointernet;
    public static String idical = null;
    public static ArrayList<Spanned> icalarr = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_item);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.eventi));
        if (CheckInternet()) {
            nointernet = "false";
            new SEvents(this).execute();
        } else {
            String[] outdated = {"newsdate", "calendardate"};
            MainDB databaseHelper = new MainDB(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String nodata = "1995-01-19 23:40:20";
            Cursor date = db.query("lstchk", // The table to query
                    outdated, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            date.moveToFirst();
            String verifydatenews = date.getString(date
                    .getColumnIndex("newsdate"));
            date.close();
            db.close();
            if (!nodata.equals(verifydatenews)) {
                nointernet = "true";
                SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_news);
                mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#fffbb901"), Color.parseColor("#ff1a171b"));
                mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
                mSwipeRefreshLayout.setRefreshing(true);
                new SEvents(this).execute();

            } else {
                Toast.makeText(this,
                        R.string.noconnection, Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    public boolean CheckInternet() {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected()) {
            connected = true;
        } else {
            try {
                if (mobile.isConnected()) connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connected;
    }


    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ical:
                if (android.os.Build.VERSION.SDK_INT < 14) {
                    Toast.makeText(this, R.string.noapilevel,
                            Toast.LENGTH_LONG).show();
                } else {
                    idical = icalarr.get(info.position).toString();
                    new EventParser(this).execute();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
