package ca.ualberta.cmput301w14t08.geochan.test;

import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

public class GeoLocationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public GeoLocationTest() {
        super(MainActivity.class);
    }

    public void testCorrectCoordinates() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        locationListenerService.stopListening();

        LocationListenerService locationListenerService2 = new LocationListenerService(getActivity());
        locationListenerService2.startListening();
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService2);
        locationListenerService.stopListening();

        assertEquals("The longitude values should be equal", geoLocation1.getLongitude(), geoLocation2.getLongitude());
        assertEquals("The latitude coordinates should be equal", geoLocation1.getLatitude(), geoLocation2.getLatitude());
    }

    public void testDistanceBetweenGeoLocationObjects() {
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);

        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);

        geoLocation1.setLocation(location1);
        geoLocation2.setLocation(location2);

        assertEquals("The distance between the objects should be 0", geoLocation1.distance(geoLocation2), 0.0);

        geoLocation2.setLatitude(geoLocation2.getLatitude() + 2);
        geoLocation2.setLongitude(geoLocation2.getLongitude() + 2);
        double distance = Math.sqrt(8);

        assertEquals("The distance should be sqrt(8)", distance, geoLocation1.distance(geoLocation2)); 
    }

    public void testConstruction() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        assertNotNull(geoLocation.getLocation());
    }

    public void testLatitude() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        geoLocation.setLatitude(1.0);
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLatitude());
    }

    public void testLongitude() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        geoLocation.setLongitude(1.0);
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLongitude());
    }

    public void testSetNewLocation() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        geoLocation.setLocation(location);
        assertEquals("Locations should be the same", location, geoLocation.getLocation());
    }

    public void testChangeOfCoordinatesDoesNotAffectLocationListener() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(geoLocation1.getLatitude());
        location.setLongitude(geoLocation1.getLongitude());
        
        geoLocation1.setLatitude(5.0);
        geoLocation1.setLongitude(5.0);
        
        assertNotSame("The latitude values should not be equal", location.getLatitude(), geoLocation1.getLatitude());
        assertNotSame("The longitude values should not be equal", location.getLongitude(), geoLocation1.getLongitude());
        
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        
        assertEquals("The latitude values of should be equal", location.getLatitude(), geoLocation2.getLatitude());
        assertEquals("The latitude values of should be equal", location.getLongitude(), geoLocation2.getLongitude());
        
        assertNotSame("The latitude values should not be equal", geoLocation1.getLatitude(), geoLocation1.getLatitude());
        assertNotSame("The longitude values should not be equal", geoLocation1.getLongitude(), geoLocation1.getLongitude());
    }
}   
