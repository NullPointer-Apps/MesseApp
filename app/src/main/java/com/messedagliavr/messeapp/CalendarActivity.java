package com.messedagliavr.messeapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.messedagliavr.messeapp.Databases.MainDB;
import com.messedagliavr.messeapp.Parsers.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {
    ProgressDialog mDialog;
    public SQLiteDatabase db;
    static Window window;
    public Cursor data;
    public static final String TITLE = "title";
    public static final String DESC = "description";
    public static final String ICAL = "ical";
    public static String nointernet;
    public String idical = null;
    public ArrayList<Spanned> icalarr = new ArrayList<Spanned>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.list_item);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.eventi));
		if (Build.VERSION.SDK_INT >= 21) {
            window= getWindow();
            window.setEnterTransition(new Slide(Gravity.BOTTOM).excludeTarget(android.R.id.statusBarBackground,true).excludeTarget(android.R.id.navigationBarBackground,true));
            window.setExitTransition(new Slide(Gravity.TOP).excludeTarget(android.R.id.statusBarBackground,true).excludeTarget(android.R.id.navigationBarBackground,true));
        }
        if (CheckInternet()) {
            nointernet = "false";
				new connectioncalendar(true).execute();
        } else {
            String[] outdated = {"newsdate", "calendardate"};
            MainDB databaseHelper = new MainDB(getBaseContext());
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
				if (Build.VERSION.SDK_INT >= 21) {
					new connectioncalendar(false).execute();
				} else {
					new connectioncalendar(true).execute();
				}
            } else {
                Toast.makeText(this,
                        R.string.noconnection, Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    private Long getTimeDiff(String time, String curTime) throws ParseException {
        Date curDate = null;
        Date oldDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            curDate = formatter.parse(curTime);
            oldDate = formatter.parse(time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        assert oldDate != null;
        long oldMillis = oldDate.getTime();
        long curMillis = curDate.getTime();
        return curMillis - oldMillis;
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

    public void refreshCalendar() {
        if (CheckInternet()) {
            MainDB databaseHelper = new MainDB(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues nowdb = new ContentValues();
            nowdb.put("calendardate", "2012-02-20 15:00:00");
            db.update("lstchk", nowdb, null, null);
            db.close();
            MainActivity.nointernet = "false";
            connectioncalendar calendar = new connectioncalendar(false);
            calendar.execute();
        } else {
            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
            mSwipeRefreshLayout.setRefreshing(false);
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

    @SuppressLint("SimpleDateFormat")
    public class eventparser extends AsyncTask<Void, Void, Void> {
        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        protected Void doInBackground(Void... params) {
            String ical = "https://www.messedaglia.it/caltoxml.php?id=" + idical;
            XMLParser parser = new XMLParser();
            String xml = parser.getXmlFromUrl(ical);
            if (!xml.equals("UnknownHostException")) {
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
                Date fine;
                Date inizio;
                try {
                    fine = dateFormat.parse(dati[4]);
                    inizio = dateFormat.parse(dati[3]);
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
                    Toast.makeText(CalendarActivity.this, R.string.noapilevel,
                            Toast.LENGTH_LONG).show();
                }
            }
            return null;
        }

    }

    public class connectioncalendar extends
            AsyncTask<Void, Void, HashMap<String, ArrayList<Spanned>>> {

        Boolean unknhost = false;
        Boolean showLoading=true;
        public connectioncalendar (Boolean showLoading) {
            this.showLoading=showLoading;
        }

        protected void onCancelled() {
            Intent main = new Intent(CalendarActivity.this, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main);
            Toast.makeText(CalendarActivity.this, R.string.canceledcalendar,
                    Toast.LENGTH_LONG).show();
        }

        public void onPreExecute() {
			MainDB databaseHelper = new MainDB(getBaseContext());
            db = databaseHelper.getWritableDatabase();
			String[] outdated = {"newsdate", "calendardate"};
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
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
            db.close();
            long l = getTimeDiff(past, now);
            if (nointernet.equals("true") && showLoading && l / 10800000 >= 3) {
                mDialog = ProgressDialog.show(CalendarActivity.this, null,
                        getString(R.string.retrievingEvents), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connectioncalendar.this.cancel(true);
                            }
                        });

            } else if (showLoading && l / 10800000 >= 3) {
                mDialog = ProgressDialog.show(CalendarActivity.this, null,
                        getString(R.string.downloadingEvents), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connectioncalendar.this.cancel(true);
                            }
                        });
            } else if (past.equals("1995-01-19 23:40:20")){
                mDialog = ProgressDialog.show(CalendarActivity.this, null,
                        getString(R.string.downloadingNews), true, true,
                        new DialogInterface.OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                connectioncalendar.this.cancel(true);
                            }
                        });
            } else {
                showLoading=false;
            }
        }

        @SuppressLint("SimpleDateFormat")
        public HashMap<String, ArrayList<Spanned>> doInBackground(
                Void... params) {
            MainDB databaseHelper = new MainDB(getBaseContext());
            db = databaseHelper.getWritableDatabase();
            HashMap<String, ArrayList<Spanned>> temhashmap = new HashMap<String, ArrayList<Spanned>>();
            ArrayList<Spanned> titoli = new ArrayList<>();
            ArrayList<Spanned> descrizioni = new ArrayList<>();
            ArrayList<Spanned> titolib = new ArrayList<>();
            final String URL = "https://www.messedaglia.it/index.php?option=com_jevents&task=modlatest.rss&format=feed&type=rss&Itemid=127&modid=162";
            final String ITEM = "item";
            final String TITLE = "title";
            final String DESC = "description";
            Element e, e2;
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
            if (l / 10800000 >= 3 && !nointernet.equals("true")) {
                XMLParser parser = new XMLParser();
                String xml = parser.getXmlFromUrl(URL);
                if (xml.equals("UnknownHostException")) {
                    unknhost = true;
                    db.close();
                    return temhashmap;
                } else {
                    Document doc = parser.getDomElement(xml);
                    NodeList nl;
                    nl = doc.getElementsByTagName(ITEM);
                    ContentValues values = new ContentValues();
                    String description;
                    HashMap<String, Integer> doppioni = new HashMap<>();
                    for (int i = 1; i < nl.getLength(); i++) {
                        HashMap<String, Spanned> map = new HashMap<>();
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
                        for (int k = icalr.length() - 2; k > -1; k--) {
                            ical += icalar[k];
                        }
                        values.put("ical", ical);
                        values.put("_id", i);
                        map.put("ical", Html.fromHtml(ical));
                        if (doppioni.containsKey(ical)) {
                            int d = doppioni.get(ical);
                            doppioni.remove(ical);
                            d++;
                            doppioni.put(ical, d);
                        } else {
                            doppioni.put(ical, 0);
                        }
                        String tito = parser.getValue(e, TITLE);
                        int n = tito.charAt(0);
                        int n2 = tito.charAt(1);
                        StringBuilder buf = new StringBuilder(tito);

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
                        description = parser.getValue(e,DESC);

                        values.put(DESC, description);
                        values.put("titleb", "<b>" + tito + "</b>");
                        map.put(DESC, Html.fromHtml(description));
                        descrizioni
                                .add(Html.fromHtml(description));
                        icalarr.add(Html.fromHtml(ical));
                        long newRowId = db.insertWithOnConflict("calendar",
                                null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    }
                    ContentValues nowdb = new ContentValues();
                    nowdb.put("calendardate", now);
                    db.update("lstchk", nowdb, null, null);
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
                    HashMap<String, Spanned> map = new HashMap<>();
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
            SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.listview_swipe_refresh_layout);
            if (unknhost) {
                Toast.makeText(CalendarActivity.this, R.string.connerr,
                        Toast.LENGTH_LONG).show();
                Intent main = new Intent(CalendarActivity.this, MainActivity.class);
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
                        setContentView(R.layout.noevents);
                    } else {
                        ArrayAdapter<Spanned> adapter = new ArrayAdapter<>(
                                CalendarActivity.this, android.R.layout.simple_list_item_1,
                                titolib);
                        ListView listView = (ListView) findViewById(R.id.list);
                        listView.setAdapter(adapter);

                        registerForContextMenu(findViewById(R.id.list));
                        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                refreshCalendar();
                            }
                        });
                        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#fffbb901"),Color.parseColor("#ff1a171b"));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parentView,
                                                    View childView, int position, long id) {
                                if (!Html.toHtml(descrizioni.get(position)).equals("")) {
                                    Intent intent = new Intent(CalendarActivity.this,
                                            ListItemSelectedCalendarActivity.class);
                                    intent.putExtra(TITLE,
                                            Html.toHtml(titoli.get(position)));
                                    intent.putExtra(DESC,
                                            Html.toHtml(descrizioni.get(position)));
                                    intent.putExtra(ICAL,
                                            Html.toHtml(icalarr.get(position)));
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CalendarActivity.this).toBundle());
                                    } else {
                                        startActivity(intent);
                                    }

                                } else {
                                    Toast.makeText(CalendarActivity.this,
                                            R.string.nodescription,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
            if (showLoading) {
                mDialog.dismiss();
            }
            mSwipeRefreshLayout.setRefreshing(false);

        }
    }
}
