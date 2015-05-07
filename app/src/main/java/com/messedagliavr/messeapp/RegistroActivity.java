package com.messedagliavr.messeapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.messedagliavr.messeapp.Adapters.CircolariAdapter;
import com.messedagliavr.messeapp.Adapters.TabAssenzeAdapter;
import com.messedagliavr.messeapp.Adapters.TabVotiAdapter;
import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.Objects.Circolari;
import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.Objects.Voto;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class RegistroActivity extends AppCompatActivity {

    ViewPager mViewPager;
    ProgressDialog mDialog;
    ProgressDialog mProgressDialog;
    int section = 1;

    public static HttpResponse httpResponse;
    public static DefaultHttpClient httpClient = new DefaultHttpClient();

    public long t;

    public static HashMap<Integer, Materia> v;
    public static HashMap<Integer, Assenza> a;
    public static HashMap<Integer, Circolari> c;
    public String idCircolare=null;
    public String nomeCircolare=null;

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
                setContentView(R.layout.menu_registro);
                break;
            case 7:
                section=6;
                setContentView(R.layout.circolari);
                setUpCircolari();
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

    class SCircolari extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute() {
            getSupportActionBar().setTitle(getString(R.string.circolari));
            mDialog = ProgressDialog.show(RegistroActivity.this, null,
                    "Aggiornamento circolari in corso", true, true,
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            SCircolari.this.cancel(true);
                        }
                    });
        }

        protected Void doInBackground(Void... voids) {
            try {
                //lista circolari
                c=scaricaCircolari(leggiPagina("https://web.spaggiari.eu/sif/app/default/bacheca_utente.php").getElementById("data_table"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mDialog.dismiss();
            setContentView(R.layout.circolari);
            section=6;
            supportInvalidateOptionsMenu();
            setUpCircolari();
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
                        Log.i("ASS", "trovata assenza:" + "Tipo=" + tipo + " Mese=" + mese + " Giorno=" + i);
                        Assenza ass = new Assenza();
                        ass.setMese(mese.trim());
                        ass.setTipo(String.valueOf(tipo.charAt(tipo.length() - 1)));
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

    public HashMap<Integer,Circolari> scaricaCircolari(Element html) throws IOException {
        c = new HashMap<>();
        int i=0;
        for (Element a : html.select("a.specifica")){
            Circolari cn = new Circolari();
            i++;
            cn.setData(a.parent().previousElementSibling().select("div.font_size_12").first().ownText());
            cn.setId(a.attr("comunicazione_id"));
            Element dettagli = leggiPagina("https://web.spaggiari.eu/sif/app/default/bacheca_comunicazione.php?action=risposta_com&com_id=" + cn.getId());
            Element divs = dettagli.select("div").first();
            cn.setTitolo(divs.ownText());
            divs.nextElementSibling();
            cn.setTesto(divs.ownText());
            divs.nextElementSibling();
            if (dettagli.select("div.hidden").size() > 3) {
                cn.setAllegato(false);
            } else {
                cn.setAllegato(true);
            }
            System.out.println("Trovata circolare - ID:" + cn.getId() + " Data:" + cn.getData() + " Titolo:" + cn.getTitolo() + " Testo:" + cn.getTesto());
            c.put(i,cn);
        }

        return c;
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
        actionBar = getSupportActionBar();

        TabAssenzeAdapter tabAdapter =
                new TabAssenzeAdapter(
                        getSupportFragmentManager(),this,a);
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

    public void setUpCircolari(){
        ListView cs = (ListView) findViewById(R.id.circolari_list);
        ArrayList<Circolari> alc = new ArrayList<>();
        for (int j = 1; j < c.size(); j++){
            alc.add(c.get(j));

        }
        cs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setContentView(R.layout.circolare);
                section = 7;
                Circolari cc = c.get(i + 1);
                TextView titolo = (TextView) findViewById(R.id.cTitle);
                TextView testo = (TextView) findViewById(R.id.cText);
                Button dwn = (Button) findViewById(R.id.cScarica);
                titolo.setText(cc.getTitolo());
                testo.setText(cc.getTesto());
                if (cc.getAllegato()) {
                    dwn.setVisibility(View.VISIBLE);
                    idCircolare = cc.getId();
                    nomeCircolare = cc.getTitolo();
                }
                else {
                    dwn.setVisibility(View.INVISIBLE);
                    dwn.setClickable(false);
                }
            }
        });
        cs.setAdapter(new CircolariAdapter(this, alc));
    }

    public void scaricaAllegato(View v) {
        final downloadAllegato downloadTask = new downloadAllegato(RegistroActivity.this);
        downloadTask.execute();


    }

    private class downloadAllegato extends AsyncTask<Void, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public downloadAllegato(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            String fileName = null;
            HttpGet httpget = new HttpGet("https://web.spaggiari.eu/sif/app/default/bacheca_utente.php?action=file_download&com_id="+idCircolare);
            HttpResponse response = null;
            try {
                response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                long fileLength = 0;
                if (entity != null) {
                    fileLength = entity.getContentLength();
                    input = entity.getContent();
                }

                File directory = new File(Environment.getExternalStorageDirectory()+"/MesseApp/");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/MesseApp/"+idCircolare+".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {

                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {

                System.out.println(e);
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    System.out.println(ignored);
                }

                return null;
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog = new ProgressDialog(RegistroActivity.this);
            mProgressDialog.setMessage("Scaricando l'allegato");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Errore nel download: " + result, Toast.LENGTH_LONG).show();
            else{
                File file = new File(Environment.getExternalStorageDirectory()+"/MesseApp/"+idCircolare+".pdf");
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),"application/" + MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath()));
                PendingIntent pIntent = PendingIntent.getActivity(RegistroActivity.this, 0, intent, 0);
                Notification.Builder builder  = new Notification.Builder(RegistroActivity.this)
                        .setContentTitle("Circolare scaricata")
                        .setContentText(nomeCircolare)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(pIntent);
                Notification n= null;
                if(Build.VERSION.SDK_INT < 16) {
                    n = builder.getNotification();
                } else {
                    n = builder.build();
                }
                if (n != null) {
                    n.flags |= Notification.FLAG_AUTO_CANCEL;


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.registro));
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
                case 6:
                    setContentView(R.layout.circolari);
                    setUpCircolari();
                    break;
            }
        } else if (getIntent().getIntExtra("circolari",0)==0){
            setContentView(R.layout.menu_registro);
            section = 1;
        } else {
            SCircolari sc = new SCircolari();
            sc.execute();
            section = 6;
        }
    }

}
