package com.messedagliavr.messeapp.AsyncTasks;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.messedagliavr.messeapp.CalendarActivity;
import com.messedagliavr.messeapp.Databases.MainDB;
import com.messedagliavr.messeapp.ListItemSelectedCalendarActivity;
import com.messedagliavr.messeapp.MainActivity;
import com.messedagliavr.messeapp.Parsers.XMLParser;
import com.messedagliavr.messeapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SEvents extends
        AsyncTask<Void, Void, HashMap<String, ArrayList<Spanned>>> {

    public static final String TITLE = "title";
    public static final String DESC = "description";
    public static final String ICAL = "ical";
    static SwipeRefreshLayout mSwipeRefreshLayout = null;
    public SQLiteDatabase db;
    ProgressDialog mDialog;
    Boolean unknhost = false;
    Boolean isRefresh = false;
    CalendarActivity ca;
    Cursor data;

    public SEvents(CalendarActivity ca) {
        this.ca = ca;
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
        ConnectivityManager connec = (ConnectivityManager) ca.getSystemService(Context.CONNECTIVITY_SERVICE);
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
            MainDB databaseHelper = new MainDB(ca.getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues nowdb = new ContentValues();
            nowdb.put("calendardate", "2012-02-20 15:00:00");
            db.update("lstchk", nowdb, null, null);
            db.close();
            MainActivity.nointernet = "false";
            isRefresh = true;
            SEvents calendar = new SEvents(ca);
            calendar.execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(ca, R.string.noconnection,
                    Toast.LENGTH_LONG).show();
        }
    }


    protected void onCancelled() {
        Intent main = new Intent(ca, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ca.startActivity(main);
        Toast.makeText(ca, R.string.canceledcalendar,
                Toast.LENGTH_LONG).show();
    }

    public void onPreExecute() {
        MainDB databaseHelper = new MainDB(ca.getBaseContext());
        db = databaseHelper.getWritableDatabase();
        String[] outdated = {"newsdate", "calendardate"};
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
        if (mSwipeRefreshLayout == null) {
            mSwipeRefreshLayout = (SwipeRefreshLayout) ca.findViewById(R.id.listview_swipe_refresh_news);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshCalendar();
                }
            });
            mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#fffbb901"), Color.parseColor("#ff1a171b"));
            mSwipeRefreshLayout.setProgressViewOffset(false, 0,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, ca.getResources().getDisplayMetrics()));
        }
        if (isRefresh) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public HashMap<String, ArrayList<Spanned>> doInBackground(
            Void... params) {
        MainDB databaseHelper = new MainDB(ca.getBaseContext());
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
        String[] outdated = {"newsdate", "calendardate"};
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
        if (l / 10800000 >= 3 && !CalendarActivity.nointernet.equals("true")) {
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
                    description = parser.getValue(e, DESC);

                    values.put(DESC, description);
                    values.put("titleb", "<b>" + tito + "</b>");
                    map.put(DESC, Html.fromHtml(description));
                    descrizioni
                            .add(Html.fromHtml(description));
                    CalendarActivity.icalarr.add(Html.fromHtml(ical));
                    long newRowId = db.insertWithOnConflict("calendar",
                            null, values, SQLiteDatabase.CONFLICT_REPLACE);

                }
                ContentValues nowdb = new ContentValues();
                nowdb.put("calendardate", now);
                db.update("lstchk", nowdb, null, null);
                db.close();
                temhashmap.put("titoli", titoli);
                temhashmap.put("descrizioni", descrizioni);
                temhashmap.put("ical", CalendarActivity.icalarr);
                temhashmap.put("titolib", titolib);
                return temhashmap;

            }

        } else {
            String[] clmndata = {"title", "description", "titleb", "ical"};
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
                CalendarActivity.icalarr.add(Html.fromHtml(data.getString(data
                        .getColumnIndex("ical"))));
                titolib.add(Html.fromHtml(data.getString(data
                        .getColumnIndex("titleb"))));

            }
            data.close();
            db.close();
            temhashmap.put("titoli", titoli);
            temhashmap.put("descrizioni", descrizioni);
            temhashmap.put("ical", CalendarActivity.icalarr);
            temhashmap.put("titolib", titolib);
            return temhashmap;

        }

    }

    public void onPostExecute(HashMap<String, ArrayList<Spanned>> resultmap) {
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) ca.findViewById(R.id.listview_swipe_refresh_news);
        if (unknhost) {
            Toast.makeText(ca, R.string.connerr,
                    Toast.LENGTH_LONG).show();
            Intent main = new Intent(ca, MainActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ca.startActivity(main);
        } else {
            if (resultmap.size() > 0) {
                final ArrayList<Spanned> titoli = resultmap.get("titoli");
                final ArrayList<Spanned> descrizioni = resultmap
                        .get("descrizioni");
                final ArrayList<Spanned> titolib = resultmap.get("titolib");
                final ArrayList<Spanned> icalarr = resultmap.get("ical");
                if (titoli.size() == 0) {
                    ca.setContentView(R.layout.noevents);
                } else {
                    ArrayAdapter<Spanned> adapter = new ArrayAdapter<>(
                            ca, android.R.layout.simple_list_item_1,
                            titolib);
                    ListView listView = (ListView) ca.findViewById(R.id.list);
                    listView.setAdapter(adapter);

                    ca.registerForContextMenu(ca.findViewById(R.id.list));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parentView,
                                                View childView, int position, long id) {
                            if (!Html.toHtml(descrizioni.get(position)).equals("")) {
                                Intent intent = new Intent(ca,
                                        ListItemSelectedCalendarActivity.class);
                                intent.putExtra(TITLE,
                                        Html.toHtml(titoli.get(position)));
                                intent.putExtra(DESC,
                                        Html.toHtml(descrizioni.get(position)));
                                intent.putExtra(ICAL,
                                        Html.toHtml(icalarr.get(position)));
                                if (Build.VERSION.SDK_INT >= 21) {
                                    ca.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ca).toBundle());
                                } else {
                                    ca.startActivity(intent);
                                }

                            } else {
                                Toast.makeText(ca,
                                        R.string.nodescription,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);

    }
}
