package ca.ualberta.cmput301w14t08.geochan.test;

import junit.framework.TestCase;
import android.location.Location;
import android.location.LocationManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

public class LogEntryTest extends TestCase {

    public void testConstruction() {
        String threadTitle = "TestThread";

        Location location = new Location(LocationManager.GPS_PROVIDER);
        GeoLocation geoLocation = new GeoLocation(location);

        LogEntry entry = new LogEntry(threadTitle, geoLocation);

        assertEquals("titles should be the same", threadTitle, entry.getThreadTitle());
        assertEquals("geoLocation should be the same", geoLocation, entry.getGeoLocation());
    }

    public void testGetAndSetTitleAndGeoLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        GeoLocation geoLocation1 = new GeoLocation(location);

        LogEntry entry = new LogEntry("testThread1", geoLocation1);

        assertEquals("titles should be the same", "testThread1", entry.getThreadTitle());
        assertEquals("geoLocation should be the same", geoLocation1, entry.getGeoLocation());

        entry.setThreadTitle("testThread2");
        assertEquals("titles should be the same", "testThread2", entry.getThreadTitle());
        
        GeoLocation geoLocation2 = new GeoLocation(location);
        entry.setGeoLocation(geoLocation2);
        assertEquals("geoLocation should be the same", geoLocation2, entry.getGeoLocation());
    }
}
