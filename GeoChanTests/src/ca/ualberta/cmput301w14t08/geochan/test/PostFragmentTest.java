package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class PostFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    PostFragment fragment;
    MainActivity activity;

    public PostFragmentTest() {
        super(MainActivity.class);
    }

   @Override
    public void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        Comment testComment = new Comment("hello", null, null);
        ThreadList.addThread(testComment, "test thread");
        //Click the thread to open the thread View fragment and then click the reply button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                
                
                // DUE TO REFACTORING OF FRAGMENTS THIS DONT WORK SO GOOD
                //Fragment fragment = (ThreadViewFragment) waitForFragment("thread_view_fragment", 2000);
                assertNotNull("threadViewFragment is null", fragment);
                ImageButton reply = (ImageButton) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.post_button);
                assertNotNull("reply button is null", reply);
                //reply.performClick();
            }
        });
        //fragment = (PostCommentFragment) waitForFragment("comFrag", 5000);
    }
    
    public void testPreconditions() {
        assertNotNull("activity is null", activity);
        assertNotNull("fragment is null", fragment);
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