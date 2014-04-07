package ca.ualberta.cmput301w14t08.geochan.test;

import org.osmdroid.util.GeoPoint;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * This is a J-unit test class for GeoLocation Objects.
 * 
 * NOTE: If testing on emulator, you must inject a location via DDMS prior to
 * beginning tests
 * 
 * @author bradsimons
 * 
 */
public class GeoLocationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    /**
     * Constructor
     */
    public GeoLocationTest() {
        super(MainActivity.class);
    }

    /**
     * Junit test for getting the correct coordinates for the location. Creates
     * two geoLocation objects, and then tests to see if their latitude and
     * longitude values match.
     */
    public void testCorrectCoordinates() {
        // construct geoLocation object 1
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        locationListenerService.stopListening();

        // construct geoLocation object 2
        LocationListenerService locationListenerService2 = new LocationListenerService(
                getActivity());
        locationListenerService2.startListening();
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService2);
        locationListenerService.stopListening();

        // compare their lat and long values
        assertEquals("The longitude values should be equal", geoLocation1.getLongitude(),
                geoLocation2.getLongitude());
        assertEquals("The latitude coordinates should be equal", geoLocation1.getLatitude(),
                geoLocation2.getLatitude());
    }

    /**
     * Testing for the distance calculation between two GeoLocation objects.
     * Creates two location objects, then constructs two geoLocations objects
     * and sets their locations to the location objects created to start. Then
     * compares distances and check for the appropriate value.
     */
    public void testDistanceBetweenGeoLocationObjects() {
        // create two location objects
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);

        // create two geoLocation objects
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        locationListenerService.stopListening();

        // assign geoLocation location attributes
        geoLocation1.setLocation(location1);
        geoLocation2.setLocation(location2);

        // check initial distance
        assertEquals("The distance between the objects should be 0",
                geoLocation1.distance(geoLocation2), 0.0);

        // change one location and recheck distances
        geoLocation2
                .setCoordinates(geoLocation2.getLatitude() + 2, geoLocation2.getLongitude() + 2);
        double distance = Math.sqrt(8);

        assertEquals("The distance should be sqrt(8)", distance,
                geoLocation1.distance(geoLocation2));
    }

    /**
     * Test construction of a geoLocation object in three ways. Test
     * construction by passing a location listener service object as a
     * parameter, set of lat and long coordinates as parameters, and passing a
     * location object as a parameter
     */
    public void testConstruction() {
        // construct with a location listener service object
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        assertNotNull(geoLocation.getLocation());

        // construct with a set of latitude and longitude values
        GeoLocation geoLocation1 = new GeoLocation(1.0, 2.0);
        assertEquals("Latitude should be the same", 1.0, geoLocation1.getLatitude());
        assertEquals("Longitude should be the same", 2.0, geoLocation1.getLongitude());

        // construct with a location object
        Location location = new Location(LocationManager.GPS_PROVIDER);
        GeoLocation geoLocation2 = new GeoLocation(location);
        assertEquals("Locations should be the same", location, geoLocation2.getLocation());
    }

    /**
     * Testing the getter and setter for the lat and long coordinates
     */
    public void testGetAndSetCoordinates() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        // test setting lat and long coordinates via the set coordinates method
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        geoLocation.setCoordinates(1.0, 1.0);

        // check results and test get lat and long
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLatitude());
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLongitude());
    }

    /**
     * Testing the getter and setter for the location attribute
     */
    public void testSetNewLocation() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        // create geoLocation object and location object
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        Location location = new Location(LocationManager.GPS_PROVIDER);

        // set the location
        geoLocation.setLocation(location);

        // get the location and test for equivalence
        assertEquals("Locations should be the same", location, geoLocation.getLocation());
    }

    /**
     * Test the getter and setter for latitude and longitude.
     */
    public void testGetAndSetLatitudeAndLongitude() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        // create geoLocation object
        GeoLocation geoLocation = new GeoLocation(locationListenerService);

        // set the latitude value, then get it back and check its value
        geoLocation.setLatitude(2.3);
        assertEquals("Latitude should be 2.3", 2.3, geoLocation.getLatitude());

        // set the longitude value, then get it back and check its value
        geoLocation.setLongitude(3.2);
        assertEquals("Longitude should be 3.2", 3.2, geoLocation.getLongitude());
    }

    /**
     * Testing to make sure that the changes in the geoLocation object do not
     * affect the location listener service (geoLocation coordinates can
     * construct from a listener, but should not be dependent on them)
     */
    public void testChangeOfCoordinatesDoesNotAffectLocationListener() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        // create new geoLocation object
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);

        // create a new location object and store the location coordinates from
        // the
        // geoLocation object into it
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(geoLocation1.getLatitude());
        location.setLongitude(geoLocation1.getLongitude());

        // change the coordinates of the geoLocation
        geoLocation1.setCoordinates(5.0, 5.0);

        // assert that the location object and the geoLocation object should not
        // have the same values
        assertNotSame("The latitude values should not be equal", location.getLatitude(),
                geoLocation1.getLatitude());
        assertNotSame("The longitude values should not be equal", location.getLongitude(),
                geoLocation1.getLongitude());

        // create another geoLocation object
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);

        // this geoLocation object should have the same values as the original
        // location
        // as the listener should not have changed
        assertEquals("The latitude values of should be equal", location.getLatitude(),
                geoLocation2.getLatitude());
        assertEquals("The latitude values of should be equal", location.getLongitude(),
                geoLocation2.getLongitude());

        // assert that the first geoLocation changes did not affect geoLocation
        // 2 and they
        // are not equal to each other
        assertNotSame("The latitude values should not be equal", geoLocation1.getLatitude(),
                geoLocation2.getLatitude());
        assertNotSame("The longitude values should not be equal", geoLocation1.getLongitude(),
                geoLocation2.getLongitude());
    }

    /**
     * Tests the getter and setter for location description
     */
    public void testGetAndSetLocationDescription() {
        // create a geoLocation object and set its description
        GeoLocation geoLocation = new GeoLocation(1.0, 1.0);
        geoLocation.setLocationDescription("test description");

        // get the description and check for the correct string
        assertEquals("the descriptions should be the same", "test description",
                geoLocation.getLocationDescription());
    }

    /**
     * Checks for the correct POI string from the GeoNames.org service. Uses
     * known GPS coordinates and checks their values with what is in the
     * database.
     */
    public void testGetPOIString() {
        // create a geoLocation, set its coordinates, and retrieve the POI
        // string. Coordinates correspond to Campus Tower Suite in Edmonton
        final GeoLocation geoLocation = new GeoLocation(53.5228, -113.5202);

        // create a runnable and handler to allow the network request to
        // complete then check for the appropriate POI string
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                assertEquals("the descriptions should be the same", "Campus Tower Suite Hotel",
                        geoLocation.getLocationDescription());

            }
        };
        handler.postDelayed(runnable, 2000);

        // change the coordinates to New York City Hall
        geoLocation.setCoordinates(-74.00597, 40.7126);

        // again check the POI
        Handler handler2 = new Handler();
        Runnable runnable2 = new Runnable() {
            public void run() {
                assertEquals("the descriptions should be the same", "New York City Hall",
                        geoLocation.getLocationDescription());

            }
        };
        handler2.postDelayed(runnable2, 2000);

        // one more check on location, set coordinates to Larry King Square
        geoLocation.setCoordinates(34.09806, -118.32944);

        // again check the POI
        Handler handler3 = new Handler();
        Runnable runnable3 = new Runnable() {
            public void run() {
                assertEquals("the descriptions should be the same", "Larry King Square",
                        geoLocation.getLocationDescription());
            }
        };
        handler3.postDelayed(runnable3, 2000);
    }

    /**
     * tests to make sure that the makeGeoPoint method returns the appropriate
     * geoPoint object based on the geoLocation object
     */
    public void testMakeGeoPointMethod() {
        GeoLocation geoLocation = new GeoLocation(1.0, 2.0);
        GeoPoint geoPoint = geoLocation.makeGeoPoint();

        assertNotNull(geoPoint);
        assertEquals("geoPoint and geoLocation should have same lat", geoLocation.getLatitude(),
                geoPoint.getLatitude());
        assertEquals("geoPoint and geoLocation should have same long", geoLocation.getLatitude(),
                geoPoint.getLatitude());
    }
}
