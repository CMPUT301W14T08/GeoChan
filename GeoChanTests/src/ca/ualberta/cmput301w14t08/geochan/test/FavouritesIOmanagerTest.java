package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.managers.FavouritesIOManager;

public class FavouritesIOmanagerTest extends ActivityInstrumentationTestCase2<MainActivity>  {
    
    private MainActivity activity;
    private FavouritesIOManager manager;
    
    
    public FavouritesIOmanagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        this.manager = FavouritesIOManager.getInstance(activity);
    }
    
    public void testSerializeDeserialize() {

    }
}
