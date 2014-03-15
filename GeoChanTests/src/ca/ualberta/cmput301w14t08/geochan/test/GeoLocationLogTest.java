package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

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
        
        GeoLocationLog.addLogEntry(threadTitle, geoLocation);
        GeoLocationLog.addLogEntry(threadTitle, geoLocation);
        GeoLocationLog.addLogEntry(threadTitle, geoLocation);
        
        for (LogEntry entry : GeoLocationLog.getLogEntries()) {
            assertEquals("threadTitles should be equal", threadTitle, entry.getThreadTitle());
            assertEquals("geoLocations should be equal", geoLocation, entry.getGeoLocation());
        }
    }
    
    public void testClearLogAndCheckIsLogEmpty() {
        GeoLocationLog.addLogEntry("TestThreadTitle", new GeoLocation(1.0,2.0));
        assertEquals("Entries array should NOT be empty", false, GeoLocationLog.isEmpty());
        
        GeoLocationLog.clearLog();
        assertEquals("Entries array should be empty",true, GeoLocationLog.isEmpty());
    }
    
    public void testSizeOfLog() {        
        locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        
        GeoLocationLog.addLogEntry("thread1", geoLocation1);
        GeoLocationLog.addLogEntry("thread2", geoLocation2);
        GeoLocationLog.addLogEntry("thread3", geoLocation3);
        
        assertEquals("Size of entries should be 3", 3, GeoLocationLog.size());
    }
}
