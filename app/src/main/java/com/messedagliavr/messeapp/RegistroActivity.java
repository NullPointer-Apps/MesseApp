package com.messedagliavr.messeapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.messedagliavr.messeapp.Adapters.CircolariAdapter;
import com.messedagliavr.messeapp.Adapters.TabAssenzeAdapter;
import com.messedagliavr.messeapp.Adapters.TabVotiAdapter;
import com.messedagliavr.messeapp.AsyncTasks.SAssenze;
import com.messedagliavr.messeapp.AsyncTasks.SCircolari;
import com.messedagliavr.messeapp.AsyncTasks.SVoti;
import com.messedagliavr.messeapp.Dialogs.CircolariDialog;
import com.messedagliavr.messeapp.Dialogs.LegendaVotiDialog;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.Objects.Circolari;
import com.messedagliavr.messeapp.Objects.Materia;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class RegistroActivity extends AppCompatActivity {

    ViewPager mViewPager;
    ProgressDialog mDialog;
    int section = 1;

    public static HttpResponse httpResponse;
    public static DefaultHttpClient httpClient = new DefaultHttpClient();

    public long t;

    public static HashMap<Integer, Materia> v;
    public static HashMap<Integer, Assenza> a;
    public static HashMap<Integer, Circolari> c;

    public void votiBtn(View v) throws IOException {
        //scarico voti
        SVoti sv = new SVoti(this);
        sv.execute();
    }

    public void assenzeBtn(View v) throws IOException {
        //scarico Assenze
        SAssenze sa = new SAssenze(this);
        sa.execute();
    }

    @Override
    public void onBackPressed() {
        switch(section){
            case 1:
            case 6:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                getSupportActionBar().removeAllTabs();
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                section=1;
                setContentView(R.layout.menu_registro2);
                break;
            case 7:
                section=6;
                setContentView(R.layout.circolari);
                setUpCircolari(c);
                break;
        }
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        if(section==2||section==3){
            getMenuInflater().inflate(R.menu.voti_registro_menu, menu);
        } else getMenuInflater().inflate(R.menu.registro_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                switch(section){
                    case 1:
                        NavUtils.navigateUpFromSameTask(this);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        onBackPressed();
                        break;
                }
                return true;
            case R.id.ftosite:
                String user = getIntent().getStringExtra("USER");
                String password = getIntent().getStringExtra("PWD");
                Intent voti = new Intent(Intent.ACTION_VIEW);
                if(user.contains("@")) {
                    voti.setData(Uri
                            .parse("https://web.spaggiari.eu/home/app/default/login_email.php?custcode=VRLS0003&login="
                                    + user + "&password=" + password));
                } else {
                    voti.setData(Uri
                            .parse("https://web.spaggiari.eu/home/app/default/login.php?custcode=VRLS0003&login="
                                    + user + "&password=" + password));
                }
                startActivity(voti);
                break;
            case R.id.help:
                LegendaVotiDialog lvd = new LegendaVotiDialog();
                lvd.show(getSupportFragmentManager(),"Help voti");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save state of your activity to outState
        outState.putInt("Section", section);
    }

    public void setUpVoti(HashMap<Integer, Materia> v){
        final ActionBar actionBar;
        ActionBar.TabListener tabListener;

        RegistroActivity.v = v;
        setContentView(R.layout.voti_parent);
        section=2;
        supportInvalidateOptionsMenu();

        TabVotiAdapter tabAdapter =
                new TabVotiAdapter(
                        getSupportFragmentManager(),this, v);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        getSupportActionBar().setSelectedNavigationItem(position);
                    }
                });
        mViewPager.setAdapter(tabAdapter);

        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(
                actionBar.newTab()
                        .setText("Quad I")
                        .setTabListener(tabListener));
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Quad II")
                        .setTabListener(tabListener));
    }

    public void setUpAssenze(HashMap<Integer, Assenza> a){
        final ActionBar actionBar;
        ActionBar.TabListener tabListener;
        RegistroActivity.a=a;
        setContentView(R.layout.voti_parent);
        section=4;
        supportInvalidateOptionsMenu();

        actionBar = getSupportActionBar();

        TabAssenzeAdapter tabAdapter =
                new TabAssenzeAdapter(
                        getSupportFragmentManager(),this, a);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        mViewPager.setAdapter(tabAdapter);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition(),true);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        actionBar.addTab(
                actionBar.newTab()
                        .setText("Assenze")
                        .setTabListener(tabListener));
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Ritardi")
                        .setTabListener(tabListener));
    }

    public void setUpCircolari(HashMap<Integer, Circolari> c){

        setContentView(R.layout.circolari);
        section=6;
        supportInvalidateOptionsMenu();

        RegistroActivity.c=c;

        ListView cs = (ListView) findViewById(R.id.circolari_list);
        ArrayList<Circolari> alc = new ArrayList<>();
        for (int j = 1; j < c.size(); j++){
            alc.add(c.get(j));

        }
        cs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CircolariDialog cd = new CircolariDialog();
                Circolari cc = RegistroActivity.c.get(i + 1);
                Bundle data = new Bundle();
                data.putString("tit",cc.getTitolo());
                data.putString("mex", cc.getTesto());
                data.putBoolean("vis", cc.getAllegato());
                data.putString("id", cc.getId());
                cd.setArguments(data);
                cd.show(getSupportFragmentManager(),"circolare");
            }
        });
        cs.setAdapter(new CircolariAdapter(this, alc));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registro));
        SharedPreferences sharedpreferences = getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
        sharedpreferences.edit().putLong("lastLogin",new Date().getTime()).commit();
        if (savedInstanceState!=null) {
            section = savedInstanceState.getInt("Section", 0);
            switch (section) {
                case 1:
                    section=1;
                    setContentView(R.layout.menu_registro2);
                    break;
                case 2:
                case 3:
                    setContentView(R.layout.voti_parent);
                    setUpVoti(v);
                    break;
                case 4:
                case 5:
                    setContentView(R.layout.voti_parent);
                    setUpAssenze(a);
                    break;
                case 6:
                    setContentView(R.layout.circolari);
                    setUpCircolari(c);
                    break;
            }
        } else if (getIntent().getIntExtra("circolari",0)==0){
            setContentView(R.layout.menu_registro2);
            section = 1;
        } else {
            getSupportActionBar().setTitle(getString(R.string.circolari));
            SCircolari sc = new SCircolari(this);
            sc.execute();
            section = 6;
        }
    }

}
