package edu.sjsu.android.googlemapandsqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationsDB extends SQLiteOpenHelper {
    /* 1a-f. define the following variables */
    protected static final String ID_COLUMN = "_id";
    protected static final String LAT_COLUMN = "latitude";
    protected static final String LNG_COLUMN = "longitude";
    protected static final String ZOOM_LEVEL_COLUMN = "zoom";
    protected static final String DATABASE_TABLE = "locations";
    private static final String DATABASE_NAME = "locationsDatabase";
    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    " %s integer primary key autoincrement, " +
                    " %s double," +
                    " %s double," +
                    " %s text)",
            DATABASE_TABLE, ID_COLUMN, LAT_COLUMN, LNG_COLUMN, ZOOM_LEVEL_COLUMN);

    public LocationsDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * 2a. A callback method that is invoked when the method
     * getReadableDatabase()/getWritableDatabase() is called
     * provided the database doesn’t exist.
     *
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }

    /**
     * 2b. A method that inserts a new location to the table.
     *
     * @param contentValues location detail
     * @return the id
     */
    public long insert(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DATABASE_TABLE, null, contentValues);
    }

    /**
     * 2c. A method that deletes all locations from the table.
     *
     * @return number of locations deleted
     */
    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(DATABASE_TABLE, null, null);
    }

    /**
     * 2d. A method that returns all the locations from the table.
     *
     * @return Cursor
     */
    public Cursor getAllLocations() {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(DATABASE_TABLE,
                new String[]{ID_COLUMN, LAT_COLUMN, LNG_COLUMN, ZOOM_LEVEL_COLUMN},
                null, null, null, null, null);
    }

}