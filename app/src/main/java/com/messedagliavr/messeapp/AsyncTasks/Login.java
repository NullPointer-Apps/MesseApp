package com.messedagliavr.messeapp.AsyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.messedagliavr.messeapp.RegistroActivity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;

public class Login extends AsyncTask<Void, Void, Boolean> {


    ProgressDialog mDialog;
    Context a;
    HttpPost httpPost;
    String user;
    String pw;
    int c;

    public Login(Context a, HttpPost hp, String user, String pw, int c) {
        this.a = a;
        httpPost = hp;
        this.user = user;
        this.pw = pw;
        this.c = c;
    }

    protected void onPreExecute() {
        mDialog = ProgressDialog.show(a, null,
                "Login in corso", true, true,
                new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        Login.this.cancel(true);
                    }
                });
    }

    protected Boolean doInBackground(Void... voids) {

        try {
            RegistroActivity.httpResponse = RegistroActivity.httpClient.execute(httpPost);
            if (RegistroActivity.httpResponse.getStatusLine().getStatusCode() != 200 || accessoEseguito()) {
                Log.e("login", "ERRORE: Response " + RegistroActivity.httpResponse.getStatusLine().getStatusCode());
                return false;
            } else {
                Log.d("login", " OK");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean b) {
        if (b) {
            mDialog.dismiss();
            Intent registro = new Intent(a, RegistroActivity.class);
            registro.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("USER", user)
                    .putExtra("PWD", pw)
                    .putExtra("circolari", c);
            a.startActivity(registro);
        } else {
            Toast.makeText(a, "Dati errati, login fallito", Toast.LENGTH_SHORT).show();
        }
    }

    //parsing della pagina
    public Document leggiPagina(String url) {
        try {
            HttpGet httpGet = new HttpGet(url);
            InputStream inputStream;
            inputStream = RegistroActivity.httpClient.execute(httpGet).getEntity().getContent();
            Document s1 = Jsoup.parse(inputStream, "UTF-8", url);
            inputStream.close();
            return s1;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean accessoEseguito() {
        String s2 = "";
        try {
            s2 = leggiPagina("https://web.spaggiari.eu/home/app/default/menu_webinfoschool_studenti.php").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s2.substring(0, 150).contains("<html class=\"login_page\">");
    }

}