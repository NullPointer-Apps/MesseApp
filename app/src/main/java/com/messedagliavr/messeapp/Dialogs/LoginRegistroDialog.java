package com.messedagliavr.messeapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.messedagliavr.messeapp.AsyncTasks.Login;
import com.messedagliavr.messeapp.Databases.MainDB;
import com.messedagliavr.messeapp.MainActivity;
import com.messedagliavr.messeapp.R;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class LoginRegistroDialog extends DialogFragment  {

    HttpPost httpPost;
    String user = "";
    String pw = "";
    final String custcode = "VRLS0003";
    ArrayList<BasicNameValuePair> values = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);


        LayoutInflater li= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View login=li.inflate(R.layout.registro_activity,null);

        builder.setView(login)
                .setTitle("Login")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CheckBox toggle = (CheckBox) login.findViewById(R.id.chkBoxRegistro);

                        EditText editU = (EditText) login.findViewById(R.id.editU);
                        EditText editPW = (EditText) login.findViewById(R.id.editPW);
                        user = editU.getText().toString().trim();
                        pw = editPW.getText().toString().trim();
                        if (toggle.isChecked()) {
                            MainDB databaseHelper = new MainDB(login.getContext());
                            SQLiteDatabase db = databaseHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("enabled", "true");
                            values.put("username", user);
                            values.put("password", pw);
                            @SuppressWarnings("unused")
                            long samerow = db.update("settvoti", values, null, null);
                            db.close();
                        }

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
                        Login l = new Login(MainActivity.context, httpPost, user, pw,getArguments().getInt("circolari"));
                        l.execute();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }



}
