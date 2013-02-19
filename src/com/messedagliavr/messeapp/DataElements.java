package com.messedagliavr.messeapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataElements {
	public static final String TITOLO = "titolo";
	//public static final String DESCRIZIONE = "descrizione";
	public static final String ID = "id";

	public static final String TABELLA = "element";
	public static final String[] COLONNE = new String[] { TITOLO, /*DESCRIZIONE,*/
			ID };

	public static void insertElement(SQLiteDatabase db, String titolo/*,
			String descrizione*/) {
		ContentValues v = new ContentValues();
		v.put(TITOLO, titolo);
		//v.put(DESCRIZIONE, descrizione);

		db.insert(TABELLA, null, v);
	}

	public static Cursor getAllElements(SQLiteDatabase db) {
		return db.query(TABELLA, COLONNE, null, null, null, null, null);
	}

	public static boolean deleteElement(SQLiteDatabase db, long id) {
		return db.delete(TABELLA, ID + "=" + id, null) > 0;
	}

	public static Cursor getElement(SQLiteDatabase db, long id)
			throws SQLException {
		Cursor c = db.query(true, TABELLA, COLONNE, ID + "=" + id, null, null,
				null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public static boolean updateElement(SQLiteDatabase db, long id,
			String titolo, String descrizione) {
		ContentValues v = new ContentValues();
		v.put(TITOLO, titolo);
		//v.put(DESCRIZIONE, descrizione);

		return db.update(TABELLA, v, ID + "=" + id, null) > 0;
	}

}