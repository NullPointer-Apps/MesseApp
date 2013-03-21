package com.messedagliavr.messeapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

public class voti extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voti);
		new registro().execute();

	}
	
	@Override
	public void onBackPressed() {
		Intent main = new Intent(this, MainActivity.class);
		main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(main);
	}
	
	String html = null;
	
public class registro extends AsyncTask<Void,Void,Void> {
	protected Void doInBackground(Void... params) {
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
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("https://web.spaggiari.eu/home/app/default/login.php");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("custcode", "VRLS0003"));
	        nameValuePairs.add(new BasicNameValuePair("login", user));
	        nameValuePairs.add(new BasicNameValuePair("password", password));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        InputStream is = response.getEntity().getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        StringBuilder sb = new StringBuilder();
	        String line = null;

	        while ((line = reader.readLine()) != null) {
	            sb.append(line);
	        }

	        is.close();
	        html = sb.toString();  
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return null;
		
	}
	public void onPostExecute (Void...params) {
		WebView votiview = (WebView) findViewById(R.id.voti);
		votiview.getSettings().setJavaScriptEnabled(true);
		votiview.getSettings().setLoadWithOverviewMode(true);
		votiview.getSettings().setUseWideViewPort(true);
		votiview
				.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		votiview.setScrollbarFadingEnabled(true);
		votiview.getSettings().setBuiltInZoomControls(true);
		votiview.loadData(html, "text/html", "UTF-8");
	}
}

}
