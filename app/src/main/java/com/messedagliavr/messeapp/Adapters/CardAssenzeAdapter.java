package com.messedagliavr.messeapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.messedagliavr.messeapp.Objects.Assenza;
import com.messedagliavr.messeapp.R;

import java.util.List;


public class CardAssenzeAdapter extends RecyclerView.Adapter<CardAssenzeAdapter.AssenzeViewHolder> {

    private List<Assenza> list;
    Context c;

    public CardAssenzeAdapter(Context c, List<Assenza> list) {
        this.c = c;
        this.list=list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(AssenzeViewHolder vh, int i) {

        Assenza n = list.get(i);
        vh.month.setText(n.getMeseS());
        vh.day.setText(n.getGiornoS());
        vh.cv.setCardBackgroundColor(Color.rgb(79,193,72)); //GREEN
        if (n.isRitardo()) {
            if (n.getTipoR().equals("Breve")) vh.cv.setCardBackgroundColor(Color.rgb(253,165,61)); //ORANGE
            vh.hour.setText(n.getTipoR());
        }
        if (!n.isGiustificata()) vh.cv.setCardBackgroundColor(Color.rgb(238, 81, 67)); //RED
    }

    @Override
    public AssenzeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_assenze, viewGroup, false);

        return new AssenzeViewHolder(itemView);
    }

    public static class AssenzeViewHolder extends RecyclerView.ViewHolder {
        protected TextView month;
        protected TextView day;
        protected TextView hour;
        protected CardView cv;

        public AssenzeViewHolder(View v) {
            super(v);
            cv = (CardView) v;

            month = (TextView) v.findViewById(R.id.meseA);
            day = (TextView) v.findViewById(R.id.giornoA);
            hour = (TextView) v.findViewById(R.id.oraR);
        }
    }

}