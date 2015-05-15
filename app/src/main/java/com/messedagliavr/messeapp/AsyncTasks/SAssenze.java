package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.messedagliavr.messeapp.Dialogs.LoginRegistroDialog;
import com.messedagliavr.messeapp.Objects.Assenza;
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

public class SAssenze extends AsyncTask<Void, Void, Void> {

    public static HashMap<Integer, Assenza> a;
    ProgressDialog mDialog;
    RegistroActivity c;
    Boolean isOffline = false;
    Boolean error = false;
    Boolean isRefresh = false;
    Boolean loginRequired = false;

    public SAssenze(RegistroActivity c, Boolean isOffline) {
        this.isOffline = isOffline;
        this.c = c;
    }

    public SAssenze(RegistroActivity c, Boolean isOffline, Boolean isRefresh) {
        this.isRefresh = isRefresh;
        this.isOffline = isOffline;
        this.c = c;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected void onPreExecute() {
        mDialog = ProgressDialog.show(c, null,
                "Aggiornamento assenze in corso", true, true,
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        SAssenze.this.cancel(true);
                    }
                });
    }

    protected Void doInBackground(Void... voids) {
        try {
            SharedPreferences sharedpreferences = c.getSharedPreferences("Assenze", Context.MODE_PRIVATE);
            String json = sharedpreferences.getString("json", "default");
            if (isOffline && !json.equals("default") && !isRefresh) {
                a = new HashMap<>();
                Type typeOfHashMap = new TypeToken<Map<Integer, Assenza>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                a = gson.fromJson(json, typeOfHashMap);
            } else if (isOffline == false) {
                a = scaricaAssenze(leggiPagina("https://web.spaggiari.eu/tic/app/default/consultasingolo.php#calendario").getElementById("skeda_calendario"));
                SharedPreferences sp = c.getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
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
    protected void onPostExecute(Void aVoid) {
        mDialog.dismiss();
        if (!error) {
            c.setUpAssenze(a);
        } else if (loginRequired) {
            Toast.makeText(c, "È necessario effettuare il login", Toast.LENGTH_SHORT);
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 0);
            data.putBoolean("isOffline", false);
            data.putBoolean("isSessionValid", false);
            login.setArguments(data);
            login.show(c.getSupportFragmentManager(), "login");
        } else {
            Toast.makeText(c, "C'è stato un errore con il download delle assenze", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onCancelled() {
        mDialog.dismiss();
        c.onBackPressed();
    }

    public Document leggiPagina(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
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

    public HashMap<Integer, Assenza> scaricaAssenze(Element html) throws IOException {
        a = new HashMap<>();
        String mese, title = "", tipo;
        SharedPreferences sharedpreferences = c.getSharedPreferences("Assenze", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("json", "default");
        Long lastUpdate = sharedpreferences.getLong("lastupdate", 0);
        SharedPreferences sp = c.getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
        Long storedDate = sp.getLong("lastLogin", 0);
        if (!json.equals("default") && (new Date().getTime() - lastUpdate) < 10800000 && !isRefresh) {
            Type typeOfHashMap = new TypeToken<Map<Integer, Assenza>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            a = gson.fromJson(json, typeOfHashMap);
        } else if (!(new Date().getTime() - storedDate > 300000) && storedDate != 0) {

            int n = 0, i;
            for (Element tr : html.select("tr")) {
                if (tr.children().size() > 2) {
                    Element td = tr.child(1);
                    mese = td.text();
                    if (mese.equals("Mese")) continue;
                    td = td.nextElementSibling();
                    for (i = 1; td != null; i++) {
                        tipo = td.text().trim();
                        if (tipo.length() > 0 && (tipo.contains("A") || tipo.contains("R"))) {
                            Log.i("ASS", "trovata assenza:" + "Tipo=" + tipo + " Mese=" + mese + " Giorno=" + i);
                            Assenza ass = new Assenza();
                            ass.setMese(mese.trim());
                            ass.setTipo(String.valueOf(tipo.charAt(tipo.length() - 1)));
                            ass.setGiorno(i);
                            for (Element div : td.select("div")) title = div.attr("title");

                            if (tipo.contains("R")) {
                                ass.setTipoR(title.substring(23));
                            }
                            if (tipo.contains("NG")) {
                                ass.setGiustificata(false);
                            }
                            n++;
                            a.put(n, ass);
                        }
                        td = td.nextElementSibling();
                    }
                }
            }
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(a);
            sharedpreferences.edit().putString("json", json).apply();
            sharedpreferences.edit().putLong("lastupdate", new Date().getTime()).apply();
        } else {
            error = true;
            loginRequired = true;
        }
        return a;
    }

}

