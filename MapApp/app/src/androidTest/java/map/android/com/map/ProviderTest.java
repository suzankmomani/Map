package map.android.com.map;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import map.android.com.map.Data.Contract;
import map.android.com.map.Data.Contract.LocationEntry;
import map.android.com.map.Data.Contract.PlaceEntry;
import map.android.com.map.Data.DbHelper;
import map.android.com.map.Data.Provider;

/**
 * Created by suzan on 29/07/17.
 */

public class ProviderTest  extends AndroidTestCase {

    public static final String LOG_TAG = ProviderTest.class.getSimpleName();


    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                PlaceEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Place table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                Provider.class.getName());
        try {
             ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: Provider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + Contract.CONTENT_AUTHORITY,
                    providerInfo.authority, Contract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: Provider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        String type = mContext.getContentResolver().getType(PlaceEntry.CONTENT_URI);
        assertEquals("Error: the PlaceEntry CONTENT_URI should return PlaceEntry.CONTENT_TYPE",
                PlaceEntry.CONTENT_TYPE, type);

        String testName = "suzan momani";
        type = mContext.getContentResolver().getType(
                PlaceEntry.buildPlaceName(testName));
        assertEquals("Error: the PlaceEntry CONTENT_URI with name should return PlaceEntry.CONTENT_TYPE",
                PlaceEntry.CONTENT_TYPE, type);


        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
                LocationEntry.CONTENT_TYPE, type);
    }

    public void testBasicPlaceQuery() {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId = TestUtilities.insertNorthPoleLocationValues(mContext);

        ContentValues placeValues = TestUtilities.createPlaceValues(locationRowId);

        long placeRowId = db.insert(PlaceEntry.TABLE_NAME, null, placeValues);
        assertTrue("Unable to Insert PlaceEntry into the Database", placeRowId != -1);

        db.close();

        Cursor placeCursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicPlaceQuery", placeCursor, placeValues);
    }

    public void testBasicLocationQueries() {
        DbHelper dbHelper = new DbHelper(mContext);;

        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicLocationQueries, location query", locationCursor, testValues);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Location Query did not properly set NotificationUri",
                    locationCursor.getNotificationUri(), LocationEntry.CONTENT_URI);
        }
    }

    public void testUpdateLocation() {
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(LocationEntry._ID, locationRowId);
        updatedValues.put(LocationEntry.COLUMN_COORD_LAT, "64.7488");

        Cursor locationCursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, updatedValues, LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,   // projection
                LocationEntry._ID + " = " + locationRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating location entry update.",
                cursor, updatedValues);

        cursor.close();
    }


     public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);

        ContentValues placeValues = TestUtilities.createPlaceValues(locationRowId);
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, tco);

        Uri placeInsertUri = mContext.getContentResolver()
                .insert(PlaceEntry.CONTENT_URI, placeValues);
        assertTrue(placeInsertUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor placeCursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating PlaceEntry insert.",
                placeCursor, placeValues);

        placeValues.putAll(testValues);

        placeCursor = mContext.getContentResolver().query(
                PlaceEntry.buildPlaceName(TestUtilities.TEST_NAME),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Place and Location Data.",
                placeCursor, placeValues);


    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver locationObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LocationEntry.CONTENT_URI, true, locationObserver);

        TestUtilities.TestContentObserver placeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, placeObserver);

        deleteAllRecordsFromProvider();

        locationObserver.waitForNotificationOrFail();
        placeObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(locationObserver);
        mContext.getContentResolver().unregisterContentObserver(placeObserver);
    }


    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertPlaceValues(long locationRowId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < 10; i++ ) {
            ContentValues placeValues = new ContentValues();
            placeValues.put(Contract.PlaceEntry.COLUMN_LOC_KEY, locationRowId);
            placeValues.put(PlaceEntry.COLUMN_NAME, "suzan "+ i);
            placeValues.put(PlaceEntry.COLUMN_DESCRIPTION,"desc "+i );
            placeValues.put(PlaceEntry.COLUMN_IMAGE,"");
            placeValues.put(PlaceEntry.COLUMN_NUMBER_OF_RATINGS,12);
            placeValues.put(PlaceEntry.COLUMN_DESCRIPTION_LONG,"test");
            placeValues.put(PlaceEntry.COLUMN_RATING,19.2);
            returnContentValues[i] = placeValues;
        }
        return returnContentValues;
    }

    public void testBulkInsert() {
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating LocationEntry.",
                cursor, testValues);

        ContentValues[] bulkInsertContentValues = createBulkInsertPlaceValues(locationRowId);

        TestUtilities.TestContentObserver placeObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(PlaceEntry.CONTENT_URI, true, placeObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(PlaceEntry.CONTENT_URI, bulkInsertContentValues);

        placeObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(placeObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        cursor = mContext.getContentResolver().query(
                PlaceEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating PlaceEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}