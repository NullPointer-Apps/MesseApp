package com.messedagliavr.messeapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;


public class contacts extends ActionBarActivity implements View.OnTouchListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatti);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View iv = findViewById(R.id.contattibg);
       if (iv != null) {
            iv.setOnTouchListener(this);
       }
       
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
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
                ColorTool ct = new ColorTool();
                int tolerance = 30;
                if (ct.closeMatch(-13893888, touchColor, tolerance)){
                    methodtobcalled = 0;handledHere = true;
                } else { if (ct.closeMatch(Color.BLUE, touchColor, tolerance)) {
                    methodtobcalled = 1;handledHere = true;
                } else { if (ct.closeMatch(Color.RED, touchColor, tolerance)) {
                    methodtobcalled = 2;handledHere = true;
                } else { if (ct.closeMatch(Color.YELLOW, touchColor, tolerance)) {
                    methodtobcalled = 3;handledHere = true;
                } else { if (ct.closeMatch(-65341, touchColor, tolerance)) {
                    methodtobcalled = 4;handledHere = true;
                } else { if (ct.closeMatch(Color.CYAN, touchColor, tolerance)) {
                    methodtobcalled = 5;handledHere = true;
                } else { if (ct.closeMatch(-5526613, touchColor, tolerance)) {
                    methodtobcalled = 6;handledHere = true;
                } else { if (ct.closeMatch(Color.WHITE, touchColor, tolerance)) {
                    methodtobcalled = 7;handledHere = true;
                } else handledHere = false;
                }
                }
                }
                }
                }
                }
                }
                break;

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
        return true;
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
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }

    public void indisucc() {
        String uri = "geo:45.437535,10.99534?q=via+dello+zappatore+2";
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(uri)));
    }
}
