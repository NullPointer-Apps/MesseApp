package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;

public class ListItemSelectedCalendar extends Activity {

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, calendar.class));
		setContentView(R.layout.list_item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_item_selected);
		Intent intent = getIntent();
		String titolorw = intent.getStringExtra(calendar.TITLE);
		String descrizionerw = intent.getStringExtra(calendar.DESC);
		TextView titoloview = (TextView) findViewById(R.id.TitoloView);
		Spanned titolo = Html.fromHtml(titolorw);
		titoloview.setText(titolo);
		WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
			descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
	}

}