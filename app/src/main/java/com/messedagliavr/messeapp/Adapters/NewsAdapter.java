package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.messedagliavr.messeapp.R;

import java.util.ArrayList;

/**
 * Created by Simone on 01/03/2015.
 */
public class NewsAdapter extends ArrayAdapter<Spanned> {
    private ArrayList<Spanned> date;
    private ArrayList<Spanned> titoli;
	private Context context;


    public NewsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public NewsAdapter(Context context, int resource, ArrayList<Spanned> titoli, ArrayList<Spanned> date) {
        super(context, resource,titoli);
        this.date = date;
        this.titoli = titoli;
		this.context=context;
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
            TextView tt1 = (TextView) v.findViewById(R.id.titlenews);

            if (tt1 != null) {
                tt1.setText(titoli.get(position));
            }
            if (tt != null && date!=null) {

                tt.setText(date.get(position));
            }

        }

        return v;

    }
}
