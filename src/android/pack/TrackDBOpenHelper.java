/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package android.pack;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TrackDBOpenHelper extends SQLiteOpenHelper {

    private final String LOG_LABEL = getClass().getSimpleName();
    public static final String DATABASE_NAME = "app.db";
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_TABLE_TRACKS = "tracks";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String USER_ID = "user";
    public static final String START = "start_time";
    public static final String END = "end_time";
    public static final String DATE = "date";
    public static final String SYNCED = "synced";
    
    public static final String DATABASE_TABLE_ITEMS = "items";
    public static final String ID_ITEM = "id";
    public static final String ID_TRACK = "trackid";
    public static final String LONG = "long";
    public static final String LAT = "lat";
    public static final String TIMESTAMP = "timestamp";
    
    private static final String CREATE_TRACKS = "create table "
            + DATABASE_TABLE_TRACKS + " (" + ID
            + " integer primary key autoincrement, "
            + NAME + " text not null, "
            + USER_ID + " text not null, "
            + START + " text not null, "
            + END + " text not null, "
            + DATE + " text not null, "
            + SYNCED + " text not null DEFAULT \'0\');";
    
    private static final String CREATE_ITEMS = "create table "
            + DATABASE_TABLE_ITEMS + " (" + ID_ITEM
            + " integer primary key autoincrement, "
            + ID_TRACK + " integer not null, "
            + LONG + " text not null, "
            + LAT + " text not null, "
            + TIMESTAMP + " text not null);";

    public TrackDBOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRACKS);
        db.execSQL(CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(LOG_LABEL, "Updgrading database, removing old data");
        db.execSQL("DROP TABLE IF EXISTS tracks");
        db.execSQL(CREATE_TRACKS);
        db.execSQL("DROP TABLE IF EXISTS items");
        db.execSQL(CREATE_ITEMS);
    }

    public Cursor fetchAllProducts() {
        String[] result_columns = new String[]{
            TrackDBOpenHelper.ID,
            TrackDBOpenHelper.USER_ID,
            TrackDBOpenHelper.NAME,
            TrackDBOpenHelper.START,
            TrackDBOpenHelper.END,
            TrackDBOpenHelper.DATE,
            TrackDBOpenHelper.SYNCED,
        };
        String where = null;
        String whereArgs[] = null;
        String groupBy = null;
        String having = null;
        String order = null;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TrackDBOpenHelper.DATABASE_TABLE_TRACKS,
                result_columns, where, whereArgs, groupBy, having, order);
        return cursor;
    }
}
