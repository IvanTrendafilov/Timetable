package uk.ac.ed.timetable.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
public class DBHelper extends SQLiteOpenHelper {
private static final String DATABASE_NAME="/data/data/uk.ac.ed.timetable/databases/TimetableDatabase.db";
public static final String TITLE="title";
public static final String VALUE="value";
public DBHelper(Context context) {
	super(context, DATABASE_NAME, null, 1);
	}
@Override
public void onCreate(SQLiteDatabase db) {
	}
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}