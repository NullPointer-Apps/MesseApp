package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.Objects.Voto;
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

public class SVoti extends AsyncTask<Void, Void, Void> {

    ProgressDialog mDialog;
    RegistroActivity c;

    public static HashMap<Integer, Materia> v;

    public SVoti(RegistroActivity c){
        this.c=c;
    }

    protected void onPreExecute() {
        mDialog = ProgressDialog.show(c, null,
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
        c.setUpVoti(v);
    }

    public HashMap<Integer,Materia> scaricaVoti(Element html) throws IOException {
        v = new HashMap<>();
        int j=0,k=0,nv=0;
        Materia materia = new Materia();
        boolean isRec=false;
        boolean isTest=false;
        SharedPreferences sharedpreferences = c.getSharedPreferences("Voti", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("json", "default");
        Long lastUpdate = sharedpreferences.getLong("lastupdate",0);
        if(!json.equals("default")&& (new Date().getTime()-lastUpdate) < 10800000) {
            Type typeOfHashMap = new TypeToken<Map<Integer, Materia>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            v = gson.fromJson(json, typeOfHashMap);
        } else {

            for (Element tr : html.select("tr")) {
                for (Element td : tr.select("td")) {
                    //ha classe font-size-14 (Materie)
                    if (td.text().equals("Test")) {
                        isTest = true;
                        isRec = false;
                        continue;
                    } else if (td.text().equals("Prove recupero")) {
                        isRec = true;
                        isTest = false;
                        continue;
                    }

                    if (td.hasClass("font_size_14") && td.hasText()) {
                        isRec = false;
                        isTest = false;
                        materia = new Materia(td.text());
                        k++;
                        System.out.println(td.text() + k);
                        v.put(k, materia);
                        j = 0;
                        nv = 0;
                    } else {
                        j++;
                        if (td.hasText()) {
                            nv++;
                            Voto voto = new Voto();
                            for (Element span : td.getElementsByTag("span")) {
                                if (span.hasClass("voto_data") && span.hasText()) {
                                    voto.setData(span.text());
                                }
                            }
                            for (Element p : td.getElementsByTag("p")) {
                                if (p.hasText() && p.hasClass("s_reg_testo")) {
                                    if (isRec) {
                                        voto.setTipo("Recupero");
                                    } else if (isTest) {
                                        voto.setTipo("Test");
                                    } else if ((j <= 5) || (j > 15 && j <= 20))
                                        voto.setTipo("Scritto");
                                    else if ((j > 5 && j <= 10) || (j > 20 || j <= 25))
                                        voto.setTipo("Orale");
                                    else voto.setTipo("Pratico");
                                    if (j > 15 && !isRec) {
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
            Gson gson = new GsonBuilder().create();
            json = gson.toJson(v);
            sharedpreferences.edit().putString("json",json).apply();
            sharedpreferences.edit().putLong("lastupdate", new Date().getTime()).apply();
        }
        return v;
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

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
