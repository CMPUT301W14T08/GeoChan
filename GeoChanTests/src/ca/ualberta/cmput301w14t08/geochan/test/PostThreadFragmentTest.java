package ca.ualberta.cmput301w14t08.geochan.test;

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.PostThreadFragment;

public class PostThreadFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    PostThreadFragment fragment;
    MainActivity activity;

    public PostThreadFragmentTest() {
        super(MainActivity.class);
    }

    //Needs work
    public void testAddThread() throws Throwable {
        activity = getActivity();
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(activity, ca.ualberta.cmput301w14t08.geochan.R.id.action_add_thread, 0);
        fragment = (PostThreadFragment) waitForFragment("postThreadFrag", 2000);
        /**
         * First, make sure the fragment inflated properly
         */
        assertNotNull("There should be a fragment here", fragment);
        
        runTestOnUiThread(new Runnable() {
            @Override 
            public void run() {
                View view = fragment.getView();
                Button button = (Button) view.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.post_thread_button);
                EditText title = (EditText) view.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.titlePrompt);
                title.setText("Thread Title.");
                EditText comment = (EditText) view.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.commentBody);
                comment.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit,"
                        + " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
                button.performClick();
                //fail("Test");
            }
        });
        
    }  
    
    
    /**
     * http://stackoverflow.com/a/17789933
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
