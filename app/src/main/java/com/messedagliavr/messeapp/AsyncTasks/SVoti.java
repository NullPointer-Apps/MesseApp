package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.messedagliavr.messeapp.Dialogs.LoginRegistroDialog;
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

    public static HashMap<Integer, Materia> v;
    ProgressDialog mDialog;
    RegistroActivity c;
    Boolean isOffline = false;
    Boolean error = false;
    Boolean isRefresh = false;
    Boolean loginRequired = false;
    Boolean noVotes = false;

    public SVoti(RegistroActivity c, Boolean isOffline) {
        this.isOffline = isOffline;
        this.c = c;
    }

    public SVoti(RegistroActivity c, Boolean isOffline, Boolean isRefresh) {
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
                "Aggiornamento voti in corso", true, true,
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        SVoti.this.cancel(true);
                    }
                });
    }

    protected Void doInBackground(Void... voids) {

        try {
            SharedPreferences sharedpreferences = c.getSharedPreferences("Voti", Context.MODE_PRIVATE);
            String json = sharedpreferences.getString("json", "default");
            if (isOffline && !json.equals("default") && !isRefresh) {
                v = new HashMap<>();
                Type typeOfHashMap = new TypeToken<Map<Integer, Materia>>() {
                }.getType();
                Gson gson = new GsonBuilder().create();
                v = gson.fromJson(json, typeOfHashMap);
            } else if (!isOffline) {
                v = scaricaVoti(leggiPagina("https://web.spaggiari.eu/cvv/app/default/genitori_voti.php").getElementById("data_table_2"));
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
    protected void onCancelled() {
        mDialog.dismiss();
        c.onBackPressed();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        System.out.println("Ci sono voti " + noVotes);
        System.out.println("Il login è richiesto " + loginRequired);
        if (!error) {
            c.setUpVoti(v);
        } else if (loginRequired) {
            Toast.makeText(c, "È necessario effettuare il login", Toast.LENGTH_SHORT);
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 0);
            data.putBoolean("isOffline", false);
            data.putBoolean("isSessionValid", false);
            login.setArguments(data);
            login.show(c.getSupportFragmentManager(), "login");
        } else if (noVotes) {
            Toast.makeText(c, "Sul registro elettronico non ci sono ancora voti", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(c, "C'è stato un errore con il download dei voti", Toast.LENGTH_SHORT).show();
        }
        mDialog.dismiss();
    }

    public HashMap<Integer, Materia> scaricaVoti(Element html) throws IOException {
        v = new HashMap<>();
        int j = 0, k = 0, nv = 0;
        Materia materia = new Materia();
        boolean isRec = false;
        boolean isTest = false;
        SharedPreferences sharedpreferences = c.getSharedPreferences("Voti", Context.MODE_PRIVATE);
        String json = sharedpreferences.getString("json", "default");
        Long lastUpdate = sharedpreferences.getLong("lastupdate", 0);
        SharedPreferences sp = c.getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
        Long storedDate = sp.getLong("lastLogin", 0);
        /*if (!json.equals("default") && (new Date().getTime() - lastUpdate) < 10800000 && !isRefresh) {
            Type typeOfHashMap = new TypeToken<Map<Integer, Materia>>() {
            }.getType();
            Gson gson = new GsonBuilder().create();
            v = gson.fromJson(json, typeOfHashMap);
        } else if (!(new Date().getTime() - storedDate > 300000) && storedDate != 0 && html != null) {*/

            for (Element tr : html.select("tr")) {
                for (Element td : tr.select("td")) {
                    if (td.text().contains("Test")) {
                        isTest = true;
                        isRec = false;
                        continue;
                    } else if (td.text().equals("Prove recupero")) {
                        isRec = true;
                        isTest = false;
                        continue;
                    }

                    //ha classe font-size-14 (Materie)
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
                                    if (j <= 15 || isRec || isTest) {
                                        voto.setQuadrimestre(1);
                                    } else {
                                        voto.setQuadrimestre(2);
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
            sharedpreferences.edit().putString("json", json).apply();
            sharedpreferences.edit().putLong("lastupdate", new Date().getTime()).apply();
        /*} else {
            error = true;
            if (html == null) {
                noVotes = true;
            } else {
                loginRequired = true;
            }
        }*/
        return v;
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
