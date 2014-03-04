package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.Comment;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.ThreadViewFragment;

public class ThreadViewFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    ThreadViewFragment fragment;
    MainActivity activity;
    
    public ThreadViewFragmentTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        ThreadList.addThread(new Comment("Hello", null), "test thread");
        //Click the thread to open the fragment
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
   
    public void testListViewItemLayouts() {
        
    }
    
    

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
