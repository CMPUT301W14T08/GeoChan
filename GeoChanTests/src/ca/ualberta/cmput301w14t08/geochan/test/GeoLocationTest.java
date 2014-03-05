package ca.ualberta.cmput301w14t08.geochan.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

public class GeoLocationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;
    private Location location;
    private LocationListenerService locationListenerService;
    private LocationManager locationManager;

    public GeoLocationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE); 
    }

    public void testCorrectCoordinates() {
        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        locationListenerService.stopListening();
        
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        assertEquals("The longitude values should be equal", location.getLongitude(), geoLocation.getLongitude());
        assertEquals("The latitude coordinates should be equal", location.getLatitude(), geoLocation.getLatitude());
    }

    public void testDistanceBetweenGeoLocationObjects() {
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        
        locationListenerService = new LocationListenerService(activity);
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
        locationListenerService = new LocationListenerService(activity);
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        assertNotNull(geoLocation.getLocation());
    }
    
    public void testLatitude() {
        locationListenerService = new LocationListenerService(activity);
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        geoLocation.setLatitude(1.0);
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLatitude());
    }
    
    public void testLongitude() {
        locationListenerService = new LocationListenerService(activity);
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        geoLocation.setLongitude(1.0);
        assertEquals("Latitude should be 1.0", 1.0, geoLocation.getLongitude());
    }
    
    public void testSetNewLocation() {
        locationListenerService = new LocationListenerService(activity);
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        geoLocation.setLocation(location);
        assertEquals("Locations should be the same", location, geoLocation.getLocation());
    }
}   
