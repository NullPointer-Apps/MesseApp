package com.messedagliavr.messeapp.Dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.messedagliavr.messeapp.R;

public class DialogVoti extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = inflater.inflate(R.layout.dialog_voti, null);
        TextView voto = (TextView) v.findViewById(R.id.voto);
        voto.setText(getArguments().getString("voto"));
        TextView quad = (TextView) v.findViewById(R.id.quad);
        quad.setText("Quad " + getArguments().getInt("quad"));
        TextView data = (TextView) v.findViewById(R.id.data);
        data.setText(getArguments().getString("data"));
        TextView tipo = (TextView) v.findViewById(R.id.tipo);
        tipo.setText(getArguments().getString("tipo"));
        TextView mat = (TextView) v.findViewById(R.id.mat);
        mat.setText(getArguments().getString("materia"));
        ImageView background = (ImageView) v.findViewById(R.id.color);
        background.setBackgroundColor(getArguments().getInt("color"));
        builder.setView(v);

        return builder.create();
    }
}