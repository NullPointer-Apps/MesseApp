package com.messedagliavr.messeapp;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.WebView;

public class voti extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voti);
		Database databaseHelper = new Database(getBaseContext());
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String[] columns = { "username", "password" };
		Cursor query = db.query("settvoti", // The table to query
				columns, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);
		query.moveToFirst();
		String user=query.getString(query.getColumnIndex("username"));
		String password=query.getString(query.getColumnIndex("password"));
		query.close();
		db.close();
		WebView votiview = (WebView) findViewById(R.id.voti);
		votiview.getSettings().setJavaScriptEnabled(true);
		votiview.getSettings().setLoadWithOverviewMode(true);
		votiview.getSettings().setUseWideViewPort(true);
		votiview
				.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		votiview.setScrollbarFadingEnabled(true);
		votiview.getSettings().setBuiltInZoomControls(true);
		votiview.loadData(getHtml("html1")+user+getHtml("html2")+password+getHtml("html3"), "text/html", "UTF-8");
	}
	
    public String getHtml(String file) {
        // load text
        try {
            // get input stream for text
            InputStream is = this.getAssets().open(file+".txt");
            // check size
            int size = is.available();
            // create buffer for IO
            byte[] buffer = new byte[size];
            // get data to buffer
            is.read(buffer);
            // close stream
            is.close();
            return new String(buffer);
            // set result to TextView
        }
        catch (IOException ex) {
            return "";
        }
    }

}
