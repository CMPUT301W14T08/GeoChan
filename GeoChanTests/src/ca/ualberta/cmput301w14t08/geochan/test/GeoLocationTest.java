package ca.ualberta.cmput301w14t08.geochan.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;

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
        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);

        
        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);  
        locationListenerService.stopListening();
        
        /**
         * should be the same distance apart to begin
         */
        assertEquals("The distance between the objects should be 0", geoLocation1.distance(geoLocation2), 0.0);
        
        /**
         * change coordinates of one location and check distance
         */
        geoLocation2.setLatitude(geoLocation2.getLatitude() + 2);
        geoLocation2.setLongitude(geoLocation2.getLongitude() + 2);
        double distance = Math.sqrt(8);
        assertEquals("The distance should be sqrt(8)", distance, geoLocation1.distance(geoLocation2));   
    }

}   
