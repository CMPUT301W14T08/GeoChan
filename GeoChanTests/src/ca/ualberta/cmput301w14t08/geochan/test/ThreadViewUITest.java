package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.regex.Pattern;
import com.robotium.solo.Solo;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

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
    
    /**
     * Helper function that launches Thread View fragment
     */
    private void getToThreadViewFrag() {
        assertTrue("Did not launch thread list", solo.waitForFragmentById(R.id.fragment_container));
        // Testing with just first thread in thread list
        solo.clickInList(2, 0);
        assertTrue("Thread view frag did not launch", solo.waitForFragmentByTag("thread_view_fragment"));
    }
    
    private void pullToRefresh() {
        // Pull to refresh
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int midX = size.x / 2;
        int midY = size.y / 2;
        int dragLength = size.y / 5;
        solo.drag(midX, midX, midY - dragLength, midY + dragLength, 10);
    }
    /**
     * Searches for the correct buttons/text on the comment reply fragment
     */
    public void testReplyFragment() {
        getToThreadViewFrag();
        solo.clickOnView(solo.getView(R.id.comment_reply_button));        
        assertTrue("Reply fragment did not launch", solo.waitForFragmentByTag("postFrag"));
        solo.searchButton(solo.getString(R.string.location));
        solo.searchButton(solo.getString(R.string.attach_image));
        solo.searchButton(solo.getString(R.string.post));
        solo.searchText(solo.getString(R.string.commentHint));
        solo.goBack();
        assertTrue("Reply fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
    }
    
    /**
     * Make a test post and check that it posts to the thread list
     */
    public void testThreadReply() {
        getToThreadViewFrag();
        solo.clickOnView(solo.getView(R.id.comment_reply_button)); 
        assertTrue("Reply fragment did not launch", solo.waitForFragmentByTag("postFrag"));
        solo.clickOnEditText(0);
        solo.clearEditText(0);
        
        String matchStr = "UI Test Reply Thread";
        solo.enterText(0, matchStr);
        // Uncomment both lines and delete goBack() to actually post a comment to thread
        //solo.clickOnButton(solo.getString(R.string.post));
        solo.goBack();
        
        pullToRefresh();
        assertTrue("Reply fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
        //solo.searchText(matchStr);
    }
    
    /**
     * Test replying to a child comment
     * Will not work if comment list is null
     */
    public void testCommentReply() {
        getToThreadViewFrag();
        
        // Click on first comment
        solo.sleep(1000);
        solo.clickInList(4, 0);
        solo.clickOnView(solo.getView(R.id.comment_reply_button, 1));
        assertTrue("Reply fragment did not launch", solo.waitForFragmentByTag("postFrag"));
        solo.clickOnEditText(0);
        solo.clearEditText(0);
        
        String matchStr = "UI Test Reply Comment";
        solo.enterText(0, matchStr);
        
        // Uncomment both lines and remove goBack() to actually post a comment
        //solo.clickOnButton(solo.getString(R.string.post));
        solo.goBack();
        
        pullToRefresh();
        assertTrue("Reply fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
        //solo.searchText(matchStr);
    }
    
    /**
     * Check that the image Dialogs display correctly
     */
    public void testImageDialog() {
        getToThreadViewFrag();
        solo.clickOnView(solo.getView(R.id.comment_reply_button));
        assertTrue("Reply fragment did not launch", solo.waitForFragmentByTag("postFrag"));
        solo.clickOnButton(solo.getString(R.string.attach_image));
        solo.searchText(solo.getString(R.string.attach_image_title));
        solo.searchText(solo.getString(R.string.attach_image_dialog));
        solo.searchButton(solo.getString(R.string.camera_dialog));
        solo.searchButton(solo.getString(R.string.gallery_dialog));
        solo.goBack();
    }
    
    /**
     * Test the custom location fragment launches and interacts correctly
     */
    public void testCustomLocation() {
        getToThreadViewFrag();
        solo.clickOnImageButton(0);
        assertTrue("Reply fragment did not launch", solo.waitForFragmentByTag("postFrag"));
        solo.clickOnButton(solo.getString(R.string.location));
        assertTrue("Custom location fragment did not launch", solo.waitForFragmentByTag("customLocFrag"));
        solo.clickOnButton(solo.getString(R.string.current_location_button_text));
        assertTrue("Location fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
        solo.clickOnButton("Current Location");
        assertTrue("Custom location fragment did not launch", solo.waitForFragmentByTag("customLocFrag"));
        solo.searchText(solo.getString(R.string.location_log));
        solo.searchButton(solo.getString(R.string.current_location_button_text));
        solo.searchButton(solo.getString(R.string.new_location_button_text));
        solo.clickLongOnView(solo.getView(R.id.map_view), 1000);
        solo.clickOnButton(solo.getString(R.string.new_location_button_text));
        assertTrue("Location fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
    }
    
    /**
     * Test thread + comment buttons, and comment inflation
     */
    public void testFavoriteButtons() {
        getToThreadViewFrag();
        solo.clickOnView(solo.getView(R.id.comment_star_button));
        solo.clickOnView(solo.getView(R.id.comment_star_button));
        solo.clickInList(4, 0);
        solo.sleep(1000);
        solo.clickOnView(solo.getView(R.id.comment_star_button, 1));
        solo.clickOnView(solo.getView(R.id.comment_star_button, 1));
        solo.clickInList(4, 0);
    }
    
    /**
     * Test map fragment, as well as get directions button
     */
    public void testThreadLocation() {
        getToThreadViewFrag();
        solo.clickOnView(solo.getView(R.id.thread_map_button));
        assertTrue("Map View fragment did not launch", solo.waitForFragmentByTag("mapFrag"));
        solo.clickOnButton(solo.getString(R.string.get_directions_button_text));
        solo.goBack();
        assertTrue("Map fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
    }
    
    public void testEditing() {
        
        // Only works if you are the OP
        solo.clickOnActionBarItem(R.id.thead_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.score_highest)));
        
        getToThreadViewFrag();
        solo.sleep(1000);
        solo.clickOnView(solo.getView(R.id.thread_edit_button));
        assertTrue("Edit fragment did not launch", solo.waitForFragmentByTag("editFrag"));
        String edit = "Edit";
        solo.clickOnEditText(0);
        solo.clearEditText(0);
        solo.enterText(0, edit);
        solo.clickOnButton(solo.getString(R.string.make_edit));
        assertTrue("Edit fragment did not return to Thread view fragment", solo.waitForFragmentByTag("thread_view_fragment"));
        solo.searchText(edit);
    }
    
    /**
     * Test each of the sorting buttons on the thread view fragment
     */
    public void testSortButtons() {
        getToThreadViewFrag();
        solo.clickOnActionBarItem(R.id.comment_sort);

        //Check that text is properly displayed
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.score_highest))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.score_lowest))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.date_new))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.date_old))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.sort_location))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.sort_image))));
        
        //Check that all menu buttons are clickable
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.score_highest)));
        
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.score_lowest)));
        
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.date_new)));
        
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.date_old)));
        
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.sort_image)));
        
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.sort_location)));
        
        assertTrue("Sort by location fragment did not open", solo.waitForFragmentByTag("customLocFrag"));
        
        solo.clickOnButton(solo.getString(R.string.current_location_button_text));
        assertTrue("Sort by location fragment did not return", solo.waitForFragmentById(R.id.fragment_container));
        
        //Set custom location buttons
        solo.clickOnActionBarItem(R.id.comment_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.sort_location)));
        assertTrue("Sort by location fragment did not open", solo.waitForFragmentByTag("customLocFrag"));
        solo.clickLongOnView(solo.getView(R.id.map_view), 1000);
        solo.clickOnButton(solo.getString(R.string.new_location_button_text));
        assertTrue("Sort by location fragment did not return", solo.waitForFragmentById(R.id.fragment_container));
        
    }

    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    
}
