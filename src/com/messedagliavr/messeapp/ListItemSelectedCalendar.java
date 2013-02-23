package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

public class ListItemSelectedCalendar extends Activity {

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, calendar.class));
		setContentView(R.layout.list_item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.list_item_selected, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ical:
			Intent intent = getIntent();
			String ical="http://www.messedaglia.it/index.php/calendario/icals.icalevent/-?template=component&evid="+intent.getStringExtra(calendar.ICAL);
			Intent calendar = new Intent(Intent.ACTION_VIEW);
			calendar.setData(Uri.parse(ical));
			startActivity(calendar);
			break;

		}
		return true;
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
