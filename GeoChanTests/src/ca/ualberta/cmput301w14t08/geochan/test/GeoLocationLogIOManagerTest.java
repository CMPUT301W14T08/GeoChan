package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.managers.GeoLocationLogIOManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

public class GeoLocationLogIOManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public GeoLocationLogIOManagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    private GeoLocationLogIOManager manager;
    private MainActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        this.manager = GeoLocationLogIOManager.getInstance(activity);
    }
    
    public void testSerializeDeserialize() {
        LogEntry log1 = new LogEntry("log1", new GeoLocation(22,22));
        LogEntry log2 = new LogEntry("log1", new GeoLocation(22,22));
        LogEntry log3 = new LogEntry("log1", new GeoLocation(22,22));
        LogEntry log4 = new LogEntry("log1", new GeoLocation(22,22));
        ArrayList<LogEntry> list = new ArrayList<LogEntry>();
        list.add(log1);
        list.add(log2);
        list.add(log3);
        list.add(log4);
        manager.serializeLog(list);
        ArrayList<LogEntry> newList = manager.deserializeLog();
        assertTrue("size should be 4", newList.size() == 4);
        for(LogEntry l : newList) {
            assertTrue("Shoulde be log1", l.getThreadTitle().equals("log1"));
            assertTrue("location should be 22,22", l.getGeoLocation().getLatitude() == 22);
            assertTrue("location should be 22,22", l.getGeoLocation().getLongitude() == 22);
        }
    }
}
