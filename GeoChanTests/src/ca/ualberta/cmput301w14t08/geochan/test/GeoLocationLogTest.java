package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;

/**
 * Test the functionality of the GeoLocationLog methods
 */
public class GeoLocationLogTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private LocationListenerService locationListenerService;
    
    public GeoLocationLogTest() {
        super(MainActivity.class);
    }
    
    /**
     * Verifies the constructor constructs object
     */
    public void testConstruction() {
        GeoLocationLog geoLocationLog =  GeoLocationLog.getInstance(getActivity());
        assertNotNull(geoLocationLog.getLogEntries());
    }
    
    /**
     * Tests adding a log entry by adding several log entries, then
     * iterating over the log and verifying the entries have been added
     * correctly.
     */
    public void testAddLogEntry() {
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance(getActivity());
        
        geoLocationLog.addLogEntry(geoLocation);
        geoLocationLog.addLogEntry(geoLocation);
        geoLocationLog.addLogEntry(geoLocation);
        
        for (GeoLocation entry : geoLocationLog.getLogEntries()) {
            assertTrue("geoLocations should be equal", geoLocation.equals(entry));
        }
    }
    
    /**
     * Tests the clearLog method by populating the log,
     * calling the method and verifying the log is empty
     */
    public void testClearLogAndCheckIsLogEmpty() {
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance(getActivity());
        
        geoLocationLog.addLogEntry(new GeoLocation(1.0,2.0));
        assertEquals("Entries array should NOT be empty", false, geoLocationLog.isEmpty());
        
        geoLocationLog.clearLog();
        assertEquals("Entries array should be empty",true, geoLocationLog.isEmpty());
    }
    
    /**
     * Test the corectness of the size method by populating the log with
     * objects and verifying the method returns the correct quantity
     */
    public void testSizeOfLog() {        
        locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        
        GeoLocationLog geoLocationLog = GeoLocationLog.getInstance(getActivity());
        
        geoLocationLog.addLogEntry(geoLocation1);
        geoLocationLog.addLogEntry(geoLocation2);
        geoLocationLog.addLogEntry(geoLocation3);
        
        assertEquals("Size of entries should be 3", 3, geoLocationLog.size());
    }
}
