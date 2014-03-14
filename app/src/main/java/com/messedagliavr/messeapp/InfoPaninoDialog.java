package com.messedagliavr.messeapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class InfoPaninoDialog extends DialogFragment {

    String name;

    public InfoPaninoDialog(String t){
        name=t;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayList<String> names=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_array)));
        ArrayList<String> prices=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_prices_array)));
        ArrayList<String> ingredients=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_ingredients_array)));
        LayoutInflater li= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View infoPanino=li.inflate(R.layout.infopanino,null);
        ((TextView)(infoPanino.findViewById(R.id.nomePanino))).setText(name);
        for (int i=0;i<names.size();i++){
            if (name.equals(names.get(i))){
                ((TextView)(infoPanino.findViewById(R.id.prezzoPanino))).setText(prices.get(i));
                ((TextView)(infoPanino.findViewById(R.id.ingredientiPanino))).setText(ingredients.get(i));
                name=name.replace(" ","").replace("(","").replace(")","").toLowerCase();
                int id= getResources().getIdentifier(name, "drawable", getActivity().getPackageName());
                if (id!=0) {
                    ((ImageView) (infoPanino.findViewById(R.id.imagePanino))).setImageDrawable(getResources().getDrawable(id));
                } else {
                    ((ImageView) (infoPanino.findViewById(R.id.imagePanino))).setImageDrawable(getResources().getDrawable(R.drawable.noimage));
                }
            }
        }
        builder.setView(infoPanino)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }
}
