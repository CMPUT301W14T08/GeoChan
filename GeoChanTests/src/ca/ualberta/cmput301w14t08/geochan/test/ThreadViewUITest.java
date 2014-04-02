package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.regex.Pattern;

import com.robotium.solo.Solo;

import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.activities.PreferencesActivity;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Tests for the threadView fragment UI
 * Uses Robotium library
 * 
 * @author Tom Krywitsky
 */
public class ThreadViewUITest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    
    public ThreadViewUITest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    private void getToThreadViewFrag() {
        assertTrue("Did not launch thread list", solo.waitForFragmentById(R.id.fragment_container));
        // Testing with just first thread in thread list
        solo.clickInList(2, 0);
        assertTrue("Thread view frag did not launch", solo.waitForFragmentByTag("thread_view_fragment"));
    }
    
    /**
     * Test that the list UI is scrollable and clickable
     */
    public void testReplyFragment() {
        getToThreadViewFrag();
        solo.clickOnImageButton(0);
        //assertTrue("Custom location fragment did not launch", solo.waitForFragmentByTag("customLocFrag"));
        solo.searchButton(Pattern.quote(solo.getString(R.string.location)));
        solo.searchButton(Pattern.quote(solo.getString(R.string.attach_image)));
        solo.searchButton(Pattern.quote(solo.getString(R.string.post)));
        solo.goBack();
    }
    
    public void testImageAttachment() {
        getToThreadViewFrag();
        solo.clickOnImageButton(0);
        solo.clickOnButton(solo.getString(R.string.attach_image));
        solo.searchButton("Camera");
        solo.searchButton("Gallery");
    }

    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    
}
