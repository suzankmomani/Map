package map.android.com.map.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by suzan on 29/07/17.
 */

public class Contract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "map.android.com.map";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PLACES = "places";
    public static final String PATH_LOCATION = "location";


    /* Inner class that defines the table contents of the location table */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "Location";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude.
        public static final String COLUMN_COORD_LAT = "lat";
        public static final String COLUMN_COORD_LNG = "lng";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the place table */
    public static final class PlaceEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).build();
        public static final Uri CONTENT_URI_JOIN =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACES).appendPath("/towTablesJoin").build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLACES;

        public static final String TABLE_NAME = "place";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // name of the place
        public static final String COLUMN_NAME = "name";
        // description of the place
        public static final String COLUMN_DESCRIPTION = "description";

        // image for the place if exist
        public static final String COLUMN_IMAGE = "image";

        // total rating of the place
        public static final String COLUMN_RATING = "COLUMN_RATING";

        // number of people rated this
        public static final String COLUMN_NUMBER_OF_RATINGS= "COLUMN_NUMBER_OF_RATINGS";

        public static final String COLUMN_DESCRIPTION_LONG="COLUMN_DESCRIPTION_LONG";

        public static Uri buildPlaceUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPlaceName(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }

        public static String getPlaceNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }
}