package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PaniniAdapter implements ListAdapter {
    ArrayList<String> names;
    ArrayList<String> prices;
    Context ctx;

    public PaniniAdapter(Context ctx, ArrayList<String> names, ArrayList<String> prices) {
        super();
        this.names=names;
        this.prices=prices;
        this.ctx = ctx;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int i) {
        View v=null;
        ViewGroup viewGroup=null;
        return getView(i, v, viewGroup);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater li = (LayoutInflater) ctx
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        view=li.inflate(R.layout.panini_item,null);
        TextView name= (TextView) view.findViewById(R.id.nomeItemPanino);
        TextView price= (TextView) view.findViewById(R.id.prezzoItemPanino);
        TextView num= (TextView) view.findViewById(R.id.numeroPanini);
        TextView hidden= (TextView) view.findViewById(R.id.position);
        name.setText(names.get(i));
        price.setText("x "+prices.get(i)+"€");
        hidden.setText(String.valueOf(i));
        if (MainActivity.numbers.get(i)!=null) {
            num.setText(String.valueOf(MainActivity.numbers.get(i)));
        }

        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
