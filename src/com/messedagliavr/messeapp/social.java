package com.messedagliavr.messeapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.analytics.tracking.android.EasyTracker;

public class social extends Activity implements View.OnTouchListener {
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social);
		View iv = findViewById(R.id.social);
		if (iv != null) {
			iv.setOnTouchListener(this);
		}
	}

	public boolean onTouch(View v, MotionEvent ev) {
		final int action = ev.getAction();
		final int evX = (int) ev.getX();
		final int evY = (int) ev.getY();
		if (action == MotionEvent.ACTION_DOWN) {
			int touchColor = getHotspotColor(R.id.image_areas, evX, evY);
			ColorTool ct = new ColorTool();
			int tolerance = 25;
			if (ct.closeMatch(Color.RED, touchColor, tolerance))
				facebook();
			else if (ct.closeMatch(Color.GREEN, touchColor, tolerance))
				youtube();
			else if (ct.closeMatch(Color.CYAN, touchColor, tolerance))
				moodle();
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

	public void youtube() {
		Intent youtube = new Intent(Intent.ACTION_VIEW);
		youtube.setData(Uri.parse("http://www.youtube.com/user/MessedagliaWeb"));
		startActivity(youtube);
	}

	public void facebook() {
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

	public void moodle() {
		Intent moodle = new Intent(Intent.ACTION_VIEW);
		moodle.setData(Uri.parse("http://corsi.messedaglia.it"));
		startActivity(moodle);
	}
}
