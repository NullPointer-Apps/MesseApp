package com.messedagliavr.messeapp.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.messedagliavr.messeapp.Databases.MainDB;
import com.messedagliavr.messeapp.MainActivity;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class LoginRegistroDialog extends DialogFragment {

    HttpPost httpPost;
    ProgressDialog mDialog;
    static Activity a;
    String user = "";
    String pw = "";
    final String custcode = "VRLS0003";
    ArrayList<BasicNameValuePair> values = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        a=getActivity();
        MainDB databaseHelper = new MainDB(MainActivity.context);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = {"enabled", "username", "password"};
        Cursor query = db.query("settvoti", // The table to query
                columns, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        query.moveToFirst();
        String enabled = query.getString(query.getColumnIndex("enabled"));
        db.close();
        if (enabled.matches("true")) {
            user = query.getString(query.getColumnIndex("username"));
            pw = query.getString(query.getColumnIndex("password"));

            values.add(new BasicNameValuePair("custcode", custcode));
            values.add(new BasicNameValuePair("login", user));
            values.add(new BasicNameValuePair("password", pw));

            if (user.contains("@")) {
                httpPost = new HttpPost("https://web.spaggiari.eu/home/app/default/login_email.php");
            } else {
                httpPost = new HttpPost("https://web.spaggiari.eu/home/app/default/login.php");
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(values));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Log.i("LOGIN", "inizio login");
            //faccio il login su un nuovo thread
            Login l = new Login();
            l.execute();

            this.dismiss();

        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(a);


        LayoutInflater li= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View login=li.inflate(R.layout.registro_activity,null);

        builder.setView(login)
                .setTitle("Login")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBox toggle = (CheckBox) login.findViewById(R.id.chkBoxRegistro);

                        EditText editU = (EditText) login.findViewById(R.id.editU);
                        EditText editPW = (EditText) login.findViewById(R.id.editPW);
                        if (toggle.isChecked()) {
                            String username = editU.getText().toString();
                            String password = editPW.getText().toString();
                            MainDB databaseHelper = new MainDB(login.getContext());
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("enabled", "true");
                            values.put("username", username);
                            values.put("password", password);
                            @SuppressWarnings("unused")
                            long samerow = db.update("settvoti", values, null, null);
                            db.close();

                        }

                        user = editU.getText().toString().trim();
                        pw = editPW.getText().toString().trim();


                        values.add(new BasicNameValuePair("custcode", custcode));
                        values.add(new BasicNameValuePair("login", user));
                        values.add(new BasicNameValuePair("password", pw));

                        if (user.contains("@")) {
                            httpPost = new HttpPost("https://web.spaggiari.eu/home/app/default/login_email.php");
                        } else {
                            httpPost = new HttpPost("https://web.spaggiari.eu/home/app/default/login.php");
                        }
                        try {
                            httpPost.setEntity(new UrlEncodedFormEntity(values));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        Log.i("LOGIN", "inizio login");
                        //faccio il login su un nuovo thread
                        Login l = new Login();
                        l.execute();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }

    //parsing della pagina
    public Document leggiPagina(String url)
    {
        try {
            HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader("If-Modified-Since", DateFormat.format("Y-m-d h-M-s", new Date()).toString());
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

    public boolean accessoEseguito()
    {
        String s2="";
        try{
            s2 = leggiPagina("https://web.spaggiari.eu/home/app/default/menu_webinfoschool_studenti.php").toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return s2.substring(0, 150).contains("<html class=\"login_page\">");
    }

    public class Login extends AsyncTask<Void, Void, Boolean> {

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(LoginRegistroDialog.a,null,
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
                if (RegistroActivity.httpResponse.getStatusLine().getStatusCode()!=200 || accessoEseguito()) {
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
                MainActivity.context.startActivity(new Intent(MainActivity.context, RegistroActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("USER",user).putExtra("PWD",pw));
            }
            else {
                Toast.makeText(MainActivity.context, "Dati errati, login fallito", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
