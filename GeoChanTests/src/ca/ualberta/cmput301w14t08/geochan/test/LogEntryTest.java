package ca.ualberta.cmput301w14t08.geochan.test;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

/**
 * J-unit test class for testing the LogEntry class
 * 
 * @author bradsimons
 *
 */
public class LogEntryTest extends TestCase {

    public void testConstruction() {
        // create a geoLocation object and corresponding log entry
        GeoLocation geoLocation = new GeoLocation(1.0, 2.0);
        geoLocation.setLocationDescription("test description");
        LogEntry entry = new LogEntry("testThread", geoLocation);
        
        // assert that the title, geoLocation, and description were all set properly
        // in construction
        assertEquals("titles should be the same", "testThread", entry.getThreadTitle());
        assertEquals("geoLocation should be the same", geoLocation, entry.getGeoLocation());
        assertEquals("location description should be the same",
                geoLocation.getLocationDescription(), entry.getLocationDescription());
    }

    /**
     * Test getter and setter for the geoLocation attribute
     */
    public void testGetAndSetGeoLocation() {
        // create a geoLocation object and corresponding entry
        GeoLocation geoLocation1 = new GeoLocation(1.0, 2.0);
        LogEntry entry = new LogEntry("testThread1", geoLocation1);
        
        // assert that the entry has the same location as the geoLocation object set
        assertEquals("geoLocation should be the same", geoLocation1, entry.getGeoLocation());

        // set a new geoLocation to that entry and check the entry's return value
        GeoLocation geoLocation2 = new GeoLocation(2.0, 3.0);
        entry.setGeoLocation(geoLocation2);
        assertEquals("geoLocation should be the same", geoLocation2, entry.getGeoLocation());
    }

    /**
     * Test the getter and set for the thread title attribute
     */
    public void testGetAndSetThreadTitle() {
        // create a new geoLocation object and entry from that location. Set a
        // test title
        GeoLocation geoLocation1 = new GeoLocation(1.0, 2.0);
        LogEntry entry = new LogEntry("testThread1", geoLocation1);

        // assert the title returned matches
        assertEquals("titles should be the same", "testThread1", entry.getThreadTitle());

        // change the title and assert the returned value matches
        entry.setThreadTitle("testThread2");
        assertEquals("titles should be the same", "testThread2", entry.getThreadTitle());
    }

    /**
     * Test the getter and setter for location description
     */
    public void testGetAndSetLocationDescription() {
        // create a geoLocation object
        GeoLocation geoLocation = new GeoLocation(1.0, 2.0);

        // create an entry and set it's location description
        LogEntry entry = new LogEntry("testThread", geoLocation);
        entry.setLocationDescription("test description");

        // assert that the entry returns the same description string
        assertEquals("descriptions should be the same", "test description",
                entry.getLocationDescription());
    }
}
