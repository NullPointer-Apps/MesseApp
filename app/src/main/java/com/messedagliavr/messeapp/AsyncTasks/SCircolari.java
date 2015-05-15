package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.messedagliavr.messeapp.Dialogs.LoginRegistroDialog;
import com.messedagliavr.messeapp.Objects.Circolari;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;

import org.apache.http.client.methods.HttpGet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SCircolari extends AsyncTask<Void, Void, Void> {

    public static HashMap<Integer, Circolari> c;
    static SwipeRefreshLayout mSwipeRefreshLayout;
    RegistroActivity ra;
    ProgressDialog mDialog;
    Boolean isOffline;
    Boolean error = false;
    Boolean isRefresh = false;
    Boolean loginRequired = false;


    public SCircolari(RegistroActivity ra, Boolean isOffline) {
        this.isOffline = isOffline;
        this.ra = ra;
        ra.setContentView(R.layout.circolari);
        ra.setSection(6);
        ra.supportInvalidateOptionsMenu();
    }

    public SCircolari(RegistroActivity ra, Boolean isOffline, Boolean isRefresh) {
        this.isRefresh = isRefresh;
        this.isOffline = isOffline;
        this.ra = ra;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected void onPreExecute() {
        if (!isRefresh || mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) ra.findViewById(R.id.swipe_refresh_layout_circolari);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshCircolari();
                }
            });
            mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#fffbb901"), Color.parseColor("#ff1a171b"));
            mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, ra.getResources().getDisplayMetrics()));
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    protected Void doInBackground(Void... voids) {
        try {
            //lista circolari
            SharedPreferences sharedpreferences = ra.getSharedPreferences("Circolari", Context.MODE_PRIVATE);
            String json = sharedpreferences.getString("json", "default");
            System.out.println(isOffline);
            if (isOffline && !json.equals("default") && !isRefresh) {
                c = new HashMap<>();
                Type typeOfHashMap = new TypeToken<Map<Integer, Circolari>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                c = gson.fromJson(json, typeOfHashMap);
            } else if (!isOffline) {
                System.out.println("Pre Scarica");
                c = scaricaCircolari(leggiPagina("https://web.spaggiari.eu/sif/app/default/bacheca_utente.php").getElementById("data_table"));
                SharedPreferences sp = ra.getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
                sp.edit().putLong("lastLogin", new Date().getTime()).commit();
            } else {
                error = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        mSwipeRefreshLayout.setRefreshing(false);
        ra.onBackPressed();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        //mDialog.dismiss();
        if (!error) {
            ra.setUpCircolari(c);
        } else if (loginRequired) {
            Toast.makeText(ra, "È necessario effettuare il login", Toast.LENGTH_SHORT);
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 1);
            data.putBoolean("isSessionValid", false);
            login.setArguments(data);
            login.show(ra.getSupportFragmentManager(), "login");
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    void refreshCircolari() {
        if (CheckInternet()) {
            SCircolari sc = new SCircolari(ra, false, true);
            sc.execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(ra, "Serve una connessione ad internet per aggiornare le circolari", Toast.LENGTH_SHORT);
        }
    }

    public boolean CheckInternet() {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) ra.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected()) {
            connected = true;
        } else {
            try {
                if (mobile.isConnected()) connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connected;
    }

    public HashMap<Integer, Circolari> scaricaCircolari(Element html) throws IOException {
        c = new HashMap<>();
        int i = 0;
        SharedPreferences sharedpreferences = ra.getSharedPreferences("Circolari", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("json", "default");
        Long lastUpdate = sharedpreferences.getLong("lastupdate", 0);
        SharedPreferences sp = ra.getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
        Long storedDate = sp.getLong("lastLogin", 0);
        if (!json.equals("default") && (new Date().getTime() - lastUpdate) < 10800000 && !isRefresh) {
            Type typeOfHashMap = new TypeToken<Map<Integer, Circolari>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            c = gson.fromJson(json, typeOfHashMap);
        } else if (!(new Date().getTime() - storedDate > 300000) && storedDate != 0) {
            for (Element a : html.select("a.specifica")) {
                Circolari cn = new Circolari();
                i++;
                cn.setData(a.parent().previousElementSibling().select("div.font_size_12").first().ownText());
                cn.setId(a.attr("comunicazione_id"));
                Element dettagli = leggiPagina("https://web.spaggiari.eu/sif/app/default/bacheca_comunicazione.php?action=risposta_com&com_id=" + cn.getId());
                cn.setTitolo(dettagli.select("div").first().ownText());
                cn.setTesto(dettagli.select("div.timesroman").first().ownText());
                if (dettagli.select("div.hidden").size() > 3) {
                    cn.setAllegato(false);
                } else {
                    cn.setAllegato(true);
                }
                System.out.println("Trovata circolare - ID:" + cn.getId() + " Data:" + cn.getData() + " Titolo:" + cn.getTitolo() + " Testo:" + cn.getTesto());
                c.put(i, cn);
            }
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(c);
            sharedpreferences.edit().putString("json", json).apply();
            sharedpreferences.edit().putLong("lastupdate", new Date().getTime()).apply();
        } else {
            error = true;
            loginRequired = true;
        }
        return c;
    }

    //parsing della pagina
    public Document leggiPagina(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader("If-Modified-Since", DateFormat.format("Y-m-d h-M-s", new Date()).toString());
            InputStream inputStream;
            inputStream = RegistroActivity.httpClient.execute(httpGet).getEntity().getContent();
            Document s1 = Jsoup.parse(convertStreamToString(inputStream), "UTF-8", Parser.xmlParser());
            inputStream.close();
            return s1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
