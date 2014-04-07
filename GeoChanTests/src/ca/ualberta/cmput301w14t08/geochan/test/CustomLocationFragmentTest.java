package ca.ualberta.cmput301w14t08.geochan.test;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;

/**
 * Tests for the custom location fragment from which a user can select a location from a map
 */
public class CustomLocationFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    Fragment fragment;
    MainActivity activity;
    
    public CustomLocationFragmentTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        });
        ThreadViewFragment fragment = (ThreadViewFragment) waitForFragment("thread_view_fragment", 5000);
        assertNotNull("fragment not initialized",fragment);
    }

    /**
     * http://stackoverflow.com/a/17789933
     * Sometimes the emulator is too slow.
     */
    protected Fragment waitForFragment(String tag, int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {

            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                return fragment;
            }
        }
        return null;
    }
    
    public void testConstruction() {
        CustomLocationFragment frag = new CustomLocationFragment();
        assertNotNull(frag);
    }
}
