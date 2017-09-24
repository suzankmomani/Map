package map.android.com.map.Data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import map.android.com.map.Data.Contract.LocationEntry;
import map.android.com.map.Data.Contract.PlaceEntry;

/**
 * Created by suzan on 29/07/17.
 */

/**
 * Manages a local database for places data.
 */
public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "map.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LNG + " REAL NOT NULL " +
                " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +
                PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                PlaceEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                PlaceEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PlaceEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                PlaceEntry.COLUMN_RATING + " DECIMAL NOT NULL," +
                PlaceEntry.COLUMN_NUMBER_OF_RATINGS + " INTEGER NOT NULL," +
                PlaceEntry.COLUMN_DESCRIPTION_LONG + " TEXT NOT NULL," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + PlaceEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "));" ;

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        /*in case anychanges made on the schema, change the version and it will drop the old tables*/
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}