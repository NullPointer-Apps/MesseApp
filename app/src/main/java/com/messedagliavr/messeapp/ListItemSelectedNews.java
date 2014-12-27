package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.webkit.WebView;
import android.widget.TextView;


public class ListItemSelectedNews extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_selected);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            //tintManager.setTintColor(Color.parseColor("#ab46e5"));
            tintManager.setTintColor(Color.parseColor("#AFAFAF"));
        }
        Intent intent = getIntent();
        String titolorw = intent.getStringExtra(MainActivity.TITLE);
        String descrizionerw ="<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" \" content=\"text/html; charset=utf-8\"></head><body style=\"background-color: transparent;\">" + intent.getStringExtra(MainActivity.DESC) +"</body></html>";        TextView titoloview = (TextView) findViewById(R.id.TitoloView);
        Spanned titolo = Html.fromHtml(titolorw);
        titoloview.setText(titolo);
        WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
        descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
        //descrizioneview.setBackgroundColor(0x00000000);

    }

}