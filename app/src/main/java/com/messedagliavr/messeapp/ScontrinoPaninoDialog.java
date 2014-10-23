/*package com.messedagliavr.messeapp;

    import android.app.AlertDialog;
    import android.app.Dialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.SharedPreferences;
    import android.net.ConnectivityManager;
    import android.os.Bundle;
    import android.support.v4.app.DialogFragment;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.widget.ListView;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.util.ArrayList;
    import java.util.Arrays;

    public class ScontrinoPaninoDialog extends DialogFragment {

        ArrayList<Integer> numbers;
        ArrayList<Integer> coolposition;
        ArrayList<Double> totals;
        Context context;

        public ScontrinoPaninoDialog(){

        }
        /*public ScontrinoPaninoDialog(ArrayList<Integer> numbers, ArrayList<Double> totals, Context context, ArrayList<Integer> coolposition){
            this.numbers = numbers;
            this.coolposition = coolposition;
            this.totals = totals;
            this.context=context;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            ArrayList<String> names=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_array)));
            ArrayList<String> prices=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_prices_array)));

            LayoutInflater li= (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View scontrinoPanino=li.inflate(R.layout.scontrino,null);
            ListView scontrino = (ListView) scontrinoPanino.findViewById(R.id.scontrino);
            TextView totale = (TextView) scontrinoPanino.findViewById(R.id.totaleScontrino);
            double totaledoub=0;

            for (Double total : totals) totaledoub += total;

            totale.setText(String.format("%.2f", totaledoub)+"€");
            scontrino.setAdapter(new ScontrinoAdapter(getActivity(), names, prices, numbers, totals,coolposition));

            builder.setView(scontrinoPanino)
                    .setPositiveButton("Conferma e invia", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (CheckInternet()) {
                                SharedPreferences prefs = context.getSharedPreferences(
                                        "paniniauth", Context.MODE_PRIVATE);

                                MainActivity.username = prefs.getString("username", "default");
                                MainActivity.password = prefs.getString("password", "default");
                                if (MainActivity.username.equals("default") || MainActivity.password.equals("default")) {
                                    DialogFragment autDialog = new AutPaninoDialog(numbers,context);
                                    autDialog.show(MainActivity.sFm, "AutenticationDialogFragment");
                                } else {
                                    paninisender(numbers,context);
                                }
                            } else {
                                Toast.makeText(context, "Devi avere una connessione alla rete wifi della scuola per poter inviare la lista panini", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }

        public boolean CheckInternet() {
            boolean connected = false;
            ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo wifi = connec
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi.isConnected()) {
                connected = true;
            }

            return connected;

        }

        public static void paninisender(ArrayList<Integer> numbers,Context context) {
            new PaniniSender(numbers, context);
        }
    }

*/