package com.messedagliavr.messeapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.messedagliavr.messeapp.Adapters.CardAssenzeAdapter;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AssenzeFragment extends Fragment {
    static Context c;
    static int type;
    static HashMap<Integer,Assenza> assenze;
    GridLayoutManager mLayoutManager;
    CardAssenzeAdapter caa;
    ArrayList<Assenza> list;


    public static AssenzeFragment newInstance(Context context, int t, HashMap<Integer,Assenza> a) {
        AssenzeFragment f = new AssenzeFragment();

        c=context;
        assenze=a;
        type= t;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Assenza> assList = new ArrayList<>();
        ArrayList<Assenza> rList=new ArrayList<>();
        for (int i = 1; i < assenze.size(); i++){
            Assenza a = assenze.get(i);
            if(a.isRitardo()&&type==1){
                rList.add(a);
            } else if (!a.isRitardo()&&type==0)assList.add(a);
        }
        if (type==0){
            list=assList;
        } else {
            list=rList;
        }
        caa=new CardAssenzeAdapter(c, list);
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
        rv.setAdapter(caa);
        size.setText(list.size()+" assenze.");
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
