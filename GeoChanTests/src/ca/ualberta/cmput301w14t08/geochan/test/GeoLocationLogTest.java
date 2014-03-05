package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

public class GeoLocationLogTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private LocationListenerService locationListenerService;
    
    public GeoLocationLogTest() {
        super(MainActivity.class);
        locationListenerService = new LocationListenerService(getActivity());
    }
    
    public void testConstruction() {
        GeoLocationLog geoLocationLog = new GeoLocationLog();
        assertNotNull(geoLocationLog.getLogEntries());
    }
    
    public void testAddLogEntry() {
        GeoLocationLog geoLocationLog = new GeoLocationLog();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        ThreadComment threadComment = new ThreadComment();
        
        geoLocationLog.addLogEntry(geoLocation, threadComment);
        
    }
}
