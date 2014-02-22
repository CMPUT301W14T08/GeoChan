package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.PostThreadFragment;
import ca.ualberta.cmput301w14t08.geochan.PreferencesFragment;

public class MainActivityUITest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity activity;
    
    public MainActivityUITest() {
        super(MainActivity.class);
    }
    
    /**
     * Click the Settings menu item and check if the correct fragment is inflated
     */
    public void testInflateSettings() throws Throwable {
        activity = getActivity();

        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, ca.ualberta.cmput301w14t08.geochan.R.id.action_settings, 0);
        Fragment fragment = (PreferencesFragment) waitForFragment("prefFrag", 2000);
        assertNotNull(fragment);
    }
    
    /**
     * Click the Add Thread action bar item and check if the correct fragment is inflated
     */
    
    public void testInflateAddThread() throws Throwable {
        activity = getActivity();
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, ca.ualberta.cmput301w14t08.geochan.R.id.action_add_thread, 0);
        Fragment fragment = (PostThreadFragment) waitForFragment("postThreadFrag", 2000);
        assertNotNull(fragment);
    }
    
        
    /**
     *  testA.. because tests are run in alphabetical order
     */
    
    public void testAListViewVisibility() {
        Intent intent = new Intent();
        setActivityIntent(intent);
        MainActivity activity = getActivity();
        ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
        View rootView = activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(rootView, listView);
    }
    
    /**
     * http://stackoverflow.com/a/17789933
     * Sometimes the emulator is too slow to launch fragments, so we need this
     */
    protected Fragment waitForFragment(String tag, int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {

            Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                return fragment;
            }
        }
        return null;
    }
}