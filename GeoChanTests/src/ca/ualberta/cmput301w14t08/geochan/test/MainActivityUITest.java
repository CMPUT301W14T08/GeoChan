package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.PreferencesFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class MainActivityUITest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity activity;
    
    public MainActivityUITest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        ThreadList.addThread(new Comment("Hello", null), "test thread");
    }

    /**
     * Click the Settings menu item and check if the correct fragment is inflated
     */
    public void testInflateSettings() throws Throwable {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, ca.ualberta.cmput301w14t08.geochan.R.id.action_settings, 0);
        Fragment fragment = (PreferencesFragment) waitForFragment("prefFrag", 2000);
        assertNotNull(fragment);
    }
    /**
     * Click the Add Thread action bar item and check if the correct fragment is inflated
     */
    /*
    public void testInflateAddThread() throws Throwable {
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, ca.ualberta.cmput301w14t08.geochan.R.id.action_add_thread, 0);
        Fragment fragment = (Fragment) waitForFragment("postThreadFrag", 2000);
        assertNotNull(fragment);
    }
    */
    /**
     *  testA.. because tests are run in alphabetical order,
     *  tests visibility of the default listview
     */
    public void testAListViewVisibility() {
        ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
        View rootView = activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(rootView, listView);
    }
    
    /**
     * Clicks a thread and asserts the correct fragment gets inflated
     */
    /*
    public void testThreadViewVisibility() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        });
        Fragment fragment = (ThreadViewFragment) waitForFragment("thread_view_fragment", 2000);
        assertNotNull(fragment);
    }
    */
    
    /**
     * http://stackoverflow.com/a/17789933
     * Sometimes the emulator is too slow.
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