package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.messedagliavr.messeapp.Objects.Materia;
import com.messedagliavr.messeapp.Objects.Voto;
import com.messedagliavr.messeapp.R;

import java.util.ArrayList;

public class ListMaterieAdapter extends ArrayAdapter<Materia> {
    String tipo="";
    int quad=0;
    ArrayList<Double> medie =new ArrayList<>();

    public ListMaterieAdapter(Context context, ArrayList<Materia> materie, String tipo, int quad) {
        super(context, 0, materie);
        this.tipo=tipo;
        this.quad=quad+1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Materia materia = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.litem_registro, parent, false);
        }

        TextView nMat = (TextView) convertView.findViewById(R.id.nMat);
        TextView avg = (TextView) convertView.findViewById(R.id.avg);
        LinearLayout tbVoti = (LinearLayout) convertView.findViewById(R.id.tbVoti);

        nMat.setText(materia.getNome().trim());
        if (medie.size()<=position) medie.add(position,materia.mediaVoti(tipo, quad));
        avg.setText("media: "+medie.get(position));

        tbVoti.removeAllViews();
        for (Voto v : materia.getVoti().values()){

            String votos = v.getVoto();
            TextView voto = new TextView(getContext());
            SpannableString cs;

            if((v.getTipo().equals(tipo)||tipo.equals("tutti"))&&(v.getQuadrimestre()==quad||quad==3)){
                if (v.getTipo().equals("Scritto")) cs = new SpannableString(votos+"s");
                else if (v.getTipo().equals("Orale")) cs = new SpannableString(votos+"o");
                else cs = new SpannableString(votos+"p");

                cs.setSpan(new SubscriptSpan(), cs.length() - 1, cs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                cs.setSpan(new RelativeSizeSpan(0.75f), cs.length() - 1, cs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                voto.setTextSize(16);
                voto.setTextColor(Color.WHITE);
                voto.setText(cs);
                voto.setEms(2);
                voto.setGravity(0x11);
                voto.setLineSpacing(0f,1.2f);
                if ((int) votos.charAt(0) >= 54){
                    if ((int) votos.charAt(0) == 71){
                        //BLUE
                        //#1 voto.setBackgroundColor(Color.rgb(184,210,226));
                        voto.setBackgroundColor(Color.rgb(114,177,214));//#2
                        //#3voto.setBackgroundColor(Color.rgb(53,75,148));
                    } else {
                        //GREEN
                        //#1 voto.setBackgroundColor(Color.rgb(170,210,154));
                        voto.setBackgroundColor(Color.rgb(79,193,72));//#2
                        //#3voto.setBackgroundColor(Color.rgb(22,173,35));

                    }
                }else {
                    //RED
                    //#1voto.setBackgroundColor(Color.rgb(220,96,93));
                    voto.setBackgroundColor(Color.rgb(238,81,67));//#2
                    //#3voto.setBackgroundColor(Color.rgb(187,57,40));
                }

                tbVoti.addView(voto);
            }
        }


        // Return the completed view to render on screen
        return convertView;
    }
}