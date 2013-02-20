package com.messedagliavr.messeapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	public static final String NOME_DB = "messeapp.db";
	public static final int VERSIONE_DB = 1;

	private static final String CREATE_NEWS = "CREATE TABLE news (id INTEGER PRIMARY KEY AUTOINCREMENT,title text not null,description text not null);";
	private static final String CREATE_CALENDAR = "CREATE TABLE calendar (id INTEGER PRIMARY KEY AUTOINCREMENT,title text not null,description text);";
	private static final String CREATE_UPDATE = "CREATE TABLE lstchk (newsdate text,calendardate text);";
	private static final String POPULATE_UPDATE = "INSERT INTO lstchk VALUES ('1995-01-19 23:40:20','1995-01-19 23:40:20');";

	public Database(Context context) {
		super(context, NOME_DB, null, VERSIONE_DB);
	}

	// crea il database se non esiste

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.err.println("Dentro create tables - ");
		try {
		db.execSQL(CREATE_NEWS);
		db.execSQL(CREATE_CALENDAR);
		db.execSQL(CREATE_UPDATE);
		db.execSQL(POPULATE_UPDATE);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Metodo usato per fare upgrade del DB se il numero di versione nuovo �
	 * maggiore del vecchio
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.err.println("Dentro update tables");
		db.execSQL("DROP TABLE IF EXISTS ");
		onCreate(db);
	}

}