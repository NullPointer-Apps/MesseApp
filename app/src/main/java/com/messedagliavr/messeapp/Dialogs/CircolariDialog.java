package com.messedagliavr.messeapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.messedagliavr.messeapp.DownloadAllegato;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CircolariDialog extends DialogFragment {
    Context c;
    NotificationManager nm;

    public CircolariDialog(){
    }

    public CircolariDialog(Context c,NotificationManager nm){
        this.c=c;
        this.nm=nm;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getArguments().getString("tit"))
                .setMessage(getArguments().getString("mex"));
        if (getArguments().getBoolean("vis")) {
            builder.setNeutralButton(R.string.download, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final DownloadAllegato downloadTask = new DownloadAllegato(c, getArguments().getString("id"),getArguments().getString("tit"),nm);
                    downloadTask.execute();
                }
            });
        }
        return builder.create();
    }

}

