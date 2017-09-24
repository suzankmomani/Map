package map.android.com.map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import map.android.com.map.Data.Contract;
import map.android.com.map.Data.DbHelper;

/**
 * Created by suzan on 29/07/17.
 */

public class TestDB extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }


    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(Contract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(Contract.PlaceEntry.TABLE_NAME);

        mContext.deleteDatabase(DbHelper.DATABASE_NAME);
        SQLiteDatabase db = new DbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without both the location entry and place entry tables",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + Contract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(Contract.LocationEntry._ID);
        locationColumnHashSet.add(Contract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(Contract.LocationEntry.COLUMN_COORD_LNG);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testLocationTable() {
        insertLocation();
    }

    public void testPlaceTable() {
        long locationRowId = insertLocation();

        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues placeValues = TestUtilities.createPlaceValues(locationRowId);

        long placeRowId = db.insert(Contract.PlaceEntry.TABLE_NAME, null, placeValues);
        assertTrue(placeRowId != -1);

        Cursor placeCursor = db.query(
                Contract.PlaceEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        assertTrue("Error: No Records returned from location query", placeCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("testInsertReadDb placeEntry failed to validate",
                placeCursor, placeValues);

        assertFalse("Error: More than one record returned from place query",
                placeCursor.moveToNext());

        placeCursor.close();
        dbHelper.close();
    }


    public long insertLocation() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(Contract.LocationEntry.TABLE_NAME, null, testValues);

        assertTrue(locationRowId != -1);
        Cursor cursor = db.query(
                Contract.LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed",
                cursor, testValues);

        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        cursor.close();
        db.close();
        return locationRowId;
    }
}