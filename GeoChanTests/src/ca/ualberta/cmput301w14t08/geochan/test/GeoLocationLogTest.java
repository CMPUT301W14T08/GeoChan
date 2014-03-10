package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.LogEntry;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;

public class GeoLocationLogTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private LocationListenerService locationListenerService;
    
    public GeoLocationLogTest() {
        super(MainActivity.class);
    }
    
    public void testConstruction() {
        assertNotNull(GeoLocationLog.getLogEntries());
    }
    
    public void testAddLogEntry() {
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        
        String threadTitle = "TestThread";
        
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance();
        geoLocationLog.addLogEntry(threadTitle, geoLocation);
        geoLocationLog.addLogEntry(threadTitle, geoLocation);
        geoLocationLog.addLogEntry(threadTitle, geoLocation);
        
        for (LogEntry entry : geoLocationLog.getLogEntries()) {
            assertEquals("threadTitles should be equal", threadTitle, entry.getThreadTitle());
            assertEquals("geoLocations should be equal", geoLocation, entry.getGeoLocation());
        }
    }
    
    public void testIsLogEmpty() {
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance();
        geoLocationLog.clearLog();
        assertEquals("Entries array should be empty",true, geoLocationLog.isEmpty());
    }
    
    public void testSizeOfLog() {
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance();
        
        locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        
        geoLocationLog.addLogEntry("thread1", geoLocation1);
        geoLocationLog.addLogEntry("thread2", geoLocation2);
        geoLocationLog.addLogEntry("thread3", geoLocation3);
        
        assertEquals("Size of entries should be 3", 3, geoLocationLog.size());
    }
}
