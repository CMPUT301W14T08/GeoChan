package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
/**
 * Tests the functionality of the GeoLocation IO manager
 */
public class GeoLocationLogIOManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public GeoLocationLogIOManagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    private MainActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
    }
    
    /**
     * Test serialization/deserialization by serializing a log of LogEntry 
     * object, deserializing it and verifying that the result matches the
     * log that was serialized.
     */
    public void testSerializeDeserialize() {
        GeoLocationLog log1 = GeoLocationLog.generateInstance(activity.getApplicationContext());
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
     
        ArrayList<GeoLocation> geoList = log1.getLogEntries();
        assertTrue("size should be 5", geoList.size() == 5);
        for(GeoLocation loc : geoList) {
            assertTrue("location should be 22,22", loc.getLatitude() == 22);
            assertTrue("location should be 22,22", loc.getLongitude() == 22);
        }
    }
}
