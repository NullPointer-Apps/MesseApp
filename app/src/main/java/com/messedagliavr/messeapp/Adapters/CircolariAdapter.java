package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        Circolari c = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_news, parent, false);
        }

        TextView data = (TextView) convertView.findViewById(R.id.datenews);
        TextView titolo = (TextView) convertView.findViewById(R.id.titlenews);

        data.setText(c.getData());
        titolo.setText(c.getTitolo());

        // Return the completed view to render on screen
        return convertView;
    }
}