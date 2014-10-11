package com.messedagliavr.messeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class timetable extends Activity implements
        AdapterView.OnItemSelectedListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orarimenu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.palestre:
                Toast.makeText(timetable.this, R.string.notavailable,
                        Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
    public String[] items() {
        try {
            String[] items = MainActivity.context.getResources().getStringArray(R.array.classi);
            return items;
        } catch (RuntimeException e) {
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    return null;
    }

    String fname = null;

    @Override
    public void onCreate(Bundle icicle) {
        String[] items= items();
        super.onCreate(icicle);
        setContentView(R.layout.timetable);
        Database databaseHelper = new Database(getBaseContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = { "fname" };
        Cursor classe = db.query("class", // The table to query
                columns, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        classe.moveToFirst();
        fname = classe.getString(classe.getColumnIndex("fname"));
        classe.close();
        db.close();
        if (fname.matches("novalue") == false) {
            items[0] = MainActivity.context.getResources().getString(R.string.defaultclass) + " " + fname.toUpperCase();
        }
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter<?> aa = new ArrayAdapter<Object>(this,
                android.R.layout.simple_spinner_item, items);

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
    }

    @SuppressLint({ "DefaultLocale", "SetJavaScriptEnabled" })
    public void onItemSelected(AdapterView<?> parent, View v, int position,
                               long id) {
        String[] items= items();
        WebView descrizioneview = (WebView) findViewById(R.id.imageorario);
        if (position == 0) {
            if (fname.matches("novalue") == false) {
                descrizioneview.getSettings().setJavaScriptEnabled(true);
                descrizioneview.getSettings().setLoadWithOverviewMode(true);
                descrizioneview.getSettings().setUseWideViewPort(true);
                descrizioneview
                        .setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                descrizioneview.setScrollbarFadingEnabled(true);
                descrizioneview.getSettings().setBuiltInZoomControls(true);
                descrizioneview.loadUrl("file:///android_res/drawable/o"
                        + fname + ".png");
            } else {
                descrizioneview.loadData("", "text/html", "UTF-8");
            }
        } else {
            Database databaseHelper = new Database(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("fname", items[position].toLowerCase());
            @SuppressWarnings("unused")
            long samerow = db.update("class", values, null, null);
            descrizioneview.getSettings().setJavaScriptEnabled(true);
            descrizioneview.getSettings().setLoadWithOverviewMode(true);
            descrizioneview.getSettings().setUseWideViewPort(true);
            descrizioneview
                    .setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            descrizioneview.setScrollbarFadingEnabled(true);
            descrizioneview.getSettings().setBuiltInZoomControls(true);
            descrizioneview.loadUrl("file:///android_res/drawable/o"
                    + items[position].toLowerCase() + ".png");
            db.close();
        }
    }

    @SuppressWarnings("unused")
    public void onNothingSelected(AdapterView<?> parent) {
        WebView descrizioneview = (WebView) findViewById(R.id.imageorario);
        Database databaseHelper = new Database(getBaseContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fname", "novalue");
        long samerow = db.update("class", values, null, null);
        descrizioneview.loadData("", "text/html", "UTF-8");
    }

}
