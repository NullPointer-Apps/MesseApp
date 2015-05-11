package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.messedagliavr.messeapp.Objects.Circolari;
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

    RegistroActivity ra;
    ProgressDialog mDialog;
    Boolean isOffline;
    Boolean error=false;
    public static HashMap<Integer, Circolari> c;

    public SCircolari(RegistroActivity ra, Boolean isOffline){
        this.isOffline=isOffline;
        this.ra=ra;
    }

    protected void onPreExecute() {
        mDialog = ProgressDialog.show(ra, null,
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
            SharedPreferences sharedpreferences = ra.getSharedPreferences("Circolari", Context.MODE_PRIVATE);
            String json = sharedpreferences.getString("json", "default");
            if(isOffline && !json.equals("default")) {
                c = new HashMap<>();
                Type typeOfHashMap = new TypeToken<Map<Integer, Circolari>>() { }.getType();
                Gson gson = new GsonBuilder().create();
                c = gson.fromJson(json, typeOfHashMap);
            } else if(isOffline == false) {
                c = scaricaCircolari(leggiPagina("https://web.spaggiari.eu/sif/app/default/bacheca_utente.php").getElementById("data_table"));
            }  else {
                error=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mDialog.dismiss();
        if(!error) {
            ra.setUpCircolari(c);
        } else {
            Toast.makeText(ra,"C'è stato un errore con il download delle circolari", Toast.LENGTH_SHORT);
        }
    }

    public HashMap<Integer,Circolari> scaricaCircolari(Element html) throws IOException {
        c = new HashMap<>();
        int i=0;
        SharedPreferences sharedpreferences = ra.getSharedPreferences("Circolari", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("json", "default");
        Long lastUpdate = sharedpreferences.getLong("lastupdate",0);
        if((!json.equals("default") && (new Date().getTime()-lastUpdate) < 10800000)) {
            Type typeOfHashMap = new TypeToken<Map<Integer, Circolari>>() { }.getType();
            Gson gson = new GsonBuilder().create();
            c = gson.fromJson(json, typeOfHashMap);
        } else {
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
            sharedpreferences.edit().putString("json",json).apply();
            sharedpreferences.edit().putLong("lastupdate", new Date().getTime()).apply();
        }
        return c;
    }

    public static String convertStreamToString(java.io.InputStream is) {
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
