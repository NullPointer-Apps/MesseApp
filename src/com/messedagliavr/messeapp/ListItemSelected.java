package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ListItemSelected extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("provaclick");
		Intent intent = getIntent();
		String titolo = intent.getStringExtra(news.TITLE);
		String descrizione = intent.getStringExtra(news.DESC);
		TextView titoloview =(TextView) findViewById(R.id.TitoloView);
		titoloview.setText(titolo);
		TextView descrizioneview =(TextView) findViewById(R.id.DescrizioneView);
		descrizioneview.setText(descrizione);
		setContentView(R.layout.list_item_selected);
	}

}
