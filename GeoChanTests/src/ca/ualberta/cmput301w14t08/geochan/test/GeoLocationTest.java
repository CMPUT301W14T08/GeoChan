package ca.ualberta.cmput301w14t08.geochan.test;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;

public class GeoLocationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;
    private Location location;

    public GeoLocationTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                // Called when a new location is found by the network location provider.
                location = newLocation;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationManager.removeUpdates(locationListener);
    }

    public void testCorrectCoordinates() {
        GeoLocation geoLocation = new GeoLocation(activity);

        assertEquals("The longitude values should be equal", location.getLongitude(), geoLocation.getLongitude());
        assertEquals("The latitude coordinates should be equal", location.getLatitude(), geoLocation.getLatitude());
    }
    
    public void testDistanceBetweenGeoLocationObjects() {
        GeoLocation geoLocation1 = new GeoLocation(activity);
        GeoLocation geoLocation2 = new GeoLocation(activity);     
        
        /**
         * should be the same distance apart to begin
         */
        assertEquals("The distance between the objects should be 0", geoLocation1.distance(geoLocation2), 0.0);
        
        /**
         * change coordinates of 1 location and check distance
         */
        geoLocation2.setLatitude(geoLocation2.getLatitude() + 2);
        geoLocation2.setLongitude(geoLocation2.getLongitude() + 2);
        double distance = Math.sqrt(Math.pow(2,2) * 2);
        assertEquals("The distance should be sqrt(4)", geoLocation1.distance(geoLocation2), distance);   
    }

}   
