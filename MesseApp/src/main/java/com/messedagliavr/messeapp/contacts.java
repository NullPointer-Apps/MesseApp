package com.messedagliavr.messeapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class contacts extends Activity implements View.OnTouchListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatti);
        View iv = findViewById(R.id.contattibg);
       if (iv != null) {
            iv.setOnTouchListener(this);
       }
        setDrawer();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            if (mDrawerLayout.isDrawerOpen(findViewById(R.id.left_drawer))==true) {
                mDrawerLayout.closeDrawer(findViewById(R.id.left_drawer));
            } else {
                mDrawerLayout.openDrawer(findViewById(R.id.left_drawer));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //Permette all'utente di aprire il navigatio toccando l'app icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void setDrawer(){
        String[] drawerItems={getString(R.string.settings),getString(R.string.contatti),getString(R.string.suggestion),getString(R.string.Info),getString(R.string.exit)};
        switch(MainActivity.layoutid){
            case R.id.info:
                mDrawerList = (ListView) this.findViewById(R.id.left_drawer);
                break;
            case R.id.activity_main:
                mDrawerList = (ListView) this.findViewById(R.id.left_drawer);
                break;
            case R.id.settings:
                mDrawerList = (ListView) this.findViewById(R.id.left_drawer);
                break;
            case R.id.contatti:
                mDrawerList = (ListView) this.findViewById(R.id.left_drawer);
                break;
        }
        Toast.makeText(this, mDrawerList.toString(), Toast.LENGTH_LONG);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, drawerItems));

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                doNavigationItem(position);
            }
        });
        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {}
            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {}
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            getActionBar().setHomeButtonEnabled(true);
        }
    }

    public void doNavigationItem(int position){
        switch (position) {
            case 0:
                setContentView(R.layout.settings);
                MainActivity.layoutid = R.id.settings;
                EditText user = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
                Button save = (Button) findViewById(R.id.savesett);
                ToggleButton toggle = (ToggleButton) findViewById(R.id.saveenabled);
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
                setDrawer();
                break;
            case 3:
                PackageInfo pinfo = null;
                try {
                    pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String versionName = pinfo.versionName;
                setContentView(R.layout.info);
                TextView vername = (TextView) findViewById(R.id.versionname);
                vername.setText(versionName);
                MainActivity.layoutid = R.id.info;
                setDrawer();
                break;
            case 4:
                super.finish();
                break;
            case 1:
                startActivity(new Intent(this, contacts.class));
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.suggestion));
                final EditText input = new EditText(this);
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
                                        "mailto", "null.p.apps@gmail.com",
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
        }
    }

    public boolean onTouch(View v, MotionEvent ev) {
        boolean handledHere = false;

        final int action = ev.getAction();

        final int evX = (int) ev.getX();
        final int evY = (int) ev.getY();
        int methodtobcalled = -1;

        // If we cannot find the imageView, return.
        // When the action is Down, see if we should show the "pressed" image
        // for the default image.
        // We do this when the default image is showing. That condition is
        // detectable by looking at the
        // tag of the view. If it is null or contains the resource number of the
        // default image, display the pressed image.
        // Now that we know the current resource being displayed we can handle
        // the DOWN and UP events.

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                // On the UP, we do the click action.
                // The hidden image (image_areas) has three different hotspots on
                // it.
                // The colors are red, blue, and yellow.
                // Use image_areas to determine which region the user touched.
                int touchColor = getHotspotColor(R.id.image_areas, evX, evY);
                // Compare the touchColor to the expected values. Switch to a
                // different image, depending on what color was touched.
                // Note that we use a Color Tool object to test whether the observed
                // color is close enough to the real color to
                // count as a match. We do this because colors on the screen do not
                // match the map exactly because of scaling and
                // varying pixel density.
                System.out.println(touchColor);
                ColorTool ct = new ColorTool();
                int tolerance = 25;
                if (ct.closeMatch(-13893888, touchColor, tolerance))
                    methodtobcalled = 0;
                else if (ct.closeMatch(Color.BLUE, touchColor, tolerance))
                    methodtobcalled = 1;
                else if (ct.closeMatch(Color.RED, touchColor, tolerance))
                    methodtobcalled = 2;
                else if (ct.closeMatch(Color.YELLOW, touchColor, tolerance))
                    methodtobcalled = 3;
                else if (ct.closeMatch(-65341, touchColor, tolerance))
                    methodtobcalled = 4;
                else if (ct.closeMatch(Color.CYAN, touchColor, tolerance))
                    methodtobcalled = 5;
                else if (ct.closeMatch(-5526613, touchColor, tolerance))
                    methodtobcalled = 6;
                else if (ct.closeMatch(Color.BLACK, touchColor, tolerance))
                    methodtobcalled = 7;

                // If the next image is the same as the last image, go back to the
                // default.
                // toast ("Current image: " + currentResource + " next: " +
                // nextImage);
                handledHere = true;
                break;

            default:
                handledHere = false;
        } // end switch

        if (handledHere) {
            switch (methodtobcalled) {
                case 0:
                    maildir();
                    break;
                case 1:
                    mailvice();
                    break;
                case 2:
                    maildid();
                    break;
                case 3:
                    numsede();
                    break;
                case 4:
                    numsede2();
                    break;
                case 5:
                    numsucc();
                    break;
                case 6:
                    indisede();
                    break;
                case 7:
                    indisucc();
                    break;
            }
        }
        return handledHere;
    }

    /** Get the color from the hotspot image at point x-y. */
    public int getHotspotColor(int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
        if (img == null) {
            Log.d("ImageAreasActivity", "Hot spot image not found");
            return 0;
        } else {
            img.setDrawingCacheEnabled(true);
            Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
            if (hotspots == null) {
                Log.d("ImageAreasActivity", "Hot spot bitmap was not created");
                return 0;
            } else {
                img.setDrawingCacheEnabled(false);
                return hotspots.getPixel(x, y);
            }
        }
    }

    public void maildir() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "dirigente@messedagliavr.it", null));
        startActivity(emailIntent);
    }

    public void mailvice() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "vicepreside@messedagliavr.it", null));
        startActivity(emailIntent);
    }

    public void maildid() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "didattica@messedagliavr.it", null));
        startActivity(emailIntent);
    }

    public void numsede() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:045596432"));
        startActivity(dialIntent);
    }

    public void numsede2() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:0458034772"));
        startActivity(dialIntent);
    }

    public void numsucc() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:0458004954"));
        startActivity(dialIntent);
    }

    public void indisede() {
        String uri = "geo:45.437535,10.99534?q=via+don+gaspare+bertoni+3b";
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

    public void indisucc() {
        String uri = "geo:45.437535,10.99534?q=via+dello+zappatore+2";
        startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

private class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        switch (position) {
            case 0:
                setContentView(R.layout.settings);
                MainActivity.layoutid = R.id.settings;
                EditText user = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
                Button save = (Button) findViewById(R.id.savesett);
                ToggleButton toggle = (ToggleButton) findViewById(R.id.saveenabled);
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
                setDrawer();
                break;
            case 3:
                PackageInfo pinfo = null;
                try {
                    pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String versionName = pinfo.versionName;
                setContentView(R.layout.info);
                TextView vername = (TextView) findViewById(R.id.versionname);
                vername.setText(versionName);
                MainActivity.layoutid = R.id.info;
                setDrawer();
                break;
            case 4:
                contacts.super.finish();
                break;
            case 1:
                startActivity(new Intent(contacts.this, contacts.class));
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(contacts.this);
                builder.setTitle(getString(R.string.suggestion));
                final EditText input = new EditText(contacts.this);
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
                                        "mailto", "null.p.apps@gmail.com",
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
        }
    }
}
}