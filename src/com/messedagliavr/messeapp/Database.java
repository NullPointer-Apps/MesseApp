package com.messedagliavr.messeapp;

import java.util.Calendar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
	public static final String NOME_DB = "data";
	static Calendar rightNow = Calendar.getInstance();
	public static final int VERSIONE_DB = rightNow.get(Calendar.DAY_OF_WEEK_IN_MONTH);;

	private static final String CREATE_TABLE = "create table (" + DataElements.TITOLO + " TEXT,"/*+ DataElements.DESCRIZIONE + " TEXT"*/ + ");";

	public Database(Context context) {
		super(context, NOME_DB, null, VERSIONE_DB);
	}

	// crea il database se non esiste

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.err.println("Dentro create tables - " + Calendar.DAY_OF_WEEK_IN_MONTH);
		try {
		db.execSQL(CREATE_TABLE);
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
		System.err.println("Dentro update tables");
		db.execSQL("DROP TABLE IF EXISTS " + DataElements.TABELLA);
		onCreate(db);
	}

}