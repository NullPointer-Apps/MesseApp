package com.messedagliavr.messeapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.messedagliavr.messeapp.Databases.MainDB;

public class SettingsActivity extends AppCompatActivity {

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settings);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(getString(R.string.settings));
        EditText user = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        CheckBox check = (CheckBox) findViewById(R.id.checkBox1);
        Button save = (Button) findViewById(R.id.savesett);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.saveenabled);
        MainDB databaseHelpersettings = new MainDB(this);
        SQLiteDatabase dbsettings = databaseHelpersettings.getWritableDatabase();
        String[] columnssettings = {"enabled", "username", "password"};
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
    }


    public void onSaveClicked(View view) {
        EditText usert = (EditText) findViewById(R.id.username);
        EditText passwordt = (EditText) findViewById(R.id.password);
        String username = usert.getText().toString();
        String password = passwordt.getText().toString();
        MainDB databaseHelper = new MainDB(getBaseContext());
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("enabled", "true");
        values.put("username", username);
        values.put("password", password);
        @SuppressWarnings("unused")
        long samerow = db.update("settvoti", values, null, null);
        db.close();
        Snackbar
                .make(findViewById(R.id.coordinator_settings), R.string.settingssaved, Snackbar.LENGTH_LONG)
                .show();
    }

    public void onToggleClicked(View view) {
        ToggleButton toggle = (ToggleButton) findViewById(R.id.saveenabled);
        boolean on = toggle.isChecked();
        EditText user = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);
        Button save = (Button) findViewById(R.id.savesett);
        CheckBox checkbox = (CheckBox) findViewById(R.id.checkBox1);
        if (on) {
            MainDB databaseHelper = new MainDB(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String[] columns = {"username", "password"};
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
            MainDB databaseHelper = new MainDB(getBaseContext());
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("enabled", "false");
            @SuppressWarnings("unused")
            long samerow = db.update("settvoti", values, null, null);
            db.close();
            Snackbar
                    .make(findViewById(R.id.coordinator_settings), R.string.noautologin, Snackbar.LENGTH_LONG)
                    .show();
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
        password.setSelection(password.getText().length());
    }

}
