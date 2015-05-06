package com.messedagliavr.messeapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messedagliavr.messeapp.Adapters.CardAssenzeAdapter;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;

public class AssenzeFragment extends Fragment {
    static Context c;
    GridLayoutManager mLayoutManager;

    public static ArrayList<Assenza> assList;
    public static ArrayList<Assenza> rList;

    static String s;
    static int l;
    static CardAssenzeAdapter caa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assList = new ArrayList<>();
        rList = new ArrayList<>();
        int i = getArguments().getInt("tipo");
        for (int j = 1; j < RegistroActivity.a.size(); j++){
            Assenza ass = RegistroActivity.a.get(j);
            if(ass.isRitardo()&&i==1){
                rList.add(ass);
            } else if (!ass.isRitardo()&&i==0){
                assList.add(ass);
            }
        }
        if (i==0) {
            s=" assenze";
            l=assList.size();
            Collections.reverse(assList);
            caa = new CardAssenzeAdapter(c, assList);
        }
        else if (i==1) {
            s=" ritardi";
            l=rList.size();
            Collections.reverse(rList);
            caa = new CardAssenzeAdapter(c,rList);
        }

    }

    public static AssenzeFragment newInstance(Context context, int i, HashMap<Integer,Assenza> a) {
        c=context;
        AssenzeFragment af = new AssenzeFragment();
        Bundle args = new Bundle();
        args.putInt("tipo", i);
        af.setArguments(args);

        return af;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.assenze, container, false);
        final TextView size = (TextView) v.findViewById(R.id.sizeTv);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.list);

        /*rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean hideToolBar = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (hideToolBar) {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

                } else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    hideToolBar = true;
                } else if (dy < -5) {
                    hideToolBar = false;
                }
            }
        });*/

        mLayoutManager = new GridLayoutManager(c,4);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(mLayoutManager);
        rv.setItemAnimator(null);

        size.setText(l + s);
        rv.setAdapter(caa);
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
