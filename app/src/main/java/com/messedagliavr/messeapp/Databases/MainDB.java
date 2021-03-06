package com.messedagliavr.messeapp.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MainDB extends SQLiteOpenHelper {
    public static final String NOME_DB = "messeapp.db";
    public static final int VERSIONE_DB = 11;

    private static final String CREATE_NEWS = "CREATE TABLE news (_id INTEGER PRIMARY KEY AUTOINCREMENT,title text not null,pubdate text not null,description text not null);";
    private static final String CREATE_CALENDAR = "CREATE TABLE calendar (_id INTEGER PRIMARY KEY AUTOINCREMENT,title text not null,titleb text not null,description text,ical text not null);";
    private static final String CREATE_CLASS = "CREATE TABLE class (fname text);";
    private static final String CREATE_UPDATE = "CREATE TABLE lstchk (newsdate text,calendardate text);";
    private static final String CREATE_SETTVOTI = "CREATE TABLE settvoti (enabled text,username text,password text);";
    private static final String POPULATE_UPDATE = "INSERT INTO lstchk VALUES ('1995-01-19 23:40:20','1995-01-19 23:40:20');";
    private static final String POPULATE_CLASS = "INSERT INTO class VALUES ('novalue');";
    private static final String POPULATE_SETTVOTI = "INSERT INTO settvoti VALUES('false','','');";

    public MainDB(Context context) {
        super(context, NOME_DB, null, VERSIONE_DB);
    }

    public MainDB() {
        super(null, null, null, 0);
    }
    // crea il database se non esiste

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_NEWS);
            db.execSQL(CREATE_CALENDAR);
            db.execSQL(CREATE_UPDATE);
            db.execSQL(POPULATE_UPDATE);
            db.execSQL(CREATE_CLASS);
            db.execSQL(POPULATE_CLASS);
            db.execSQL(CREATE_SETTVOTI);
            db.execSQL(POPULATE_SETTVOTI);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Metodo usato per fare upgrade del DB se il numero di versione nuovo è
     * maggiore del vecchio
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS news;");
            db.execSQL(CREATE_NEWS);
        }
    }
}
