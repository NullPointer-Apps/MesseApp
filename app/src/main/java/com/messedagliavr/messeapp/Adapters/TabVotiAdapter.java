package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.messedagliavr.messeapp.Fragments.VotiFragment;
import com.messedagliavr.messeapp.Objects.Materia;

import java.util.HashMap;

public class TabVotiAdapter extends FragmentStatePagerAdapter {
    static Context c;
    static HashMap<Integer, Materia> v;

    public TabVotiAdapter(FragmentManager fm, Context co, HashMap<Integer, Materia> vo) {
        super(fm);
        c = co;
        v = vo;
    }

    @Override
    public Fragment getItem(int i) {
        return VotiFragment.newInstance(c, i, v);
    }

    @Override
    public int getCount() {
        return 2;
    }

}

