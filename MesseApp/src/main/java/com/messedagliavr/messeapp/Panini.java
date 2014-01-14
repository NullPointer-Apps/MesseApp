package com.messedagliavr.messeapp;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Panini extends Activity {
    ArrayList<String> nomipanini=null;
    ArrayList<Double> prezzipanini=null;

    ListView list = (ListView)findViewById(R.id.listapanini);
    ListAdapter adapter=new ListAdapter() {
        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return 16;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view=findViewById(R.layout.panini_item);
            TextView nome=(TextView)view.findViewById(R.id.nomepanino);
            nome.setText(nomipanini.get(i));
            return view;
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panini);
        list.setAdapter(adapter);
        initialize();
    }

    @Override
    public void onStop() {
        super.onStop();
        //disconnessione ed eliminazione network
    }

    public void initialize(){
        for(int i=0;i<15;i++){
            switch(i){
                case 1:
                    nomipanini.add(i,"Tonno");
                    prezzipanini.add(i,0.80);
                    break;
                case 2:
                    nomipanini.add(i,"Brioche dolce");
                    prezzipanini.add(i,0.70);
                    break;
                case 3:
                    nomipanini.add(i,"Brioche salata");
                    prezzipanini.add(i,0.70);
                    break;
                case 4:
                    nomipanini.add(i,"Pizzetta Margherita");
                    prezzipanini.add(i,1.20);
                    break;
                case 5:
                    nomipanini.add(i,"Pizzetta Salamino");
                    prezzipanini.add(i,1.20);
                    break;
                case 6:
                    nomipanini.add(i,"Caprese");
                    prezzipanini.add(i,1.20);
                    break;
                case 7:
                    nomipanini.add(i,"Panzerotto");
                    prezzipanini.add(i,1.50);
                    break;
                case 8:
                    nomipanini.add(i,"Cotto&Funghi");
                    prezzipanini.add(i,1.20);
                    break;
                case 9:
                    nomipanini.add(i,"Cotto&Maionese");
                    prezzipanini.add(i,1.20);
                    break;
                case 10:
                    nomipanini.add(i,"Cotto&Funghi");
                    prezzipanini.add(i,1.20);
                    break;
                case 11:
                    nomipanini.add(i,"Cotoletta");
                    prezzipanini.add(i,2.00);
                    break;
                case 12:
                    nomipanini.add(i,"Pizza Margherita");
                    prezzipanini.add(i,2.00);
                    break;
                case 13:
                    nomipanini.add(i,"Pizza Salamino");
                    prezzipanini.add(i,2.00);
                    break;
                case 14:
                    nomipanini.add(i,"Cotto&Funghi(Focaccia)");
                    prezzipanini.add(i,1.20);
                    break;
                case 15:
                    nomipanini.add(i,"Panino Cotto");
                    prezzipanini.add(i,0.80);
                    break;
                case 0:
                    nomipanini.add(i,"Panino Crudo");
                    prezzipanini.add(i,0.80);
                    break;
            }
        }
    }
    public String scontrino(){
        String text="";
        int totale=0;
        int numpanini;
        for(int i=0;i<nomipanini.size();i++){
            numpanini=list.;
            if (list.get(i)!=0) {
                text+=nomipanini.get(i)+" x "+numpanini+"\n";
                totale+=numpanini*prezzipanini.get(i);
            }
        }
        text+="Totale:  "+totale+"€";
        return text;
    }
}