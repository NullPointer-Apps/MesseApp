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

public class TabAssenzeAdapter extends FragmentStatePagerAdapter {
    static Context c;
    static HashMap<Integer,Assenza> a;

    public TabAssenzeAdapter(FragmentManager fm, Context co, HashMap<Integer, Assenza> as) {
        super(fm);
        c=co;
        a=as;
    }

    @Override
    public Fragment getItem(int i) {
        return AssenzeFragment.newInstance(c, i,a);
    }

    @Override
    public int getCount() {
        return 2;
    }

}

