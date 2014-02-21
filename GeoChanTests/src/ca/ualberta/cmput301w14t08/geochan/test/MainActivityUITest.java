package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;

public class MainActivityUITest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public MainActivityUITest() {
        super(MainActivity.class);
    }
    
    /**
     * Click the Settings menu item and check if the correct fragment is inflated
     */
    public void testInflateSettings() throws Throwable {
        final MainActivity activity = getActivity();
        
        Instrumentation.ActivityMonitor am = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, true);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, 0, 0);
        FragmentManager m = activity.getFragmentManager();
        Fragment fragment = m.findFragmentByTag("prefFrag");
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
}