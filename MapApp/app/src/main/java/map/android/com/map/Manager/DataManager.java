package map.android.com.map.Manager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import java.util.List;

import map.android.com.map.Data.Contract;
import map.android.com.map.Objects.Place;

import static map.android.com.map.Manager.DbQueryManager.getAllPlacesList;

/**
 * Created by suzan on 30/07/17.
 */

/* this class of for deleting and inserting test records from the database */
/* this is only for testing purposes, in normal cases data will be fetched from a web service and stored in local db to be used*/
public class DataManager {

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 5;
    private static final String[] cities = new String[]{"Taj Mall", "Mecca Mall", "Morocco Mall", "Riyadh Gallery Mall", "NY mall"};
    private static final String[] long_desc = new String[]{"TAJ lifestyle center, the ultimate destination for shopping, dining and entertainment in Jordan. TAJ is a multi-purpose shopping complex with over 190 of the finest outlets spread across 150,000 square meters of indoor and outdoor space.\n" +
            "\n" +
            "TAJ offers the Kingdom’s most comprehensive retail mix from world renowned high street fashion labels, designer boutiques, jewelry, cosmetics and beauty to kid’s wear, home decor, sporting goods, electronics, and Jordan’s premier supermarket retailer Cozmo. What’s more, 40 percent of the brands TAJ has introduced are new to the Kingdom. "
            , "Mecca Mall is a shopping mall in Amman, Jordan. It is located on Mecca Street, and this is the reason the mall was named Mecca Mall."
            , "Morocco Mall is the second largest shopping centre in Africa with 200 000m² of floor space in Casablanca, Morocco. Morocco Mall, which opened on December 1, 2011, was designed by Architect Davide Padoa of Design International, a global architecture boutique with its headquarters in London."
            , "Riyadh Gallery Mall brings a shopping and entertainment experience of world-class level, tailored for local and international visitors. Strategically located in Jordan’s Irbid city the 87,000 square meters features over 250 international & local brands, including department stores, fashion, lifestyle, sports, electronics, home furnishing stores and the largest hypermarket in the country."
            , "NY mall Mall is the second largest shopping centre in Africa with 200 000m² of floor space in Casablanca, Morocco. Morocco Mall, which opened on December 1, 2011, was designed by Architect Davide Padoa of Design International, a global architecture boutique with its headquarters in London."};
    private static final String[] desc = new String[]{"Amman, Jordan", "Amman, Jordan", "Casablanca, Morocco", "Ryadh, KSA", "NY,USA"};
    private static final float[] rating = new float[]{2.4F, 4.3F, 3.0F, 3F, 5F};
    private static final Integer[] numberOfRatings = new Integer[]{20, 25, 55, 90, 11};

    public static List<Place> prepareData(Context context) {
        deleteAllRecordsFromProvider(context);
        insertValues(context);
        List<Place> placeList = getAllPlacesList(context);
        return placeList;
    }

    public static void deleteAllRecordsFromProvider(Context context) {
        context.getContentResolver().delete(
                Contract.PlaceEntry.CONTENT_URI,
                null,
                null
        );
        context.getContentResolver().delete(
                Contract.LocationEntry.CONTENT_URI,
                null,
                null
        );

    }

    private static void insertValues(Context context) {
        ContentValues[] bulkInsertContentValues = createBulkInsertPlaceValues(context);

        context.getContentResolver().bulkInsert(Contract.PlaceEntry.CONTENT_URI, bulkInsertContentValues);
    }


    private static ContentValues[] createBulkInsertPlaceValues(Context context) {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues locationValues = getLocationValue(i);
            Uri locationUri = context.getContentResolver().insert(Contract.LocationEntry.CONTENT_URI, locationValues);
            long locationRowId = ContentUris.parseId(locationUri);

            ContentValues placeValues = new ContentValues();
            placeValues.put(Contract.PlaceEntry.COLUMN_LOC_KEY, locationRowId);
            placeValues.put(Contract.PlaceEntry.COLUMN_NAME, cities[i]);
            placeValues.put(Contract.PlaceEntry.COLUMN_DESCRIPTION, desc[i]);
            placeValues.put(Contract.PlaceEntry.COLUMN_IMAGE, "https://upload.wikimedia.org/wikipedia/commons/1/1e/Stonehenge.jpg");
            placeValues.put(Contract.PlaceEntry.COLUMN_NUMBER_OF_RATINGS, numberOfRatings[i]);
            placeValues.put(Contract.PlaceEntry.COLUMN_DESCRIPTION_LONG, long_desc[i]);
            placeValues.put(Contract.PlaceEntry.COLUMN_RATING, rating[i]);
            returnContentValues[i] = placeValues;
        }
        return returnContentValues;
    }


    private static ContentValues getLocationValue(int i) {


        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        if (i == 0) {
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 31.9414);
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, 35.8880);
        } else if (i == 1) {
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 31.9774);
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, 35.8434);
        } else if (i == 2) {
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 33.5759);
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, -7.7068);
        } else if (i == 3) {
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 24.7437);
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, 46.6578);
        } else {
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LAT, 40.7128);
            testValues.put(Contract.LocationEntry.COLUMN_COORD_LNG, -74.0059);
        }


        return testValues;
    }


}
