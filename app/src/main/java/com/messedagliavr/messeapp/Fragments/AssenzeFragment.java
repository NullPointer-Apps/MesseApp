package com.messedagliavr.messeapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messedagliavr.messeapp.Adapters.CardAssenzeAdapter;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.R;

import java.util.ArrayList;

import java.util.Collections;

public class AssenzeFragment extends Fragment {
    static Context c;
    public ArrayList<Assenza> list;
    GridLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.assenze, container, false);
        TextView size = (TextView) v.findViewById(R.id.sizeTv);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.list);

        mLayoutManager = new GridLayoutManager(c,4);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(null);
        Collections.reverse(list);

        if (getArguments().getInt("tipo")==0) size.setText(list.size() + " assenze");
        else size.setText(list.size() + " ritardi");
        rv.setAdapter(new CardAssenzeAdapter(c,list));
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tipo",getArguments().getInt("tipo"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
