package com.messedagliavr.messeapp;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.messedagliavr.messeapp.Adapters.TabAssenzeAdapter;
import com.messedagliavr.messeapp.Adapters.TabVotiAdapter;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.Objects.Voto;
import com.messedagliavr.messeapp.Utilities.SystemBarTintManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
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

    public void votiBtn(View v) throws IOException {
        //scarico voti
        SVoti sv = new SVoti();
        sv.execute();
    }

    public void assenzeBtn(View v) throws IOException {
        //scarico Assenze
        SAssenze sa = new SAssenze();
        sa.execute();
    }

    @Override
    public void onBackPressed() {
        switch(section){
            case 1:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                getSupportActionBar().removeAllTabs();
                getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                section=1;
                setContentView(R.layout.menu_registro);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.registro_menu, menu);
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

        }
        return super.onOptionsItemSelected(item);
    }



    class SVoti extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(RegistroActivity.this, null,
                    "Aggiornamento voti in corso", true, true,
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            SVoti.this.cancel(true);
                        }
                    });
        }

        protected Void doInBackground(Void... voids) {

            try {
                v=scaricaVoti(leggiPagina("https://web.spaggiari.eu/cvv/app/default/genitori_voti.php").getElementById("data_table_2"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.dismiss();
            setContentView(R.layout.voti_parent);
            section=2;
            supportInvalidateOptionsMenu();
            setUpVoti();
        }
    }

    class SAssenze extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(RegistroActivity.this, null,
                    "Aggiornamento assenze in corso", true, true,
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            SAssenze.this.cancel(true);
                        }
                    });
        }

        protected Void doInBackground(Void... voids) {
            try {
                a=scaricaAssenze(leggiPagina("https://web.spaggiari.eu/tic/app/default/consultasingolo.php#calendario").getElementById("skeda_calendario"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.dismiss();
            setContentView(R.layout.voti_parent);
            section=4;
            supportInvalidateOptionsMenu();
            setUpAssenze();
        }
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    //parsing della pagina
    public Document leggiPagina(String url)
    {
        try {
            HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader("If-Modified-Since", DateFormat.format("Y-m-d h-M-s", new Date()).toString());
            InputStream inputStream;
            inputStream = httpClient.execute(httpGet).getEntity().getContent();
            Document s1 = Jsoup.parse(convertStreamToString(inputStream), "UTF-8", Parser.xmlParser());
            inputStream.close();
            return s1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<Integer,Materia> scaricaVoti(Element html) throws IOException {
        v = new HashMap<>();
        int j=0,k=0,nv=0;
        Materia materia = new Materia();
        boolean isRec=false;
        boolean isTest=false;

        for (Element tr : html.select("tr")) {
            for (Element td : tr.select("td")) {

                //ha classe font-size-14 (Materie)
                if (td.text().equals("Test")){
                    isTest=true;
                    isRec=false;
                    continue;
                } else if (td.text().equals("Prove recupero")) {
                    isRec = true;
                    isTest = false;
                    continue;
                }

                if (td.hasClass("font_size_14") && td.hasText()) {
                    isRec=false;
                    isTest=false;
                    materia = new Materia(td.text());
                    k++;
                    System.out.println(td.text()+k);
                    v.put(k,materia);
                    j = 0;
                    nv=0;
                } else {
                    j++;
                    if (td.hasText()){
                        nv++;
                        Voto voto = new Voto();
                        for (Element span : td.getElementsByTag("span")) {
                            if (span.hasClass("voto_data") && span.hasText()) {
                                voto.setData(span.text());
                            }
                        }
                        for (Element p : td.getElementsByTag("p")) {
                            if (p.hasText() && p.hasClass("s_reg_testo")) {
                                if (isRec){
                                    voto.setTipo("Recupero");
                                } else if (isTest){
                                    voto.setTipo("Test");
                                } else if ((j <= 5) || (j > 15 && j <= 20))
                                    voto.setTipo("Scritto");
                                else if ((j > 5 && j <= 10) || (j > 20 || j <= 25))
                                    voto.setTipo("Orale");
                                else voto.setTipo("Pratico");
                                if (j > 15&&!isRec) {
                                    voto.setQuadrimestre(2);
                                } else {
                                    voto.setQuadrimestre(1);
                                }
                                voto.setVoto(p.text());
                                materia.addVoto(nv, voto);
                            }
                        }
                    }
                }
            }
        }
        return v;
    }

    public HashMap<Integer,Assenza> scaricaAssenze(Element html) throws IOException {
        a = new HashMap<>();
        String mese, title="",tipo;

        int n=0,i;
        for (Element tr : html.select("tr")){
            if (tr.children().size()>2){
                Element td = tr.child(1);
                mese = td.text();
                if (mese.equals("Mese")) continue;
                td=td.nextElementSibling();
                for (i=1; td!=null; i++) {
                    tipo = td.text().trim();
                    if (tipo.length()>0&&(tipo.contains("A") || tipo.contains("R"))) {
                        Log.i("ASS","trovata assenza:"+"Tipo="+tipo+" Mese="+mese+" Giorno="+i);
                        Assenza ass = new Assenza();
                        ass.setMese(mese.trim());
                        ass.setTipo(String.valueOf(tipo.charAt(tipo.length()-1)));
                        ass.setGiorno(i);
                        for (Element div : td.select("div")) title = div.attr("title");
                        if (tipo.contains("R")) {
                            ass.setTipoR(title.substring(23));
                        }
                        if (tipo.contains("NG")){
                            ass.setGiustificata(false);
                        }
                        n++;
                        a.put(n, ass);
                    }
                    td=td.nextElementSibling();
                }
            }
        }
        return a;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save state of your activity to outState
        outState.putInt("Section",section);
    }

    public void setUpVoti(){
        final ActionBar actionBar;
        ActionBar.TabListener tabListener;
        TabVotiAdapter tabAdapter =
                new TabVotiAdapter(
                        getSupportFragmentManager(),this,v);
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

    public void setUpAssenze(){
        final ActionBar actionBar;
        ActionBar.TabListener tabListener;

        TabAssenzeAdapter tabAdapter =
                new TabAssenzeAdapter(
                        getSupportFragmentManager(),this,a);
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
                        .setText("Assenze")
                        .setTabListener(tabListener));
        actionBar.addTab(
                actionBar.newTab()
                        .setText("Ritardi")
                        .setTabListener(tabListener));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registro));
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            //tintManager.setTintColor(Color.parseColor("#ab46e5"));
            tintManager.setTintColor(Color.parseColor("#AFAFAF"));
        }
        if (savedInstanceState!=null) {
            section = savedInstanceState.getInt("Section", 0);
            switch (section) {
                case 1:
                    section=1;
                    setContentView(R.layout.menu_registro);
                    break;
                case 2:
                case 3:
                    setContentView(R.layout.voti_parent);
                    setUpVoti();
                    break;
                case 4:
                case 5:
                    setContentView(R.layout.voti_parent);
                    setUpAssenze();
                    break;
            }
        } else {
            setContentView(R.layout.menu_registro);
            section = 1;
        }
    }

}
