package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.messedagliavr.messeapp.Fragments.AssenzeFragment;
import com.messedagliavr.messeapp.Objects.Assenza;

import java.util.ArrayList;
import java.util.HashMap;

public class TabAssenzeAdapter extends FragmentPagerAdapter {
    static Context c;
    static HashMap<Integer,Assenza> a;
    public static ArrayList<Assenza> assList;
    public static ArrayList<Assenza> rList;

    public TabAssenzeAdapter(FragmentManager fm, Context co, HashMap<Integer, Assenza> as) {
        super(fm);
        c=co;
        a=as;
    }

    @Override
    public Fragment getItem(int i) {
        assList = new ArrayList<>();
        rList = new ArrayList<>();
        for (int j = 1; j < a.size(); j++){
            Assenza ass = a.get(j);
            if(ass.isRitardo()&&i==1){
                rList.add(ass);
            } else if (!ass.isRitardo()&&i==0){
                assList.add(ass);
            }
        }
        AssenzeFragment af = new AssenzeFragment();
        Bundle args = new Bundle();
        args.putInt("tipo",i);
        if (i==0) {
            af.list=assList;
        } else {
            af.list=rList;
        }
        af.setArguments(args);
        return af;
    }

    @Override
    public int getCount() {
        return 2;
    }

}

