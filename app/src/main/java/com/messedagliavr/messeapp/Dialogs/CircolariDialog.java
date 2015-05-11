package com.messedagliavr.messeapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.messedagliavr.messeapp.AsyncTasks.DownloadAllegato;
import com.messedagliavr.messeapp.R;


public class CircolariDialog extends DialogFragment {
    Context c;
    NotificationManager nm;

    public CircolariDialog(){
    }

    public boolean CheckInternet() {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connec
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connec
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnected()) {
            connected = true;
        } else {
            try {
                if (mobile.isConnected()) connected = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connected;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        c=getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        nm = (NotificationManager) c.getSystemService(c.NOTIFICATION_SERVICE);
        builder.setTitle(getArguments().getString("tit"))
                .setMessage(getArguments().getString("mex"));
        if (getArguments().getBoolean("vis")) {
            builder.setNeutralButton(R.string.download, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final DownloadAllegato downloadTask = new DownloadAllegato(c, getArguments().getString("id"),getArguments().getString("tit"),nm);
                    if(CheckInternet()) {
                        downloadTask.execute();
                    } else {
                        Toast.makeText(c, "Serve una connessione ad internet per scaricare gli allegati", Toast.LENGTH_SHORT);
                    }

                }
            });
        }
        return builder.create();
    }

}

