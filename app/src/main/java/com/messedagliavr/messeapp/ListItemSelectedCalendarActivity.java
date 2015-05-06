package com.messedagliavr.messeapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;


public class ListItemSelectedCalendarActivity extends AppCompatActivity {
    static Window window;
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.eventi));
        Intent intent = getIntent();
        String titolorw = intent.getStringExtra("title");
        String descrizionerw ="<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" \" content=\"text/html; charset=utf-8\"></head><body>" + intent.getStringExtra("description") +"</body></html>";
        TextView titoloview = (TextView) findViewById(R.id.TitoloView);
        Spanned titolo = Html.fromHtml(titolorw);
        titoloview.setText(titolo);
        WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
        descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
        descrizioneview.setBackgroundColor(Color.parseColor("#eeeeee"));
        if (Build.VERSION.SDK_INT >= 21) {
            window= getWindow();
            window.setEnterTransition(new Slide(Gravity.RIGHT));
            window.setExitTransition(new Slide(Gravity.LEFT));
        }

    }

}