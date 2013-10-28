package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class ListItemSelectedNews extends Activity {
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item_selected);
		Intent intent = getIntent();
		String titolorw = intent.getStringExtra(news.TITLE);
		String descrizionerw = intent.getStringExtra(news.DESC);
		TextView titoloview = (TextView) findViewById(R.id.TitoloView);
		Spanned titolo = Html.fromHtml(titolorw);
		titoloview.setText(titolo);
		WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
		descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
	}

}
