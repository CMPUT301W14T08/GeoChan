package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.services.LocationListenerService;

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

}
