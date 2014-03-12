package com.messedagliavr.messeapp;


import android.os.AsyncTask;
import android.widget.Toast;
import android.content.Context;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaniniSender {
    ArrayList<Integer> numbers;
    Boolean success=true;
    Boolean authproblem=false;
    Context context;
    public static String username=MainActivity.username;
    public static String password=MainActivity.password;
    public static String authentication;

    public PaniniSender (ArrayList<Integer> numbers,Context context) {
        this.numbers=numbers;
        new PostListaPanini().execute();
        this.context=context;
    }

    private class PostListaPanini extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost=null;
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));

                httppost = new HttpPost("http://www.nullpointerapps.com/Messedaglia/authentication.php");


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                authentication = httpclient.execute(httppost, responseHandler);


            } catch (ClientProtocolException e) {
                success=false;
            } catch (IOException e) {
                success=false;
            }
            if(authentication.equals("true")) {


                try {

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("classe", MainActivity.myClass));
                    nameValuePairs.add(new BasicNameValuePair("cotoletta", numbers.get(0).toString()));
                    nameValuePairs.add(new BasicNameValuePair("piadacrudo", numbers.get(1).toString()));
                    nameValuePairs.add(new BasicNameValuePair("piadacotto", numbers.get(2).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pizzawurst", numbers.get(3).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pizzasal", numbers.get(4).toString()));
                    nameValuePairs.add(new BasicNameValuePair("panzerotto", numbers.get(5).toString()));
                    nameValuePairs.add(new BasicNameValuePair("cottofunghip", numbers.get(6).toString()));
                    nameValuePairs.add(new BasicNameValuePair("cottofunghif", numbers.get(7).toString()));
                    nameValuePairs.add(new BasicNameValuePair("caprese", numbers.get(8).toString()));
                    nameValuePairs.add(new BasicNameValuePair("crudo", numbers.get(9).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pizzettamarg", numbers.get(10).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pizzettawurst", numbers.get(11).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pizzettasal", numbers.get(12).toString()));
                    nameValuePairs.add(new BasicNameValuePair("tonnatag", numbers.get(13).toString()));
                    nameValuePairs.add(new BasicNameValuePair("tonnatap", numbers.get(14).toString()));
                    nameValuePairs.add(new BasicNameValuePair("cotto", numbers.get(15).toString()));
                    nameValuePairs.add(new BasicNameValuePair("pancetta", numbers.get(16).toString()));
                    nameValuePairs.add(new BasicNameValuePair("salame", numbers.get(17).toString()));
                    if (MainActivity.myPiano.equals(MainActivity.piani[0])) {
                        httppost = new HttpPost("http://www.nullpointerapps.com/Messedaglia/listapaninipiano0.php");
                    }
                    if (MainActivity.myPiano.equals(MainActivity.piani[1])) {
                        httppost = new HttpPost("http://www.nullpointerapps.com/Messedaglia/listapaninipiano2.php");
                    }
                    if (MainActivity.myPiano.equals(MainActivity.piani[2])) {
                        httppost = new HttpPost("http://www.nullpointerapps.com/Messedaglia/listapaninizappatore.php");
                    }

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    String response = httpclient.execute(httppost, responseHandler);

                } catch (ClientProtocolException e) {
                    success = false;
                } catch (IOException e) {
                    success = false;
                }
            } else {
                authproblem=true;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(success==true&&authproblem==false) {
                Toast.makeText(context,"Invio lista panini riuscito correttamente",Toast.LENGTH_LONG).show();
            } else if(success==false&&authproblem==false) {
                Toast.makeText(context,"Invio lista panini fallito, riprova più tardi",Toast.LENGTH_LONG).show();
            } else if (success==true&&authproblem==true){
                Toast.makeText(context,"Hai inserito un nome utente o una password errati",Toast.LENGTH_LONG).show();
            }
        }
    }

}
