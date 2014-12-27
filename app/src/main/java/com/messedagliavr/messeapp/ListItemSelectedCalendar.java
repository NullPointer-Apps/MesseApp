package com.messedagliavr.messeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ListItemSelectedCalendar extends ActionBarActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_item_selected, menu);
        return true;
    }

    public String idical = null;

    public class eventparser extends AsyncTask<Void, Void, String[]> {
        @SuppressLint("InlinedApi")
        @Override
        protected String[] doInBackground(Void... params) {
            String ical = "http://www.messedaglia.it/caltoxml.php?id=" + idical;
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(ical);
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName("VEVENT");

            String[] dati = { "", "", "", "", "" };
            Element e = (Element) nl.item(0);
            dati[0] = parser.getValue(e, "SUMMARY");
            int l = parser.getValue(e, "DESCRIPTION").length() - 3;
            dati[1] = parser.getValue(e, "DESCRIPTION").substring(4, l);
            dati[2] = parser.getValue(e, "LOCATION");
            dati[3] = parser.getValue(e, "DTSTART");
            dati[4] = parser.getValue(e, "DTEND");
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyyMMdd'T'HHmmss", Locale.US);
            dateFormat.setLenient(false);
            Date fine = null;
            Date inizio = null;
            try {
                fine = dateFormat.parse(dati[4].toString());
                inizio = dateFormat.parse(dati[3].toString());
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setType("vnd.android.cursor.item/event")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                inizio.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                fine.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
                        .putExtra(Events.TITLE, dati[0])
                        .putExtra(Events.DESCRIPTION, dati[1])
                        .putExtra(Events.EVENT_LOCATION,
                                dati[2] + " A. Messedaglia");
                startActivity(intent);
            } catch (java.text.ParseException e1) {
                e1.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
                Toast.makeText(ListItemSelectedCalendar.this,
                        R.string.noapilevel, Toast.LENGTH_LONG).show();
            }
            return dati;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ical:
                if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 14) {
                    Toast.makeText(ListItemSelectedCalendar.this,
                            R.string.noapilevel, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = getIntent();
                    String ical = intent.getStringExtra(MainActivity.ICAL);
                    int l = ical.length() - 5;
                    idical = ical.substring(3, l);
                    new eventparser().execute();
                }
                break;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

        }
        return true;
    }

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
        String descrizionerw ="<!DOCTYPE html><head><meta http-equiv=\"Content-Type\" \" content=\"text/html; charset=utf-8\"></head><body style=\"background-color: transparent;\">" + intent.getStringExtra(MainActivity.DESC) +"</body></html>";
        TextView titoloview = (TextView) findViewById(R.id.TitoloView);
        Spanned titolo = Html.fromHtml(titolorw);
        titoloview.setText(titolo);
        WebView descrizioneview = (WebView) findViewById(R.id.DescrizioneView);
        descrizioneview.loadData(descrizionerw, "text/html", "UTF-8");
        //descrizioneview.setBackgroundColor(0x00000000);

    }

}