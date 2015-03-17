package com.messedagliavr.messeapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.messedagliavr.messeapp.Utilities.SystemBarTintManager;

/**
 * Created by Ambrof on 17/03/15.
 */
public class SocialActivity extends ActionBarActivity {
    static Window window;
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Social");
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            // enable navigation bar tint
            tintManager.setNavigationBarTintEnabled(true);
            //tintManager.setTintColor(Color.parseColor("#ab46e5"));
            tintManager.setTintColor(Color.parseColor("#AFAFAF"));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window= getWindow();
            window.setEnterTransition(new Slide(Gravity.BOTTOM));
            window.setExitTransition(new Slide(Gravity.TOP));
        }
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
}
