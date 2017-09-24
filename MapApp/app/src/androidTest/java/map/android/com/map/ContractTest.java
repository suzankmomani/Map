package map.android.com.map;

import android.net.Uri;
import android.test.AndroidTestCase;

import map.android.com.map.Data.Contract;

/**
 * Created by suzan on 29/07/17.
 */

public class ContractTest extends AndroidTestCase {

    private static final String TEST_PLACE_NAME = "suzan momani";

    public void testbuildPlaceName() {
        Uri locationUri = Contract.PlaceEntry.buildPlaceName(TEST_PLACE_NAME);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildPlaceName in " +
                        "PlaceContract.",
                locationUri);
        assertEquals("Error: Place location not properly appended to the end of the Uri",
                TEST_PLACE_NAME, locationUri.getLastPathSegment());
        assertEquals("Error: Place location Uri doesn't match our expected result",
                locationUri.toString(),
                "content://dopravo.android.com.dopravoapp/places/suzan%20momani");

    }
}