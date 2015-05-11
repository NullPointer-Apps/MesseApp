package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messedagliavr.messeapp.Dialogs.DialogVoti;
import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.Objects.Voto;
import com.messedagliavr.messeapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ListMaterieAdapter extends ArrayAdapter<Materia> {
    String tipo="";
    int quad=0;
    ArrayList<Double> medie =new ArrayList<>();
    FragmentManager fm;
    public ListMaterieAdapter(Context context, ArrayList<Materia> materie, String tipo, int quad,FragmentManager fm) {
        super(context, 0, materie);
        this.tipo=tipo;
        this.quad=quad+1;
        this.fm = fm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Materia materia = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.litem_registro, parent, false);
        }

        TextView nMat = (TextView) convertView.findViewById(R.id.nMat);
        TextView avg = (TextView) convertView.findViewById(R.id.avg);
        LinearLayout tbVoti = (LinearLayout) convertView.findViewById(R.id.tbVoti);

        nMat.setText(materia.getNome().trim());
        if (medie.size()<=position) medie.add(position,materia.mediaVoti(tipo, quad));
        if (medie.get(position)>0) avg.setText("media: "+medie.get(position));
        else avg.setText("media: n/d");

        tbVoti.removeAllViews();

        HashMap<Integer,Voto> vv = materia.getVoti();
        for (int i = 1; i <= vv.size(); i++) {
            final Voto v = vv.get(i);
            String votos = v.getVoto();
            final TextView voto = new TextView(getContext());
            final SpannableString cs;

            if((v.getTipo().equals(tipo)||tipo.equals("tutti"))&&(v.getQuadrimestre()==quad||quad==3)){

                if ((int) votos.charAt(0) >= 54){
                    if ((int) votos.charAt(0) == 71){
                        //BLUE
                        voto.setBackgroundColor(Color.rgb(114,177,214));
                    } else {
                        //GREEN
                        voto.setBackgroundColor(Color.rgb(79,193,72));
                    }
                }else if ((int) votos.charAt(0) >= 48){
                    //RED
                    voto.setBackgroundColor(Color.rgb(238,81,67));
                }

                switch (v.getTipo()) {
                    case "Scritto":
                        cs = new SpannableString(votos + "S");
                        break;
                    case "Orale":
                        cs = new SpannableString(votos + "O");
                        break;
                    case "Pratico":
                        cs = new SpannableString(votos + "P");
                        break;
                    case "Test":
                        cs = new SpannableString(votos + "T");
                        voto.setBackgroundColor(Color.rgb(114,177,214));
                        break;
                    case "Recupero":
                        cs = new SpannableString(votos + "R");
                        voto.setBackgroundColor(Color.rgb(114,177,214));
                        break;
                    default:
                        cs = new SpannableString(votos + "e");
                        break;
                }

                cs.setSpan(new SubscriptSpan(), cs.length() - 1, cs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.65f), cs.length() - 1, cs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), cs.length() - 1, cs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                voto.setTextSize(16);
                voto.setTextColor(Color.WHITE);
                voto.setText(cs);
                voto.setEms(2);
                voto.setGravity(0x11);
                voto.setLineSpacing(0f, 1.2f);
                voto.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        DialogVoti dv = new DialogVoti();
                        Bundle data = new Bundle();
                        data.putString("materia", materia.getNome());
                        data.putString("voto", v.getVoto());
                        data.putString("data", v.getData());
                        data.putInt("quad", v.getQuadrimestre());
                        data.putString("tipo", v.getTipo());
                        data.putInt("color", ((ColorDrawable) voto.getBackground()).getColor());
                        dv.setArguments(data);
                        dv.show(fm,"dettagli voto");
                        return false;
                    }
                });
                tbVoti.addView(voto);
            }
        }


        // Return the completed view to render on screen
        return convertView;
    }
}