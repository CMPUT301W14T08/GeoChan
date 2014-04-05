package ca.ualberta.cmput301w14t08.geochan.test;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;

public class FavoritesUITests extends ActivityInstrumentationTestCase2<MainActivity> {

    Solo solo;
    
    public FavoritesUITests() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    
}
