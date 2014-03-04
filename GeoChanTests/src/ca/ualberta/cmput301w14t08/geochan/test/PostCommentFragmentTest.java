package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class PostCommentFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    PostCommentFragment fragment;
    MainActivity activity;

    public PostCommentFragmentTest() {
        super(MainActivity.class);
    }

   @Override
    public void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        Comment testComment = new Comment("hello", null);
        ThreadList.addThread(testComment, "test thread");
        //Click the thread to open the thread View fragment and then click the reply button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                Button reply = (Button) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.post_comment_button);
            }
        });
        Fragment fragment = (ThreadViewFragment) waitForFragment("comFrag", 5000);
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
