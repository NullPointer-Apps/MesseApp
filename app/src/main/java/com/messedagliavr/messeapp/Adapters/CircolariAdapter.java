package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.messedagliavr.messeapp.Objects.Circolari;
import com.messedagliavr.messeapp.R;

import java.util.ArrayList;

public class CircolariAdapter extends ArrayAdapter<Circolari> {


    public CircolariAdapter(Context context, ArrayList<Circolari> circolari) {
        super(context, 0, circolari);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        Circolari c = getItem(position);

        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_news2, parent, false);
        }

        TextView tt = (TextView) v.findViewById(R.id.datenews);
        TextView tt2 = (TextView) v.findViewById(R.id.datenews2);
        TextView tt1 = (TextView) v.findViewById(R.id.titlenews);

        String day;
        String month="";
        String s = c.getData();
        day = s.substring(0,2);
        switch(s.substring(3,5)){
            case "01":
                month="GEN";
                break;
            case "02":
                month="FEB";
                break;
            case "03":
                month="MAR";
                break;
            case "04":
                month="APR";
                break;
            case "05":
                month="MAG";
                break;
            case "06":
                month="GIU";
                break;
            case "07":
                month="LUG";
                break;
            case "08":
                month="AGO";
                break;
            case "09":
                month="SET";
                break;
            case "10":
                month="OTT";
                break;
            case "11":
                month="NOV";
                break;
            case "12":
                month="DEC";
                break;
        }
        tt.setText(day);
        tt2.setText(month);
        tt.setTextColor(Color.rgb(114, 177, 214));
        tt2.setTextColor(Color.rgb(114, 177, 214));
        tt1.setText(c.getTitolo());

        // Return the completed view to render on screen
        return v;
    }
}