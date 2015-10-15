package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.messedagliavr.messeapp.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewsAdapter extends ArrayAdapter<Spanned> {
    private ArrayList<Spanned> date;
    private ArrayList<Spanned> titoli;
    private Context context;


    public NewsAdapter(Context context, int resource, ArrayList<Spanned> titoli, ArrayList<Spanned> date) {
        super(context, resource, titoli);
        this.date = date;
        this.titoli = titoli;
        this.context = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.item_news, null);
        }

        Spanned p = getItem(position);

        if (p != null) {

            TextView tt = (TextView) v.findViewById(R.id.datenews);
            TextView tt2 = (TextView) v.findViewById(R.id.datenews2);
            TextView tt1 = (TextView) v.findViewById(R.id.titlenews);

            if (tt1 != null) {
                tt1.setText(titoli.get(position));
            }
            boolean f = false;
            Locale currentLocale = context.getResources().getConfiguration().locale;
            DateFormat parserDatePub = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
            Date data = null;
            try {
                data = parserDatePub.parse(date.get(position).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(data);
            String day = Integer.toString(calFrom.get(Calendar.DAY_OF_MONTH));
            String month = new DateFormatSymbols().getMonths()[calFrom.get(Calendar.MONTH)].toUpperCase().substring(0, 3);

            if (tt != null && data != null) {
                tt.setTextColor(Color.rgb(114, 177, 214));
                tt.setText(day);
                tt2.setTextColor(Color.rgb(114, 177, 214));
                tt2.setText(month);
            }
        }
        return v;

    }
}
