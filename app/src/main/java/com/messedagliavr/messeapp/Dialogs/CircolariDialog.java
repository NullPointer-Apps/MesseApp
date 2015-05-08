package com.messedagliavr.messeapp.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.messedagliavr.messeapp.AsyncTasks.DownloadAllegato;
import com.messedagliavr.messeapp.R;
import com.messedagliavr.messeapp.RegistroActivity;


public class CircolariDialog extends DialogFragment {
    Context c;
    NotificationManager nm;

    public CircolariDialog(){
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
                    downloadTask.execute();
                }
            });
        }
        return builder.create();
    }

}

