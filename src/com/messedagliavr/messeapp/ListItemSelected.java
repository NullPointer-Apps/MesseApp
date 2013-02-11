package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public class ListItemSelected extends Activity {

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, news.class));
		setContentView(R.layout.list_item);
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
		Spanned descrizione = Html.fromHtml(descrizionerw);

		titoloview.setText(titolo);
		TextView descrizioneview = (TextView) findViewById(R.id.DescrizioneView);

		descrizioneview.setText(descrizione);

	}

}
