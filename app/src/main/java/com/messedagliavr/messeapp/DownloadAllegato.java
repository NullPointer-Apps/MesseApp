package com.messedagliavr.messeapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DownloadAllegato extends AsyncTask<Void, Integer, String> {

    ProgressDialog mProgressDialog;
    private Context context;
    private PowerManager.WakeLock mWakeLock;
    String idCircolare;
    String nomeCircolare;
    NotificationManager nm;

    public DownloadAllegato(Context c, String id, String nome,NotificationManager nm) {
        context = c;
        idCircolare=id;
        nomeCircolare=nome;
        this.nm=nm;
    }

    @Override
    protected String doInBackground(Void... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        String fileName = null;
        HttpGet httpget = new HttpGet("https://web.spaggiari.eu/sif/app/default/bacheca_utente.php?action=file_download&com_id="+idCircolare);
        HttpResponse response;
        try {
            response = RegistroActivity.httpClient.execute(httpget);
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
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                ignored.printStackTrace();
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
        mProgressDialog = new ProgressDialog(context);
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
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Notification.Builder builder  = new Notification.Builder(context)
                    .setContentTitle("Circolare scaricata")
                    .setContentText(nomeCircolare)
                    .setSmallIcon(R.drawable.webicon)
                    .setContentIntent(pIntent);
            Notification n;
            if(Build.VERSION.SDK_INT < 16) {
                n = builder.getNotification();
            } else {
                n = builder.build();
            }
            n.flags |= Notification.FLAG_AUTO_CANCEL;

            nm.notify(0, n);

        }

    }
}
