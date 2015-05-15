package com.messedagliavr.messeapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.messedagliavr.messeapp.AsyncTasks.SNews;
import com.messedagliavr.messeapp.Databases.MainDB;

public class NewsActivity extends AppCompatActivity {

    public static String nointernet;
    static Window window;

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


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.notizie));
        if (Build.VERSION.SDK_INT >= 21) {
            window = getWindow();
            window.setEnterTransition(new Slide(Gravity.BOTTOM).excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
            window.setExitTransition(new Slide(Gravity.TOP).excludeTarget(android.R.id.statusBarBackground, true).excludeTarget(android.R.id.navigationBarBackground, true));
        }
        if (CheckInternet()) {
            nointernet = "false";
            new SNews(this, false).execute();

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
                if (Build.VERSION.SDK_INT >= 21) {
                    new SNews(this, false).execute();
                } else {
                    new SNews(this, false).execute();
                }
            } else {
                Toast.makeText(this, R.string.noconnection,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
