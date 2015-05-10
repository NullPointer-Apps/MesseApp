package com.messedagliavr.messeapp.AsyncTasks;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.messedagliavr.messeapp.CalendarActivity;
import com.messedagliavr.messeapp.Parsers.XMLParser;
import com.messedagliavr.messeapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class EventParser extends AsyncTask<Void, Void, Void> {
    CalendarActivity ca;
    public EventParser(CalendarActivity ca){
        this.ca=ca;
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected Void doInBackground(Void... params) {
        String ical = "https://www.messedaglia.it/caltoxml.php?id=" + CalendarActivity.idical;
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(ical);
        if (!xml.equals("UnknownHostException")) {
            Document doc = parser.getDomElement(xml);
            NodeList nl = doc.getElementsByTagName("VEVENT");

            String[] dati = { "", "", "", "", "" };
            Element e = (Element) nl.item(0);
            dati[0] = parser.getValue(e, "SUMMARY");
            int l = parser.getValue(e, "DESCRIPTION").length();
            if (l == 0) {
                dati[1] = "Nessuna descrizione";
            } else {
                dati[1] = parser.getValue(e, "DESCRIPTION").substring(4,
                        l - 3);
            }
            dati[2] = parser.getValue(e, "LOCATION");
            dati[3] = parser.getValue(e, "DTSTART");
            dati[4] = parser.getValue(e, "DTEND");
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyyMMdd'T'HHmmss");
            Date fine;
            Date inizio;
            try {
                fine = dateFormat.parse(dati[4]);
                inizio = dateFormat.parse(dati[3]);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setType("vnd.android.cursor.item/event")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                inizio.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                fine.getTime())
                        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,
                                false)
                        .putExtra(CalendarContract.Events.TITLE, dati[0])
                        .putExtra(CalendarContract.Events.DESCRIPTION, dati[1])
                        .putExtra(CalendarContract.Events.EVENT_LOCATION,
                                dati[2] + " A. Messedaglia");
                ca.startActivity(intent);
            } catch (java.text.ParseException e1) {
                e1.printStackTrace();
            } catch (Exception e2) {
                e2.printStackTrace();
                Toast.makeText(ca, R.string.noapilevel,
                        Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

}