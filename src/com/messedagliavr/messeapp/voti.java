package com.messedagliavr.messeapp;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class voti extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voti);
		// new registro().execute();
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
		String user = query.getString(query.getColumnIndex("username"));
		String password = query.getString(query.getColumnIndex("password"));
		query.close();
		db.close();
		WebView votiview = (WebView) findViewById(R.id.voti);
		votiview.getSettings().setJavaScriptEnabled(true);
		votiview.getSettings().setLoadWithOverviewMode(true);
		votiview.getSettings().setUseWideViewPort(true);
		votiview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		votiview.setScrollbarFadingEnabled(true);
		votiview.getSettings().setBuiltInZoomControls(true);
		votiview.setWebViewClient(new HelloWebViewClient());
		votiview.loadDataWithBaseURL(
				"https://web.spaggiari.eu/home/app/default/",
				getHtml("html1") + user + getHtml("html2") + password
						+ getHtml("html3"), "text/html", "UTF-8", null);

	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	public String getHtml(String file) {
		try {
			InputStream is = this.getAssets().open(file + ".txt");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			return new String(buffer);
		} catch (IOException ex) {
			return "";
		}
	}

	@Override
	public void onBackPressed() {
		Intent main = new Intent(this, MainActivity.class);
		main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(main);
	}
}
