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

public class ScontrinoAdapter implements ListAdapter {

    ArrayList<String> names;
    ArrayList<String> prices;
    ArrayList<Integer> numbers;
    ArrayList<Double> totals;
    ArrayList<Integer> coolposition;
    Context ctx;

    public ScontrinoAdapter(Context ctx, ArrayList<String> names, ArrayList<String> prices,  ArrayList<Integer> numbers, ArrayList<Double> totals,ArrayList<Integer> coolposition) {
        super();
        this.names=names;
        this.prices=prices;
        this.numbers=numbers;
        this.totals=totals;
        this.coolposition=coolposition;
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
        return coolposition.size();
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
        while(!coolposition.contains(i))i++;
        view=li.inflate(R.layout.scontrino_item,null);
        TextView name= (TextView) view.findViewById(R.id.nomeItemScontrino);
        TextView price= (TextView) view.findViewById(R.id.prezzoItemScontrino);
        TextView number= (TextView) view.findViewById(R.id.numeroItemScontrino);
        TextView total= (TextView) view.findViewById(R.id.totaleItemScontrino);
        name.setText(names.get(i));
        price.setText("x "+prices.get(i)+"€");
        number.setText(String.valueOf(numbers.get(i)));
        total.setText(String.format("%.2f", totals.get(i))+"€");
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
