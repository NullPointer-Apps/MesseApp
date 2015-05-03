package com.messedagliavr.messeapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messedagliavr.messeapp.Adapters.ListMaterieAdapter;
import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

public class VotiFragment extends ListFragment {
    static Context c;
    static ListMaterieAdapter lma;
    static double m;


    public static VotiFragment newInstance(Context context, int num, HashMap<Integer,Materia> v) {
        VotiFragment f = new VotiFragment();

        double somma=0;
        int size = v.size();
        Log.d("SIZE",size+"");
        c=context;

        for (Materia m : v.values()){
            if(m.haVoti("tutti",num+1)) {
                somma += m.mediaVoti("tutti", num + 1);
            } else size--;
        }

        BigDecimal bd;
        if (size!=0) {
            bd = new BigDecimal(somma / size);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
        } else {
           bd=new BigDecimal(0);
        }
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putDouble("media", bd.doubleValue() );
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null) {
            ArrayList<Materia> t = new ArrayList<>();
            for (int i = 1; i <= RegistroActivity.v.size() ; i++) {
                t.add(RegistroActivity.v.get(i));
            }
            lma = new ListMaterieAdapter(c, t, "tutti", getArguments().getInt("num"));
            m=getArguments().getDouble("media");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.voti, container, false);
        TextView media = (TextView) v.findViewById(R.id.mediaTV);
        media.setText("Media quadrimestre: " + m);
        setListAdapter(lma);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
