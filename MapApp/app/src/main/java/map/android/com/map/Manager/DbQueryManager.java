package map.android.com.map.Manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import map.android.com.map.Data.Contract;
import map.android.com.map.Objects.Location;
import map.android.com.map.Objects.Place;

import static map.android.com.map.Data.Contract.BASE_CONTENT_URI;
import static map.android.com.map.Data.Contract.PATH_PLACES;

/**
 * Created by suzan on 31/07/17.
 */


/*This class is for querying data from the database*/
public class DbQueryManager {

    /* query places with name LIKE %?%  */
    public static List<Place> getPlacesByName(Context context, String name) {
        List<Place> placeList = getPlacesByNameList(getPlacesByNameCursor(context, name));
        return placeList;
    }

    private static Cursor getPlacesByNameCursor(Context context, String name) {
        Uri CONTENT_URI_PLACE_JOIN =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).appendEncodedPath("place_with_name/" + name).build();

        Cursor cursor = context.getContentResolver().query(
                CONTENT_URI_PLACE_JOIN,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );
        return cursor;
    }

    private static List<Place> getPlacesByNameList(Cursor cursor) {

        List<Place> placeList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                Location location = new Location();
                place.setId(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry._ID)));
                place.setName(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_NAME)));
                place.setDesc(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_DESCRIPTION)));
                place.setImage(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_IMAGE)));
                place.setRating(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_RATING)));
                place.setNoOfRatings(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_NUMBER_OF_RATINGS)));
                place.setLongDesc(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_DESCRIPTION_LONG)));

                location.setId(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry._ID)));
                location.setLat(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry.COLUMN_COORD_LAT)));
                location.setLng(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry.COLUMN_COORD_LNG)));

                place.setLocation(location);

                placeList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return placeList;
    }


    /* query all places */
    public static List<Place> getAllPlacesList(Context context) {
        Cursor cursor = getAllPlacesCursor(context);
        List<Place> placeList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Place place = new Place();
                Location location = new Location();
                place.setId(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry._ID)));
                place.setName(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_NAME)));
                place.setDesc(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_DESCRIPTION)));
                place.setImage(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_IMAGE)));
                place.setRating(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_RATING)));
                place.setNoOfRatings(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_NUMBER_OF_RATINGS)));
                place.setLongDesc(cursor.getString(cursor.getColumnIndex(Contract.PlaceEntry.COLUMN_DESCRIPTION_LONG)));


                location.setId(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry._ID)));
                location.setLat(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry.COLUMN_COORD_LAT)));
                location.setLng(cursor.getString(cursor.getColumnIndex(Contract.LocationEntry.COLUMN_COORD_LNG)));

                place.setLocation(location);

                placeList.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return placeList;
    }

    private static Cursor getAllPlacesCursor(Context context) {
        // A cursor is your primary interface to the query results.
        Cursor cursor = context.getContentResolver().query(
                Contract.PlaceEntry.CONTENT_URI_JOIN,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );
        return cursor;
    }

}
