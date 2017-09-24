package map.android.com.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;


import java.util.Map;
import java.util.Set;

import map.android.com.map.Data.Contract;
import map.android.com.map.Data.DbHelper;

/**
 * Created by suzan on 29/07/17.
 */

public class TestUtilities extends AndroidTestCase {

    static final String TEST_NAME = "new name";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createPlaceValues(long locationRowId) {
        ContentValues placeValues = new ContentValues();
        placeValues.put(Contract.PlaceEntry.COLUMN_LOC_KEY, locationRowId);
        placeValues.put(Contract.PlaceEntry.COLUMN_NAME, "suzan momani");
        placeValues.put(Contract.PlaceEntry.COLUMN_DESCRIPTION, "suzan");
        placeValues.put(Contract.PlaceEntry.COLUMN_IMAGE, "");

        return placeValues;
    }


    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, -147.353);

        return testValues;
    }


    static long insertNorthPoleLocationValues(Context context) {
        // insert our test records into the database
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(Contract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

      static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }
        public void waitForNotificationOrFail() {

            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }

    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}