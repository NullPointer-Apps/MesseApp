package com.messedagliavr.messeapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;
import android.graphics.Color;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import android.support.v4.widget.SwipeRefreshLayout;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    //GENERAL
    public static String nointernet;
    public static View rootView;
    static Context context;
    public static int section=0;
    public static FragmentManager sFm;
    //NEWS
    public Boolean unknhost = false;
    public SQLiteDatabase db;
    public Cursor data;
    //CALENDAR
    public ArrayList<Spanned> icalarr = new ArrayList<Spanned>();
    public static final String TITLE = "title";
    public static final String DESC = "description";
    public static final String ICAL = "ical";
    ProgressDialog mDialog;
    public String idical = null;
    //PANINI
    public static ListView listViewpanini;
    public static ArrayList<String> names;
    public static ArrayList<String> prices;
    public static ArrayList<Integer> numbers=new ArrayList<Integer>();
    public static String[] piani;
    public static String myPiano="Primo Piano";
    public static String username;
    public static String password;
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

        context=getBaseContext();
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
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                // create our manager instance after the content view is set
                SystemBarTintManager tintManager = new SystemBarTintManager(this);
                // enable status bar tint
                tintManager.setStatusBarTintEnabled(true);
                // enable navigation bar tint
                tintManager.setNavigationBarTintEnabled(true);
                tintManager.setTintColor(Color.parseColor("#AFAFAF"));
                }
    }

    public void paniniplus(View i){
        View parent= (View)(i.getParent());
        TextView num = (TextView) parent.findViewById(R.id.numeroPanini);
        TextView hidden = (TextView) parent.findViewById(R.id.position);
        int numint = Integer.parseInt((num.getText()).toString());
        int tot=0;
        Log.i("???",String.valueOf(tot));
        for (Integer number : numbers) tot += number;
        Log.i("???",String.valueOf(tot));
        if (numint<12&&tot<45) numint++;
        num.setText(String.valueOf(numint));
        int position = Integer.parseInt((hidden.getText()).toString());
        numbers.set(position,numint);
    }

    public void paniniminus(View i){
        View parent= (View)(i.getParent());
        TextView num = (TextView) parent.findViewById(R.id.numeroPanini);
        TextView hidden = (TextView) parent.findViewById(R.id.position);
        int numint = Integer.parseInt((num.getText()).toString());
        if (numint>0) numint--;
        num.setText(String.valueOf(numint));
        int position = Integer.parseInt((hidden.getText()).toString());
        numbers.set(position,numint);
    }

    public void showInfoPanino(View i){/*
        View parent = (View)i.getParent().getParent();
        TextView title =(TextView) parent.findViewById(R.id.firstLinear).findViewById(R.id.nomeItemPanino);
        DialogFragment infoDialog = new InfoPaninoDialog(title.getText().toString());
        infoDialog.show(getSupportFragmentManager(), "InfoDialogFragment");*/
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (position==1){
            names=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_array)));
            prices=new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.panini_prices_array)));
            int n = names.size();
            for (int i=0;i<n;i++) {
                if(numbers.size()<n){
                    numbers.add(i,0);
                } else {
                    numbers.set(i,0);
                }
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position))
                    .commit();
    }

    @Override
    public void onBackPressed() {
        if (section==0){
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
            case 5:
                mTitle = getString(R.string.Info);
                break;
            case 8:
                mTitle = getString(R.string.notizie);
                break;
            case 9:
                mTitle = getString(R.string.eventi);
                break;
            case 10:
                mTitle = getString(R.string.circolari);
                break;
            case 11:
                mTitle = getString(R.string.fine_scuola);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            switch(section){
                case 1:
                    //Panini
                    getMenuInflater().inflate(R.menu.panini, menu);
                    break;
                case 8:
                    //News
                    getMenuInflater().inflate(R.menu.news, menu);
                    break;
                case 9:
                case 12:
                    //Calendar
                    getMenuInflater().inflate(R.menu.calendar, menu);
                    break;
                case 10:
                    //Circolari
                    getMenuInflater().inflate(R.menu.notices, menu);
                    break;
                case 11:
                    //Fine Scuola
                    getMenuInflater().inflate(R.menu.fine_scuola, menu);
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
        switch(section){
            case 1:
                //News
                getMenuInflater().inflate(R.menu.panini, menu);
                break;
            case 8:
                //News
                getMenuInflater().inflate(R.menu.news, menu);
                break;
            case 9:
            case 12:
                //Calendar
                getMenuInflater().inflate(R.menu.calendar, menu);
                break;
            case 10:
                //Circolari
                getMenuInflater().inflate(R.menu.notices, menu);
                break;
            case 11:
                //Fine Scuola
                getMenuInflater().inflate(R.menu.fine_scuola, menu);
                break;
            default:
                getMenuInflater().inflate(R.menu.activity_main, menu);
                break;
        }
        restoreActionBar();

        return super.onCreateOptionsMenu(menu);
    }

    private final int FINE_SCUOLA_ID = 1;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
           DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (mNavigationDrawerFragment.isDrawerOpen()){
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        MyDifferenceFromToday diff;
        switch (id){
            case R.id.help:
                DialogFragment helpDialog = new HelpPaninoDialog();
                helpDialog.show(getSupportFragmentManager(), "ScontrinoDialogFragment");
                break;
            case R.id.sendlist:
                    String[] prices=getResources().getStringArray(R.array.panini_prices_array);
                    ArrayList<Double> totals=new ArrayList<Double>();
                    ArrayList<Integer> coolposition=new ArrayList<Integer>();
                    for (int i=0;i<prices.length;i++){
                        totals.add(i,numbers.get(i)*Double.parseDouble(prices[i]));
                        if(numbers.get(i)!=0){
                            coolposition.add(i);
                        }
                    }
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                boolean go = (hour==10&&minute<=15)||(hour<10&&hour>=8)||(hour==7&&minute>=45);
                    if (!go) {
                        Toast.makeText(this,"La lista panini è chiusa", Toast.LENGTH_SHORT).show();
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
            case R.id.refreshend:
                diff = new MyDifferenceFromToday(2015,6,6,13,0);
                TextView end = (TextView) rootView.findViewById(R.id.fine_scuola);
                end.setText("Fine della scuola in:\n"+diff.getDays(diff.getDiff())+"g "+diff.getHours(diff.getDiff())+"h "+diff.getMinutes(diff.getDiff())+"m");
                break;
            case FINE_SCUOLA_ID:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(11))
                        .commit();
                break;
            case R.id.timetoend:
                diff = new MyDifferenceFromToday(2015,6,6,13,0);
                item.getSubMenu().clear();
                item.getSubMenu().add(Menu.NONE, Menu.NONE, Menu.NONE, "Fine della scuola in:");
                item.getSubMenu().add(""+diff.getDays(diff.getDiff())+" giorni").setEnabled(false);
                item.getSubMenu().add(""+diff.getHours(diff.getDiff())+" ore").setEnabled(false);
                item.getSubMenu().add("" + diff.getMinutes(diff.getDiff()) + " min").setEnabled(false);
                break;

            case R.id.refreshnotices:
               /* if (CheckInternet()) {
                    MainActivity.nointernet = "false";
                    new getNotices().execute();
                } else {
                    Toast.makeText(this, R.string.noconnection,
                            Toast.LENGTH_LONG).show();
                }*/
                break;
            case R.id.palestre:
                Toast.makeText(MainActivity.this, R.string.notavailable,
                        Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void refreshNews() {
	if (CheckInternet()) {
					SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
                    Database databaseHelper = new Database(getBaseContext());
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues nowdb = new ContentValues();
                    nowdb.put("newsdate", "2012-02-20 15:00:00");
                    long samerow = db.update("lstchk", nowdb, null, null);
                    db.close();
                    MainActivity.nointernet = "false";
					connection news = new connection (false);
                    news.execute();
					mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(this, R.string.noconnection,
                            Toast.LENGTH_LONG).show();
                }
	}
	
	public void refreshCalendar() {
	if (CheckInternet()) {
					SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
                    Database databaseHelper = new Database(getBaseContext());
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    ContentValues nowdb = new ContentValues();
                    nowdb.put("calendardate", "2012-02-20 15:00:00");
                    long samerow = db.update("lstchk", nowdb, null, null);
                    db.close();
                    MainActivity.nointernet = "false";
					connectioncalendar calendar = new connectioncalendar (false);
                    calendar.execute();
					mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(this, R.string.noconnection,
                            Toast.LENGTH_LONG).show();
                }
	}

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ical:
                if (android.os.Build.VERSION.SDK_INT < 14) {
                    Toast.makeText(this, R.string.noapilevel,
                            Toast.LENGTH_LONG).show();
                } else {
                    idical = icalarr.get(info.position).toString();
                    new eventparser().execute();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    public void onToggleClicked(View view) {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.saveenabled);
        boolean on = toggle.isChecked();
        EditText user = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button save = (Button) findViewById(R.id.savesett);
        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox1);
        if (on) {
            Database databaseHelper = new Database(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] columns = { "username", "password" };
            Cursor query = db.query("settvoti", // The table to query
                    columns, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            user.setVisibility(View.VISIBLE);
            query.moveToFirst();
            user.setText(query.getString(query.getColumnIndex("username")));
            password.setVisibility(View.VISIBLE);
            password.setText(query.getString(query.getColumnIndex("password")));
            save.setVisibility(View.VISIBLE);
            checkbox.setVisibility(View.VISIBLE);
            query.close();
            db.close();
        } else {
            user.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            checkbox.setVisibility(View.GONE);
            Database databaseHelper = new Database(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("enabled", "false");
            @SuppressWarnings("unused")
            long samerow = db.update("settvoti", values, null, null);
            db.close();
            Toast.makeText(this, getString(R.string.noautologin),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onCheckClickedRegistro(View view) {
        CheckBox toggle = (CheckBox) findViewById(R.id.checkBox1);
        EditText password = (EditText) findViewById(R.id.password);
        if (toggle.isChecked()) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password.setInputType(129);
        }
    }

    public void onCheckClickedPanini(View view) {
        CheckBox toggle = (CheckBox) findViewById(R.id.checkBoxPaniniSettings);
        EditText password = (EditText) findViewById(R.id.passwordpanini);
        if (toggle.isChecked()) {
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password.setInputType(129);
        }
    }

    public void onSaveClicked(View view) {
        EditText usert = (EditText) findViewById(R.id.username);
        EditText passwordt = (EditText) findViewById(R.id.password);
        String username = usert.getText().toString();
        String password = passwordt.getText().toString();
        Database databaseHelper = new Database(getBaseContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("enabled", "true");
        values.put("username", username);
        values.put("password", password);
        @SuppressWarnings("unused")
        long samerow = db.update("settvoti", values, null, null);
        db.close();
        Toast.makeText(this, getString(R.string.settingssaved),
                Toast.LENGTH_LONG).show();
    }

    public void social(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(6))
                .commit();
    }

    public void NullPApps(View v) {
        Intent npa = new Intent(Intent.ACTION_VIEW);
        npa.setData(Uri.parse("http://www.nullpointerapps.com"));
        startActivity(npa);
    }

    public void youtube(View v) {
        Intent youtube = new Intent(Intent.ACTION_VIEW);
        youtube.setData(Uri.parse("http://www.youtube.com/user/MessedagliaWeb"));
        startActivity(youtube);
    }

    public void moodle(View v) {
        Intent moodle = new Intent(Intent.ACTION_VIEW);
        moodle.setData(Uri.parse("http://corsi.messedaglia.it"));
        startActivity(moodle);
    }

    public void facebook(View v) {
        String fbapp = "fb://group/110918169016604";
        Intent fbappi = new Intent(Intent.ACTION_VIEW, Uri.parse(fbapp));
        try {
            startActivity(fbappi);
        } catch (ActivityNotFoundException ex) {
            String uriMobile = "http://touch.facebook.com/groups/110918169016604";
            Intent fb = new Intent(Intent.ACTION_VIEW, Uri.parse(uriMobile));
            startActivity(fb);
        }
    }

    public void voti(View v) {
        Database databaseHelper = new Database(getBaseContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String[] columns = { "enabled", "username", "password" };
        Cursor query = db.query("settvoti", // The table to query
                columns, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        );
        query.moveToFirst();
        String enabled = query.getString(query.getColumnIndex("enabled"));
        db.close();
        if (enabled.matches("true")) {
            String user = query.getString(query.getColumnIndex("username"));
            String password = query.getString(query.getColumnIndex("password"));
            Intent voti = new Intent(Intent.ACTION_VIEW);
            voti.setData(Uri
                    .parse("https://web.spaggiari.eu/home/app/default/login.php?custcode=VRLS0003&login="
                            + user + "&password=" + password));
            query.close();
            startActivity(voti);
        } else {
            Intent voti = new Intent(Intent.ACTION_VIEW);
            voti.setData(Uri
                    .parse("https://web.spaggiari.eu/home/app/default/login.php?custcode=VRLS0003"));
            query.close();
            startActivity(voti);
        }
    }

    public void news(View v) {
        supportInvalidateOptionsMenu();
        if (CheckInternet()) {
            nointernet = "false";
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(8))
                    .commit();
            new connection().execute();
        } else {
            String[] outdated = { "newsdate", "calendardate" };
            Database databaseHelper = new Database(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String nodata = "1995-01-19 23:40:20";
            Cursor date = db.query("lstchk", // The table to query
                    outdated, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            date.moveToFirst();
            String verifydatenews = date.getString(date
                    .getColumnIndex("newsdate"));
            date.close();
            db.close();
            if (!nodata.equals(verifydatenews)) {
                nointernet = "true";
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(8))
                        .commit();
                new connection().execute();
            } else {
                Toast.makeText(this, R.string.noconnection,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private Long getTimeDiff(String time, String curTime) throws ParseException {
        Date curDate = null;
        Date oldDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            curDate = formatter.parse(curTime);
            oldDate = formatter.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        long oldMillis = oldDate.getTime();
        long curMillis = curDate.getTime();
        long diff = curMillis - oldMillis;
        return diff;
    }

    public void calendar(View v) {
        supportInvalidateOptionsMenu();
        if (CheckInternet()) {
            nointernet = "false";
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(9))
                    .commit();
            new connectioncalendar().execute();
        } else {
            String[] outdated = { "newsdate", "calendardate" };
            Database databaseHelper = new Database(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String nodata = "1995-01-19 23:40:20";
            Cursor date = db.query("lstchk", // The table to query
                    outdated, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            date.moveToFirst();
            String verifydatenews = date.getString(date
                    .getColumnIndex("newsdate"));
            date.close();
            db.close();
            if (!nodata.equals(verifydatenews)) {
                nointernet = "true";
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(9))
                        .commit();

                new connectioncalendar().execute();
            } else {
                Toast.makeText(this,
                        R.string.noconnection, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void orario(View v) {
        startActivity(new Intent(this, timetable.class));
    }

    public void notices(View v){
        Toast.makeText(this, R.string.ondevelopment,
                Toast.LENGTH_LONG).show();
        /*supportInvalidateOptionsMenu();
        if (CheckInternet()) {
            nointernet = "false";
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(10))
                    .commit();
            new getNotices().execute();
        } else {
            nointernet="true";
            Toast.makeText(this, R.string.noconnection,
                       Toast.LENGTH_LONG).show();
        }*/
    }

    public void notavailable(View v) {
        Toast.makeText(this, R.string.notavailable,
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 3:
                //CIRCOLARI
                mDialog = new ProgressDialog(this);
                mDialog.setMessage(getString(R.string.downloadingNotices));
                mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mDialog.setProgress(0);
                mDialog.setCancelable(false);
                mDialog.show();
                return mDialog;
            default:
                return null;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            section=sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, section);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            if (rootView!=null) ((ViewGroup)rootView.getParent()).removeView(rootView);
            getActivity().supportInvalidateOptionsMenu();
            switch(section){
                case 0:
                    //home
                    rootView = inflater.inflate(R.layout.home, container, false);
                    break;
                case 1:
                    //Panini
                    Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    //boolean go = (hour==10&&minute<=5)||(hour<10&&hour>=8)||(hour==7&&minute>=45);
                    boolean go =false;
                    if (go){
                       /* rootView = inflater.inflate(R.layout.panini, container, false);
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
                    */} else {
                        rootView = inflater.inflate(R.layout.paninichiusa, container, false);
                    }
                    break;
                case 2:
                    //settings
                    rootView = inflater.inflate(R.layout.settings, container, false);
                    EditText user = (EditText) rootView.findViewById(R.id.username);
                    EditText password = (EditText) rootView.findViewById(R.id.password);
                    CheckBox check = (CheckBox) rootView.findViewById(R.id.checkBox1);
                    Button save = (Button) rootView.findViewById(R.id.savesett);
                    ToggleButton toggle = (ToggleButton) rootView.findViewById(R.id.saveenabled);
                    Database databaseHelpersettings = new Database(getActivity());
                    SQLiteDatabase dbsettings = databaseHelpersettings.getWritableDatabase();
                    String[] columnssettings = { "enabled", "username", "password" };
                    Cursor query = dbsettings.query("settvoti", // The table to query
                            columnssettings, // The columns to return
                            null, // The columns for the WHERE clause
                            null, // The values for the WHERE clause
                            null, // don't group the rows
                            null, // don't filter by row groups
                            null // The sort order
                    );
                    query.moveToFirst();
                    String enabled = query.getString(query.getColumnIndex("enabled"));
                    dbsettings.close();
                    if (enabled.matches("true")) {
                        user.setVisibility(View.VISIBLE);
                        user.setText(query.getString(query.getColumnIndex("username")));
                        password.setVisibility(View.VISIBLE);
                        password.setText(query.getString(query
                                .getColumnIndex("password")));
                        save.setVisibility(View.VISIBLE);
                        check.setVisibility(View.VISIBLE);
                        toggle.setChecked(true);
                    } else {
                        user.setVisibility(View.GONE);
                        password.setVisibility(View.GONE);
                        save.setVisibility(View.GONE);
                        check.setVisibility(View.GONE);
                    }
                    query.close();

                    EditText usernamepanini = (EditText) rootView.findViewById(R.id.usernamepanini);
                    EditText passwordpanini = (EditText) rootView.findViewById(R.id.passwordpanini);
                    SharedPreferences prefs = context.getSharedPreferences(
                            "paniniauth", Context.MODE_PRIVATE);
                    String usernsett= prefs.getString("username", "default");
                    String passwsett=prefs.getString("password", "default");
                    if(!usernsett.equals("default")&&!passwsett.equals("default")) {
                        usernamepanini.setText(usernsett);
                        passwordpanini.setText(passwsett);
                    }
                    break;
                case 3:
                    //contacts
                    startActivity(new Intent(getActivity(), contacts.class));
                    break;
                case 4:
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
                                            "mailto", "support@nullpointerapps.com",
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
                case 5:
                    //info
                    rootView = inflater.inflate(R.layout.info, container, false);
                    String versionName = MainActivity.pinfo.versionName;
                    TextView vername = (TextView) rootView.findViewById(R.id.versionname);
                    vername.setText(versionName);
                    break;
                case 6:
                    //social
                    rootView = inflater.inflate(R.layout.social, container, false);
                    break;
                case 8:
                    //News
                    rootView = inflater.inflate(R.layout.list_item, container, false);
                    break;
                case 9:
                    //Calendar
                    rootView = inflater.inflate(R.layout.list_item, container, false);
                    break;
                case 10:
                    //Circolari
                    rootView = inflater.inflate(R.layout.list_item, container, false);
                    break;
                case 11:
                    //Fine Scuola
                    rootView = inflater.inflate(R.layout.fine_scuola, container, false);
                    MyDifferenceFromToday diff = new MyDifferenceFromToday(2014,6,7,13,0);
                    TextView end = (TextView)rootView.findViewById(R.id.fine_scuola);
                    end.setText("Fine della scuola in:\n" + diff.getDays(diff.getDiff()) + "g " + diff.getHours(diff.getDiff()) + "h " + diff.getMinutes(diff.getDiff()) + "m");
                    break;
                case 12:
                    rootView =inflater.inflate(R.layout.noevents, container, false);
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

    /*public class getNotices extends AsyncTask<String, Integer, ArrayList<String>>{

        ArrayList<String> titoli = new ArrayList<String>();
        ArrayList<String> titolir = new ArrayList<String>();
        String find = "/index.php/component/jdownloads/finish/";
        int n=20;

        public void onPreExecute() {
            showDialog(3);
        }

        @Override
        protected void onPostExecute(final ArrayList<String> strings) {
            dismissDialog(3);
            if (strings.size()>n){
                for (int i=0;i<n;i++) {
                    titoli.add(strings.get(strings.size()-i-1));
                }
                Collections.reverse(titoli);
            } else {
                titoli.addAll(strings);
            }
            String titolo;
            char[] ctit;
            for (String aTitoli : titoli) {
                titolo = aTitoli.substring(find.length());
                for (int k = 0; k < titolo.length() - 3; k++) {
                    if (titolo.substring(k, k + 3).equals("-p-")) {
                        titolo = titolo.substring(k + 3);
                        break;
                    }
                }
                titolo = titolo.replace('-', ' ');
                titolo = titolo.replace("/", " - ");
                titolo = titolo.substring(0, titolo.length() - 9);
                ctit = titolo.toCharArray();
                ctit[0] = String.valueOf(titolo.charAt(0)).toUpperCase().charAt(0);
                titolo = String.copyValueOf(ctit);
                titolir.add(titolo);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    MainActivity.this, android.R.layout.simple_list_item_1,
                    titolir);
            ListView listView = (ListView) rootView.findViewById(android.R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parentView,
                                        View childView, int position, long id){
                    Intent notices = new Intent(Intent.ACTION_VIEW);
                    notices.setData(Uri
                            .parse("http://www.messedaglia.it"+titoli.get(position)));
                    startActivity(notices);
                }
            });
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mDialog.setProgress(values[0]);
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> links = new ArrayList<String>();

            final String URL = "http://www.messedaglia.it/index.php/component/jdownloads/viewdownload/6/";
            int id=2120;
            Elements linkss;
            String n;
            boolean found=true;
            for (int i=0;found;i++){
                org.jsoup.nodes.Document doc = null;
                id++;
                try {
                    doc = Jsoup.connect(URL + id).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                linkss= doc.select("a[href]");
                for (org.jsoup.nodes.Element link : linkss) {
                    n=link.attr("abs:href");
                    if(n.contains(find)){
                        links.add(n);
                        publishProgress((int) (( i/ (float) this.n) * 100));
                        found=true;
                        break;
                    } else {
                        found=false;
                    }
                }
            }
            return links;
        }
    }*/

    public class connection extends
            AsyncTask<Void, Void, HashMap<String, ArrayList<Spanned>>> {
			Boolean showLoading=true;
			public connection (Boolean showLoading) {
			this.showLoading=showLoading;
			}
        protected void onCancelled() {
            Intent main = new Intent(MainActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            Toast.makeText(MainActivity.this, R.string.cancelednews, Toast.LENGTH_LONG)
                    .show();
        }

        public void onPreExecute() {
            if (MainActivity.nointernet.equals("true") && showLoading == true) {
                mDialog = ProgressDialog.show(MainActivity.this, null,
                        getString(R.string.retrievingNews), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connection.this.cancel(true);
                            }
                        });
            } else if (showLoading == true) {
                mDialog = ProgressDialog.show(MainActivity.this, null,
                        getString(R.string.downloadingNews), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connection.this.cancel(true);
                            }
                        });
            }
        }

        public HashMap<String, ArrayList<Spanned>> doInBackground(
                Void... params) {
            Database databaseHelper = new Database(getBaseContext());
            db = databaseHelper.getWritableDatabase();
            HashMap<String, ArrayList<Spanned>> temhashmap = new HashMap<String, ArrayList<Spanned>>();
            ArrayList<Spanned> titoli = new ArrayList<Spanned>();
            ArrayList<Spanned> descrizioni = new ArrayList<Spanned>();
            ArrayList<Spanned> datePubList = new ArrayList<Spanned>();
            ArrayList<Spanned> titolib = new ArrayList<Spanned>();
            // All static variables
            final String URL = "https://www.messedaglia.it/index.php?option=com_ninjarsssyndicator&feed_id=1&format=raw";
            // XML node keys
            final String ITEM = "item"; // parent node
            final String TITLE = "title";
            final String DESC = "description";
            final String PUBDATE = "pubDate";
            Element e = null;
            ArrayList<HashMap<String, Spanned>> menuItems = new ArrayList<HashMap<String, Spanned>>();
            String[] outdated = { "newsdate", "calendardate" };
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = df.format(c.getTime());
            Cursor date = db.query("lstchk", // The table to query
                    outdated, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            date.moveToFirst();
            String past = date.getString(date.getColumnIndex("newsdate"));
            date.close();
            long l = getTimeDiff(past, now);
            if (l / 10800000 >= 3 && MainActivity.nointernet != "true") {
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(URL);
                if (xml == "UnknownHostException") {
                    unknhost = true;
                    db.close();
                    return temhashmap;
                } else {
                    Document doc = parser.getDomElement(xml);
                    NodeList nl = doc.getElementsByTagName(ITEM);
                    ContentValues values = new ContentValues();
						SimpleDateFormat parserDatePub=new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",Locale.US);
						DateFormat datePubFormat;
						Locale currentLocale = getResources().getConfiguration().locale;
						datePubFormat = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
                    for (int i = 0; i < nl.getLength(); i++) {
                        HashMap<String, Spanned> map = new HashMap<String, Spanned>();
                        e = (Element) nl.item(i);
                        values.put("_id", i);
                        values.put(TITLE, parser.getValue(e, TITLE));
                        values.put(DESC, parser.getValue(e, DESC));
							String datePubLocale="";
						try{
							datePubLocale=datePubFormat.format(parserDatePub.parse(parser.getValue(e, PUBDATE))).toString();
							} catch (java.text.ParseException dateP) {
								System.out.println(dateP);
								datePubLocale=parser.getValue(e, PUBDATE);
							}
                        values.put(PUBDATE, datePubLocale);
                        values.put("titleb", "<b>" + parser.getValue(e, TITLE)
                                + "</b>");
                        map.put(TITLE, Html.fromHtml(parser.getValue(e, TITLE)));
                        map.put(DESC, Html.fromHtml(parser.getValue(e, DESC)));
						map.put(PUBDATE, Html.fromHtml(datePubLocale));

                        titoli.add(Html.fromHtml(parser.getValue(e, TITLE)));
                        descrizioni
                                .add(Html.fromHtml(parser.getValue(e, DESC)));
                        datePubList
                                .add(Html.fromHtml(datePubLocale));
							System.out.println(Html.fromHtml(datePubLocale));
                        titolib.add(Html.fromHtml("<b>"
                                + parser.getValue(e, TITLE) + "</b>"));
                        // adding HashList to ArrayList
                        menuItems.add(map);
                        long newRowId = db.insertWithOnConflict("news", null,
                                values, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                    ContentValues nowdb = new ContentValues();
                    nowdb.put("newsdate", now);
                    long samerow = db.update("lstchk", nowdb, null, null);
                    db.close();
                    temhashmap.put("titoli", titoli);
                    temhashmap.put("descrizioni", descrizioni);
                    temhashmap.put("titolib", titolib);
                    temhashmap.put("pubdate", datePubList);
                    return temhashmap;

                }
            } else {
                String[] clmndata = { "title","pubdate", "description", "titleb" };
                String sortOrder = "_id";

                data = db.query("news", // The table to query
                        clmndata, // The columns to return
                        null, // The columns for the WHERE clause
                        null, // The values for the WHERE clause
                        null, // don't group the rows
                        null, // don't filter by row groups
                        sortOrder // The sort order
                );
                for (data.move(0); data.moveToNext(); data.isAfterLast()) {
                    HashMap<String, Spanned> map = new HashMap<String, Spanned>();
                    map.put(TITLE, Html.fromHtml(data.getString(data
                            .getColumnIndex("title"))));
                    map.put(DESC, Html.fromHtml(data.getString(data
                            .getColumnIndex("description"))));
                    map.put(PUBDATE, Html.fromHtml(data.getString(data
                            .getColumnIndex("pubdate"))));

                    titoli.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("title"))));
                    descrizioni.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("description"))));
                    titolib.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("titleb"))));
                    datePubList.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("pubdate"))));
                    // adding HashList to ArrayList
                    menuItems.add(map);

                }
                data.close();
                db.close();
                temhashmap.put("titoli", titoli);
                temhashmap.put("descrizioni", descrizioni);
                temhashmap.put("titolib", titolib);
                temhashmap.put("pubdate", datePubList);
                return temhashmap;

            }

        }

        public void onPostExecute(HashMap<String, ArrayList<Spanned>> resultmap) {
            if (unknhost) {
                Toast.makeText(MainActivity.this, R.string.connerr, Toast.LENGTH_LONG)
                        .show();
                Intent main = new Intent(MainActivity.this, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            } else {
                if (resultmap.size() > 0) {
					SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
					mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
					@Override
						public void onRefresh() {
							refreshNews();
						}
                    final ArrayList<Spanned> titoli = resultmap.get("titoli");
                    final ArrayList<Spanned> descrizioni = resultmap
                            .get("descrizioni");
                    final ArrayList<Spanned> pubDate = resultmap
                            .get("pubdate");
                    final ArrayList<Spanned> titolib = resultmap.get("titolib");

                    NewsAdapter adapter = new NewsAdapter(
                            MainActivity.context, R.layout.item_news,
                            titolib, pubDate);
                    ListView listView = (ListView) rootView.findViewById(android.R.id.list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parentView,
                                                View childView, int position, long id) {
                            Intent intent = new Intent(MainActivity.this,
                                    ListItemSelectedNews.class);
                            intent.putExtra(TITLE,
                                    Html.toHtml(titoli.get(position)));
                            intent.putExtra(DESC,
                                    Html.toHtml(descrizioni.get(position)));
                            startActivity(intent);
                        }
                    });
                }
            }
        }
		if (showLoading == true) {
			mDialog.dismiss();
    }

    public class connectioncalendar extends
            AsyncTask<Void, Void, HashMap<String, ArrayList<Spanned>>> {

        Boolean unknhost = false;
		Boolean showLoading=true;
			public connectioncalendar (Boolean showLoading) {
			this.showLoading=showLoading;
			}

        protected void onCancelled() {
            Intent main = new Intent(MainActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            Toast.makeText(MainActivity.this, R.string.canceledcalendar,
                    Toast.LENGTH_LONG).show();
        }

        public void onPreExecute() {
            if (MainActivity.nointernet == "true" && showLoading == true) {
                mDialog = ProgressDialog.show(MainActivity.this, null,
                        getString(R.string.retrievingEvents), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connectioncalendar.this.cancel(true);
                            }
                        });

            } else if (showLoading == true) {
                mDialog = ProgressDialog.show(MainActivity.this, null,
                        getString(R.string.downloadingEvents), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connectioncalendar.this.cancel(true);
                            }
                        });
            }
        }

        @SuppressLint("SimpleDateFormat")
        public HashMap<String, ArrayList<Spanned>> doInBackground(
                Void... params) {
            Database databaseHelper = new Database(getBaseContext());
            db = databaseHelper.getWritableDatabase();
            HashMap<String, ArrayList<Spanned>> temhashmap = new HashMap<String, ArrayList<Spanned>>();
            ArrayList<Spanned> titoli = new ArrayList<Spanned>();
            ArrayList<Spanned> descrizioni = new ArrayList<Spanned>();
            ArrayList<Spanned> titolib = new ArrayList<Spanned>();
            final String URL = "https://www.messedaglia.it/index.php?option=com_jevents&task=modlatest.rss&format=feed&type=rss&Itemid=127&modid=162";
            String URLE = "https://www.messedaglia.it/caltoxml.php?id=";
            final String ITEM = "item";
            final String TITLE = "title";
            final String DESC = "description";
            Element e, e2 = null;
            String[] outdated = { "newsdate", "calendardate" };
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String now = df.format(c.getTime());
            Cursor date = db.query("lstchk", // The table to query
                    outdated, // The columns to return
                    null, // The columns for the WHERE clause
                    null, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    null // The sort order
            );
            date.moveToFirst();
            String past = date.getString(date.getColumnIndex("calendardate"));
            date.close();
            long l = getTimeDiff(past, now);
            if (l / 10800000 >= 3 && MainActivity.nointernet != "true") {
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(URL);
                if (xml == "UnknownHostException") {
                    unknhost = true;
                    db.close();
                    return temhashmap;
                } else {
                    Document doc = parser.getDomElement(xml);
                    NodeList nl;
                    nl = doc.getElementsByTagName(ITEM);
                    ContentValues values = new ContentValues();
                    Boolean ok = false;
                    HashMap<String, Integer> doppioni = new HashMap<String, Integer>();
                    for (int i = 1; i < nl.getLength(); i++) {
                        HashMap<String, Spanned> map = new HashMap<String, Spanned>();
                        e = (Element) nl.item(i);
                        e2 = (Element) nl.item(i - 1);
                        String idnp = parser.getValue(e, "link");
                        String idnp2 = parser.getValue(e2, "link");
                        char[] idnpa = idnp.toCharArray();
                        char[] idnpa2 = idnp2.toCharArray();
                        String icalr = "";
                        String icalr2 = "";
                        int cnt = 0;
                        int lnt = idnp.length();
                        for (int j = lnt - 1; j > 0; j--) {
                            if (idnpa[j] == '/') {
                                cnt++;
                            }
                            if (cnt == 2) {
                                icalr += idnpa[j - 1];
                                icalr2 += idnpa2[j - 1];
                            }
                            if (cnt > 2) {
                                j = 0;
                            }
                        }
                        char[] icalar = icalr.toCharArray();
                        char[] icalar2 = icalr2.toCharArray();
                        String ical = "";
                        String ical2 = "";
                        for (int k = icalr.length() - 2; k > -1; k--) {
                            ical += icalar[k];
                            ical2 += icalar2[k];
                        }
                        values.put("ical", ical);
                        values.put("_id", i);
                        map.put("ical", Html.fromHtml(ical));
                        if (doppioni.containsKey(ical)) {
                            int d = doppioni.get(ical);
                            doppioni.remove(ical);
                            d++;
                            doppioni.put(ical, d++);
                        } else {
                            doppioni.put(ical, 0);
                        }
                        String tito = parser.getValue(e, TITLE);
                        int n = tito.charAt(0);
                        int n2 = tito.charAt(1);
                        StringBuffer buf = new StringBuffer(tito);

                        switch (tito.charAt(3) + tito.charAt(4)
                                + tito.charAt(5)) {
                            case 282:// GEN
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'F');
                                        buf.setCharAt(4, 'e');
                                        buf.setCharAt(5, 'b');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'F');
                                            buf.setCharAt(4, 'e');
                                            buf.setCharAt(5, 'b');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 269: // FEB
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 50
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 66)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 18));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'M');
                                        buf.setCharAt(4, 'a');
                                        buf.setCharAt(5, 'r');
                                    }
                                } else {
                                    if (n == 50) {
                                        if (n2 + doppioni.get(ical) <= 56) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 8));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'M');
                                            buf.setCharAt(4, 'a');
                                            buf.setCharAt(5, 'r');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 288: // Mar
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'A');
                                        buf.setCharAt(4, 'p');
                                        buf.setCharAt(5, 'r');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'A');
                                            buf.setCharAt(4, 'p');
                                            buf.setCharAt(5, 'r');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 291: // Apr
                                if (n2 + doppioni.get(ical) >= 58) {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical) - 10));
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) == 58)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                    } else {
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'M');
                                        buf.setCharAt(4, 'a');
                                        buf.setCharAt(5, 'g');
                                    }
                                } else {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical)));
                                }
                                break;
                            case 277: // Mag
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'G');
                                        buf.setCharAt(4, 'i');
                                        buf.setCharAt(5, 'u');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'G');
                                            buf.setCharAt(4, 'i');
                                            buf.setCharAt(5, 'u');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 293: // Giu
                                if (n2 + doppioni.get(ical) >= 58) {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical) - 10));
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) == 58)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                    } else {
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'L');
                                        buf.setCharAt(4, 'u');
                                        buf.setCharAt(5, 'g');
                                    }
                                } else {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical)));
                                }
                                break;
                            case 296: // Lug
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'A');
                                        buf.setCharAt(4, 'g');
                                        buf.setCharAt(5, 'g');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'A');
                                            buf.setCharAt(4, 'g');
                                            buf.setCharAt(5, 'o');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 279: // Ago
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'S');
                                        buf.setCharAt(4, 'e');
                                        buf.setCharAt(5, 't');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'S');
                                            buf.setCharAt(4, 'e');
                                            buf.setCharAt(5, 't');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 300: // Set
                                if (n2 + doppioni.get(ical) >= 58) {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical) - 10));
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) == 58)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                    } else {
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'O');
                                        buf.setCharAt(4, 't');
                                        buf.setCharAt(5, 't');
                                    }
                                } else {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical)));
                                }
                                break;
                            case 311: // Ott
                                if (n2 + doppioni.get(ical) == 58) {
                                    if (n + 1 <= 51) {
                                        buf.setCharAt(0, (char) (n + 1));
                                        buf.setCharAt(1, '0');
                                    } else {
                                        buf.setCharAt(1, '1');
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'N');
                                        buf.setCharAt(4, 'o');
                                        buf.setCharAt(5, 'v');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(1, '1');
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'N');
                                            buf.setCharAt(4, 'o');
                                            buf.setCharAt(5, 'v');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                            case 307: // Nov
                                if (n2 + doppioni.get(ical) >= 58) {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical) - 10));
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) == 58)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                    } else {
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'D');
                                        buf.setCharAt(4, 'i');
                                        buf.setCharAt(5, 'c');
                                    }
                                } else {
                                    buf.setCharAt(1,
                                            (char) (n2 + doppioni.get(ical)));
                                }
                                break;
                            case 272: // Dic
                                if (n2 + doppioni.get(ical) >= 58) {
                                    if (n + 1 < 51
                                            || (n + 1 == 51 && n2
                                            + doppioni.get(ical) <= 59)) {
                                        buf.setCharAt(0, (char) (n + 1));

                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 10));

                                    } else {
                                        buf.setCharAt(
                                                1,
                                                (char) (n2 + doppioni.get(ical) - 11));
                                        buf.setCharAt(0, '0');
                                        buf.setCharAt(3, 'G');
                                        buf.setCharAt(4, 'e');
                                        buf.setCharAt(5, 'n');
                                    }
                                } else {
                                    if (n == 51) {
                                        if (n2 + doppioni.get(ical) <= 49) {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical)));
                                        } else {
                                            buf.setCharAt(
                                                    1,
                                                    (char) (n2 + doppioni.get(ical) - 1));
                                            buf.setCharAt(0, '0');
                                            buf.setCharAt(3, 'G');
                                            buf.setCharAt(4, 'e');
                                            buf.setCharAt(5, 'n');
                                        }
                                    } else {
                                        buf.setCharAt(1,
                                                (char) (n2 + doppioni.get(ical)));
                                    }
                                }
                                break;
                        }

                        tito = buf.toString();
                        values.put(TITLE, tito);
                        map.put(TITLE, Html.fromHtml(tito));
                        titoli.add(Html.fromHtml(tito));
                        titolib.add(Html.fromHtml("<b>" + tito + "</b>"));

                        values.put(DESC, parser.getValue(e, DESC));
                        values.put("titleb", "<b>" + tito + "</b>");
                        map.put(DESC, Html.fromHtml(parser.getValue(e, DESC)));
                        descrizioni
                                .add(Html.fromHtml(parser.getValue(e, DESC)));
                        icalarr.add(Html.fromHtml(ical));
                        long newRowId = db.insertWithOnConflict("calendar",
                                null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    }
                    ContentValues nowdb = new ContentValues();
                    nowdb.put("calendardate", now);
                    long samerow = db.update("lstchk", nowdb, null, null);
                    db.close();
                    temhashmap.put("titoli", titoli);
                    temhashmap.put("descrizioni", descrizioni);
                    temhashmap.put("ical", icalarr);
                    temhashmap.put("titolib", titolib);
                    return temhashmap;

                }

            } else {
                String[] clmndata = { "title", "description", "titleb", "ical" };
                String sortOrder = "_id";

                data = db.query("calendar", // The table to query
                        clmndata,
                        // The columns to return
                        null, // The columns for the WHERE clause
                        null, // The values for the WHERE clause
                        null, // don't group the rows
                        null, // don't filter by row groups
                        sortOrder // The sortorder
                );

                for (data.move(0); data.moveToNext(); data.isAfterLast()) {
                    HashMap<String, Spanned> map = new HashMap<String, Spanned>();
                    map.put(TITLE, Html.fromHtml(data.getString(data
                            .getColumnIndex("title"))));
                    map.put(DESC, Html.fromHtml(data.getString(data
                            .getColumnIndex("description"))));
                    map.put("ical", Html.fromHtml(data.getString(data
                            .getColumnIndex("ical"))));

                    titoli.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("title"))));
                    descrizioni.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("description"))));
                    icalarr.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("ical"))));
                    titolib.add(Html.fromHtml(data.getString(data
                            .getColumnIndex("titleb"))));

                }
                data.close();
                db.close();
                temhashmap.put("titoli", titoli);
                temhashmap.put("descrizioni", descrizioni);
                temhashmap.put("ical", icalarr);
                temhashmap.put("titolib", titolib);
                return temhashmap;

            }

        }

        public void onPostExecute(HashMap<String, ArrayList<Spanned>> resultmap) {
            if (unknhost) {
                Toast.makeText(MainActivity.this, R.string.connerr,
                        Toast.LENGTH_LONG).show();
                Intent main = new Intent(MainActivity.this, MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(main);
            } else {
                if (resultmap.size() > 0) {
                    final ArrayList<Spanned> titoli = resultmap.get("titoli");
                    final ArrayList<Spanned> descrizioni = resultmap
                            .get("descrizioni");
                    final ArrayList<Spanned> titolib = resultmap.get("titolib");
                    final ArrayList<Spanned> icalarr = resultmap.get("ical");
                    if (titoli.size()==0) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, PlaceholderFragment.newInstance(12))
                                .commit();
                    } else {
                        ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(
                                MainActivity.this, android.R.layout.simple_list_item_1,
                                titolib);
						SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
						mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						@Override
							public void onRefresh() {
								refreshCalendar();
							}
                        ListView listView = (ListView) rootView.findViewById(android.R.id.list);
                        listView.setAdapter(adapter);

                        registerForContextMenu(findViewById(android.R.id.list));

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parentView,
                                                    View childView, int position, long id) {
                                if (Html.toHtml(descrizioni.get(position)) != "") {
                                    Intent intent = new Intent(MainActivity.this,
                                            ListItemSelectedCalendar.class);
                                    intent.putExtra(TITLE,
                                            Html.toHtml(titoli.get(position)));
                                    intent.putExtra(DESC,
                                            Html.toHtml(descrizioni.get(position)));
                                    intent.putExtra(ICAL,
                                            Html.toHtml(icalarr.get(position)));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this,
                                            R.string.nodescription,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
			if (showLoading == true) {
				mDialog.dismiss();
			}
        }
    }

    @SuppressLint("SimpleDateFormat")
    public class eventparser extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String ical = "https://www.messedaglia.it/caltoxml.php?id=" + idical;
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(ical);
            if (xml == "UnknownHostException") {
            } else {
                Document doc = parser.getDomElement(xml);
                NodeList nl = doc.getElementsByTagName("VEVENT");

                String[] dati = { "", "", "", "", "" };
                Element e = (Element) nl.item(0);
                dati[0] = parser.getValue(e, "SUMMARY");
                int l = parser.getValue(e, "DESCRIPTION").length();
                if (l == 0) {
                    dati[1] = "Nessuna descrizione";
                } else {
                    dati[1] = parser.getValue(e, "DESCRIPTION").substring(4,
                            l - 3);
                }
                dati[2] = parser.getValue(e, "LOCATION");
                dati[3] = parser.getValue(e, "DTSTART");
                dati[4] = parser.getValue(e, "DTEND");
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyyMMdd'T'HHmmss");
                Date fine = null;
                Date inizio = null;
                try {
                    fine = dateFormat.parse(dati[4].toString());
                    inizio = dateFormat.parse(dati[3].toString());
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setType("vnd.android.cursor.item/event")
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                    inizio.getTime())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                    fine.getTime())
                            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,
                                    false)
                            .putExtra(CalendarContract.Events.TITLE, dati[0])
                            .putExtra(CalendarContract.Events.DESCRIPTION, dati[1])
                            .putExtra(CalendarContract.Events.EVENT_LOCATION,
                                    dati[2] + " A. Messedaglia");
                    startActivity(intent);
                } catch (java.text.ParseException e1) {
                    e1.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    Toast.makeText(MainActivity.this, R.string.noapilevel,
                            Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

    }

    public void salvaPanini(View v){
        SharedPreferences prefs = MainActivity.context.getSharedPreferences(
                "paniniauth", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username",((EditText) findViewById(R.id.usernamepanini)).getText().toString());
        editor.putString("password",((EditText) findViewById(R.id.passwordpanini)).getText().toString());
        editor.commit();
        Toast.makeText(this,"Impostazioni correttamente salvate",Toast.LENGTH_LONG).show();
    }

}

