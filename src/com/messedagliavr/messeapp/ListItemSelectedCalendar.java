package com.messedagliavr.messeapp;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
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
		ProgressDialog mDialog = new ProgressDialog(ListItemSelectedCalendar.this);
		mDialog.setMessage("Caricamento...");
		mDialog.setCancelable(false);
		setContentView(R.layout.list_item_selected);
		Intent intent = getIntent();
		String titolorw = intent.getStringExtra(calendar.TITLE);
		String descrizionerw = intent.getStringExtra(calendar.DESC);
		TextView titoloview = (TextView) findViewById(R.id.TitoloView);
		Spanned titolo = Html.fromHtml(titolorw);
		titoloview.setText(titolo);
		WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
		try {
			Method method = View.class.getMethod("setLayerType", int.class,
					Paint.class);
			method.invoke(descrizioneview, View.LAYER_TYPE_SOFTWARE, null);
			descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
		} catch (Exception e) {
			Log.e("RD", "Hardware Acceleration not supported on API "
					+ android.os.Build.VERSION.SDK_INT, e);
		}
		mDialog.dismiss();
	}

}
