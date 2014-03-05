package ca.ualberta.cmput301w14t08.geochan.test;

import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;

public class LocationListenerServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public LocationListenerServiceTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testConstruction() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        assertNotNull(locationListenerService.getCurrentLocation());
    }
    
    public void testBetterLocationAfterTwoMinutes() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        location1.setLongitude(0.0);
        location1.setLatitude(0.0);
        location2.setLongitude(1.0);
        location2.setLatitude(1.0);
        
        // add over two minutes in milliseconds to one location's time
        location2.setTime(location2.getTime() + 120001);
        assertTrue(locationListenerService.isBetterLocation(location2, location1));  
        assertFalse(locationListenerService.isBetterLocation(location1, location2));
    }
    
    public void testSameProvider() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        String provider1 = LocationManager.GPS_PROVIDER;
        String provider2 = LocationManager.NETWORK_PROVIDER;
        
        assertTrue(locationListenerService.isSameProvider(provider1, provider1));
        assertTrue(locationListenerService.isSameProvider(provider2, provider2));
        assertFalse(locationListenerService.isSameProvider(provider1, provider2));
        assertFalse(locationListenerService.isSameProvider(provider2, provider1));
    }
    
    public void testBetterAccuracy() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        location1.setAccuracy(1);
        location2.setAccuracy(2);
        
        assertTrue(locationListenerService.isBetterLocation(location1, location2));
        assertFalse(locationListenerService.isBetterLocation(location2, location1));
    }
    
    public void testRetrieveLocation() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        assertNotNull(locationListenerService.getCurrentLocation());
    }
}
