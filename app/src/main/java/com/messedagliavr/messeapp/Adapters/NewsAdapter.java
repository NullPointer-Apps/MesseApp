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

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<Spanned> {
    private ArrayList<Spanned> date;
    private ArrayList<Spanned> titoli;
	private Context context;


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
            TextView tt2 = (TextView) v.findViewById(R.id.datenews2);
            TextView tt1 = (TextView) v.findViewById(R.id.titlenews);

            if (tt1 != null) {
                tt1.setText(titoli.get(position));
            }
            boolean f=false;
            String day="";
            String month="";

            if (tt != null && date!=null) {
                String s = date.get(position).toString();
                for (int i= 0; i<s.length();i++){
                    if (s.charAt(i)==','){
                        if (!f){
                            month+=s.charAt(i+2);
                            month+=s.charAt(i+3);
                            month+=s.charAt(i+4);
                            f=true;
                        } else {
                            if (s.charAt(i-2)!=' ') day+=s.charAt(i-2);
                            else day+='0';
                            day+=s.charAt(i-1);
                        }
                    }
                }

                tt.setTextColor(Color.rgb(114, 177, 214));
                tt.setText(day);
                tt2.setText(month.toUpperCase());
                tt2.setTextColor(Color.rgb(114, 177, 214));
            }
        }
        return v;

    }
}
