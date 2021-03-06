package com.messedagliavr.messeapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.messedagliavr.messeapp.Dialogs.HelpPaninoDialog;
import com.messedagliavr.messeapp.Dialogs.LoginRegistroDialog;
import com.messedagliavr.messeapp.Fragments.NavigationDrawerFragment;
import com.messedagliavr.messeapp.Utilities.MyDifferenceFromToday;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    //GENERAL
    public static String nointernet;
    public static View rootView;
    public static Context context;
    public static int section = 0;
    public static FragmentManager sFm;
    //PANINI
    //public static ListView listViewpanini;
    public static ArrayList<String> names;
    public static ArrayList<String> prices;
    public static ArrayList<Integer> numbers = new ArrayList<>();
    //public static String[] piani;
    //public static String myPiano = "Primo Piano";
    public static String username;
    public static String password;
    public static ActionBar actionBar;
    public static View CoordinatorMain;
    //INFO
    static PackageInfo pinfo = null;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CoordinatorMain = findViewById(R.id.coordinator_main);
        context = getBaseContext();
        sFm = getSupportFragmentManager();

        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    /*public void paniniplus(View i) {
        View parent = (View) (i.getParent());
        TextView num = (TextView) parent.findViewById(R.id.numeroPanini);
        TextView hidden = (TextView) parent.findViewById(R.id.position);
        int numint = Integer.parseInt((num.getText()).toString());
        int tot = 0;
        Log.i("???", String.valueOf(tot));
        for (Integer number : numbers) tot += number;
        Log.i("???", String.valueOf(tot));
        if (numint < 12 && tot < 45) numint++;
        num.setText(String.valueOf(numint));
        int position = Integer.parseInt((hidden.getText()).toString());
        numbers.set(position, numint);
    }

    public void paniniminus(View i) {
        View parent = (View) (i.getParent());
        TextView num = (TextView) parent.findViewById(R.id.numeroPanini);
        TextView hidden = (TextView) parent.findViewById(R.id.position);
        int numint = Integer.parseInt((num.getText()).toString());
        if (numint > 0) numint--;
        num.setText(String.valueOf(numint));
        int position = Integer.parseInt((hidden.getText()).toString());
        numbers.set(position, numint);
    }*/

    /*public void showInfoPanino(View i) {
        View parent = (View)i.getParent().getParent();
        TextView title =(TextView) parent.findViewById(R.id.firstLinear).findViewById(R.id.nomeItemPanino);
        DialogFragment infoDialog = new InfoPaninoDialog(title.getText().toString());
        infoDialog.show(getSupportFragmentManager(), "InfoDialogFragment");
    }*/

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        /*if (position == 1) {
            names = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.panini_array)));
            prices = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.panini_prices_array)));
            int n = names.size();
            for (int i = 0; i < n; i++) {
                if (numbers.size() < n) {
                    numbers.add(i, 0);
                } else {
                    numbers.set(i, 0);
                }
            }
        }*/
        if (position == 4) {
            Intent web = new Intent(Intent.ACTION_VIEW);
            web.setData(Uri
                    .parse("https://www.messedaglia.it"));
            startActivity(web);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position))
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (section == 0) {
            super.finish();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(0))
                    .commit();
        }

    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.app_name);
                break;
            case 1:
                mTitle = getString(R.string.panini);
                break;
            case 2:
                mTitle = getString(R.string.settings);
                break;
            case 6:
                mTitle = getString(R.string.Info);
                break;
            case 7:
                mTitle = "Social";
                break;
            case 12:
                mTitle = getString(R.string.fine_scuola);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar ActionBar = getSupportActionBar();
        assert ActionBar != null;
        ActionBar.setDisplayHomeAsUpEnabled(true);
        ActionBar.setHomeButtonEnabled(true);
        ActionBar.setDisplayShowTitleEnabled(true);
        ActionBar.setTitle(mTitle);
        actionBar = ActionBar;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            switch (section) {
                case 1:
                    //Panini
                    getMenuInflater().inflate(R.menu.panini, menu);
                    break;
                default:
                    getMenuInflater().inflate(R.menu.activity_main, menu);
                    break;
            }
        } else {
            getMenuInflater().inflate(R.menu.global, menu);
        }

        restoreActionBar();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        switch (section) {
            case 1:
                //Panini
                getMenuInflater().inflate(R.menu.panini, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.activity_main, menu);
                break;
        }
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (mNavigationDrawerFragment.isDrawerOpen()) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int FINE_SCUOLA_ID = 1;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        MyDifferenceFromToday diff;
        switch (id) {
            case R.id.help:
                DialogFragment helpDialog = new HelpPaninoDialog();
                helpDialog.show(getSupportFragmentManager(), "ScontrinoDialogFragment");
                break;
            case R.id.sendlist:
                /*String[] prices = getResources().getStringArray(R.array.panini_prices_array);
                ArrayList<Double> totals = new ArrayList<>();
                ArrayList<Integer> coolposition = new ArrayList<>();
                for (int i = 0; i < prices.length; i++) {
                    totals.add(i, numbers.get(i) * Double.parseDouble(prices[i]));
                    if (numbers.get(i) != 0) {
                        coolposition.add(i);
                    }
                }*/
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                boolean go = (hour == 10 && minute <= 15) || (hour < 10 && hour >= 8) || (hour == 7 && minute >= 45);
                if (!go) {
                    View.OnClickListener listener = new View.OnClickListener() {
                        public void onClick(View view) {
                            onBackPressed();
                        }
                    };
                    Snackbar
                            .make(findViewById(R.id.coordinator_news), "La lista panini è chiusa", Snackbar.LENGTH_LONG)
                            .setAction("OK", listener)
                            .show();
                    //Toast.makeText(this, "La lista panini è chiusa", Toast.LENGTH_SHORT).show();
                }/* else if (coolposition.size()>0){
                        //
                        Bundle arg=new Bundle();
                        double t[]= new double[totals.size()];
                        for (int i=0;i<t.length;i++){
                            t[i]=totals.get(i);
                        }
                        arg.putIntegerArrayList("numbers",numbers);
                        arg.putDoubleArray("totals",t);
                        DialogFragment scontrinoDialog = new ScontrinoPaninoDialog(numbers, totals, this, coolposition,numbers);
                        scontrinoDialog.show(getSupportFragmentManager(), "ScontrinoDialogFragment");
                    } else {
                        Toast.makeText(this,"Devi selezionare almeno un panino", Toast.LENGTH_SHORT).show();
                    }*/
                break;
            case FINE_SCUOLA_ID:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(11))
                        .commit();
                break;
            case R.id.timetoend:
                diff = new MyDifferenceFromToday(2016, 6, 8, 13, 0);
                item.getSubMenu().clear();
                item.getSubMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, "Fine della scuola in:");
                item.getSubMenu().add("" + diff.getDays(diff.getDiff()) + " giorni");
                item.getSubMenu().add("" + diff.getHours(diff.getDiff()) + " ore");
                item.getSubMenu().add("" + diff.getMinutes(diff.getDiff()) + " min");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public void social(View v) {
        startActivity(new Intent(this, SocialActivity.class));
    }

    public void NullPApps(View v) {
        Intent npa = new Intent(Intent.ACTION_VIEW);
        npa.setData(Uri.parse("http://www.nullpointerapps.com"));
        startActivity(npa);
    }


    public void voti(View v) {
        Boolean isSessionValid = false;
        SharedPreferences sharedpreferences = getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
        Long storedDate = sharedpreferences.getLong("lastLogin", 0);
        if (!CheckInternet() && storedDate != 0) {
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 0);
            data.putBoolean("isOffline", true);
            login.setArguments(data);
            login.show(getSupportFragmentManager(), "login");

        } else if (!CheckInternet()) {
            Snackbar
                    .make(findViewById(R.id.coordinator_main), R.string.noconnection, Snackbar.LENGTH_LONG)
                    .show();
        } else if ((new Date().getTime() - storedDate) > 300000 || RegistroActivity.httpClient == null || RegistroActivity.httpResponse == null) {
            isSessionValid = false;
        } else if (storedDate != 0) {
            isSessionValid = true;
        }
        if (CheckInternet()) {
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 0);
            data.putBoolean("isOffline", false);
            data.putBoolean("isSessionValid", isSessionValid);
            login.setArguments(data);
            login.show(getSupportFragmentManager(), "login");
        }
    }


    public boolean CheckInternet() {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void news(View v) {
        startActivity(new Intent(this, NewsActivity.class));
    }

    public void calendar(View v) {
        startActivity(new Intent(this, CalendarActivity.class));
    }

    public void orario(View v) {
        startActivity(new Intent(this, TimetableActivity.class));
    }

    public void notices(View v) {
        Boolean isSessionValid = false;
        SharedPreferences sp = getSharedPreferences("Circolari", Context.MODE_PRIVATE);
        String json = sp.getString("json", "default");
        if (CheckInternet() == false && !json.equals("default")) {
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 1);
            data.putBoolean("isOffline", true);
            login.setArguments(data);
            login.show(getSupportFragmentManager(), "login");

        } else if (CheckInternet() == true) {
            SharedPreferences sharedpreferences = getSharedPreferences("RegistroSettings", Context.MODE_PRIVATE);
            Long storedDate = sharedpreferences.getLong("lastLogin", 0);
            if ((new Date().getTime() - storedDate) > 300000 || RegistroActivity.httpClient == null || RegistroActivity.httpResponse == null) {
                isSessionValid = false;
            } else if (storedDate != 0) {
                isSessionValid = true;
            }
            DialogFragment login = new LoginRegistroDialog();
            Bundle data = new Bundle();
            data.putInt("circolari", 1);
            data.putBoolean("isSessionValid", isSessionValid);
            login.setArguments(data);
            login.show(getSupportFragmentManager(), "login");
        } else {
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View view) {
                    onBackPressed();
                }
            };
            Snackbar
                    .make(findViewById(R.id.coordinator_main), R.string.noconnection, Snackbar.LENGTH_LONG)
                    .show();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            section = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, section);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            if ((rootView != null) && ((rootView.getParent()) != null)) {
                ((ViewGroup) rootView.getParent()).removeView(rootView);
            }
            getActivity().supportInvalidateOptionsMenu();
            switch (section) {
                case 0:
                    //home
                    rootView = inflater.inflate(R.layout.home, container, false);
                    break;
                case 1:
                    //Panini
                    /*Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    boolean go = (hour==10&&minute<=5)||(hour<10&&hour>=8)||(hour==7&&minute>=45);
                     if (go) {
                       rootView = inflater.inflate(R.layout.panini, container, false);
                        ListAdapter adapter = new PaniniAdapter(context, names, prices);
                        listViewpanini = (ListView) rootView.findViewById(R.id.listView);
                        listViewpanini.setAdapter(adapter);
                        piani = getResources().getStringArray(R.array.piani);
                        Spinner spin = (Spinner) rootView.findViewById(R.id.spinnerpiani);
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                myPiano = piani[i];
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                        ArrayAdapter<?> aa = new ArrayAdapter<Object>(context,
                                android.R.layout.simple_spinner_item, piani);

                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spin.setAdapter(aa);

                    } else {*/
                    rootView = inflater.inflate(R.layout.paninichiusa, container, false);
                    //}
                    break;
                case 2:
                    startActivity(new Intent(MainActivity.context, SettingsActivity.class));
                    break;
                case 3:
                    //contacts
                    startActivity(new Intent(getActivity(), ContactsActivity.class));
                    break;
                case 5:
                    //suggestion
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.suggestion));
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    input.setVerticalScrollBarEnabled(true);
                    input.setSingleLine(false);
                    builder.setView(input);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String m_Text = input.getText().toString();
                                    Intent emailIntent = new Intent(
                                            Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto", "simone@nullpointerapps.com",
                                            null));
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                            getString(R.string.suggestion));
                                    emailIntent.putExtra(Intent.EXTRA_TEXT,
                                            Html.fromHtml(m_Text));
                                    startActivity(Intent.createChooser(emailIntent,
                                            getString(R.string.suggestion)));
                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                    break;
                case 6:
                    //info
                    rootView = inflater.inflate(R.layout.info, container, false);
                    String versionName = MainActivity.pinfo.versionName;
                    TextView vername = (TextView) rootView.findViewById(R.id.versionname);
                    vername.setText(versionName);
                    break;
                case 7:
                    //social
                    rootView = inflater.inflate(R.layout.social, container, false);

                    break;
                case 12:
                    //Fine Scuola
                    rootView = inflater.inflate(R.layout.fine_scuola, container, false);
                    MyDifferenceFromToday diff = new MyDifferenceFromToday(2016, 6, 8, 13, 0);
                    TextView end = (TextView) rootView.findViewById(R.id.fine_scuola);
                    end.setText("Fine della scuola in:\n" + diff.getDays(diff.getDiff()) + "g " + diff.getHours(diff.getDiff()) + "h " + diff.getMinutes(diff.getDiff()) + "m");
                    break;
                case 13:
                    rootView = inflater.inflate(R.layout.noevents, container, false);
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}

