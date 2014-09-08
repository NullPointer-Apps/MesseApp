package com.messedagliavr.messeapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class AutPaninoDialog extends DialogFragment {
    ArrayList<Integer> numbers;
    Context context;

    public AutPaninoDialog() {

    }

    /*public AutPaninoDialog(ArrayList<Integer> numbers,Context context){
        this.numbers=numbers;
        this.context=context;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater li= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View autPanino=li.inflate(R.layout.alertautenticazione,null);

        builder.setView(autPanino)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.username = ((TextView) autPanino.findViewById(R.id.username)).getText().toString();
                        MainActivity.password = ((TextView) autPanino.findViewById(R.id.password)).getText().toString();
                        Boolean isChecked = ((CheckBox) autPanino.findViewById(R.id.checkBoxPanini)).isChecked();
                        if (isChecked) {
                            SharedPreferences prefs = MainActivity.context.getSharedPreferences(
                                    "paniniauth", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username",MainActivity.username);
                            editor.putString("password",MainActivity.password);
                            editor.commit();
                        }
                        ScontrinoPaninoDialog.paninisender(numbers,context);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }*/
}
