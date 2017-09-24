package map.android.com.map.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by suzan on 29/07/17.
 */

public class Provider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DbHelper mOpenHelper;

    static final int PLACE = 100;
    static final int PLACE_WITH_LOCATION = 101;
    static final int LOCATION = 300;
    static final int PLACE_WITH_NAME = 400;


    private static final SQLiteQueryBuilder sPlaceByLocationSettingQueryBuilder;

    static {
        sPlaceByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sPlaceByLocationSettingQueryBuilder.setTables(
                Contract.PlaceEntry.TABLE_NAME + " INNER JOIN " +
                        Contract.LocationEntry.TABLE_NAME +
                        " ON " + Contract.PlaceEntry.TABLE_NAME +
                        "." + Contract.PlaceEntry.COLUMN_LOC_KEY +
                        " = " + Contract.LocationEntry.TABLE_NAME +
                        "." + Contract.LocationEntry._ID);
    }

    private Cursor getPlaceByLocationSetting(Uri uri, String[] projection, String sortOrder) {

        return sPlaceByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private static final String sPlaceNameSelection =
            Contract.PlaceEntry.TABLE_NAME +
                    "." + Contract.PlaceEntry.COLUMN_NAME + "  LIKE '%' || ? || '%' ";

    private Cursor getPlaceByName(Uri uri, String[] projection, String sortOrder) {
        String name = Contract.PlaceEntry.getPlaceNameFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sPlaceNameSelection;
        selectionArgs = new String[]{name};


        return sPlaceByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }


      /*
       This UriMatcher will match each URI to the PLACE, LOCATION , PLACE_WITH_LOCATION, PLACE_WITH_NAME
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, Contract.PATH_PLACES, PLACE);
        matcher.addURI(authority, Contract.PATH_PLACES + "/*", PLACE_WITH_LOCATION);
        matcher.addURI(authority, Contract.PATH_PLACES + "/*/*", PLACE_WITH_NAME);

        matcher.addURI(authority, Contract.PATH_LOCATION, LOCATION);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case PLACE_WITH_LOCATION:
                return Contract.PlaceEntry.CONTENT_TYPE;
            case PLACE:
                return Contract.PlaceEntry.CONTENT_TYPE;
            case LOCATION:
                return Contract.LocationEntry.CONTENT_TYPE;
            case PLACE_WITH_NAME:
                return Contract.PlaceEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //  given a URI, will determine what kind of request it is
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case PLACE_WITH_NAME: {
                retCursor = getPlaceByName(uri, projection, sortOrder);
                break;
            }
            case PLACE_WITH_LOCATION: {
                retCursor = getPlaceByLocationSetting(uri, projection, sortOrder);
                break;
            }
            case PLACE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.PlaceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        Contract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case PLACE: {
                long _id = db.insert(Contract.PlaceEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = Contract.PlaceEntry.buildPlaceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(Contract.LocationEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = Contract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case PLACE:
                rowsDeleted = db.delete(
                        Contract.PlaceEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        Contract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case PLACE:
                rowsUpdated = db.update(Contract.PlaceEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(Contract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(Contract.PlaceEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // This is a method specifically to assist the testing
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
