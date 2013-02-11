package com.messedagliavr.messeapp;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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
		titoloview.setText(titolo);
		WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
		//descrizioneview.setBackgroundColor(0x00FFFFFF);
		try {
			Method method = View.class.getMethod("setLayerType", int.class,
					Paint.class);
			method.invoke(descrizioneview, View.LAYER_TYPE_SOFTWARE, null);
			descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
		} catch (Exception e) {
			Log.e("RD", "Hardware Acceleration not supported on API "
					+ android.os.Build.VERSION.SDK_INT, e);
		}

	}

}
