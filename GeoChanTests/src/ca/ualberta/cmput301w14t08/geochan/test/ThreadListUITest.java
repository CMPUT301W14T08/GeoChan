package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.regex.Pattern;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.activities.PreferencesActivity;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.view.View;
import android.widget.ListView;

import com.robotium.solo.Solo;



/**
 * Tests for the UI components of the main ThreadList.
 * Simplified using Robotium library
 * 
 * @author Thomas Krywitsky
 */
public class ThreadListUITest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    
    public ThreadListUITest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    /**
     * Launch the preferences activity and check that activity displays correctly
     * 
     */
    public void testSettingsDisplay() {
        assertTrue("Thread view fragment is not displayed", solo.waitForFragmentById(R.id.fragment_container));
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        solo.assertCurrentActivity("Not preferences activity", PreferencesActivity.class);
        assertTrue(solo.searchText("Settings"));
        assertTrue(solo.searchText("Change Username"));
        assertTrue(solo.searchText("Device Hash"));
        solo.goBack();
        solo.assertCurrentActivity("Didn't return from Preferences", MainActivity.class);
    }
 
    
    /**
     * Test that the change username dialog is reflected in the preferences view
     */
    public void testUsernameChange() {
        assertTrue("Thread view fragment is not displayed", solo.waitForFragmentById(R.id.fragment_container));
        solo.sendKey(Solo.MENU);
        solo.clickOnText("Settings");
        assertTrue(solo.searchText("Change Username"));
        solo.clickOnText("Change Username");
        solo.clearEditText(0);
        solo.enterText(0, "UserTest");
        solo.clickOnButton("OK");
        assertTrue(solo.searchText("UserTest"));
        
        //Clean up
        solo.clickOnText("Change Username");
        solo.clearEditText(0);
        solo.enterText(0, "Anon");
    }
    
    /**
     * Test that the new thread button displays the new thread fragment
     */
    public void testNewThreadButton() {
        assertTrue("Thread view fragment is not displayed", solo.waitForFragmentById(R.id.fragment_container));
        View button = solo.getView(R.id.action_add_thread);
        solo.clickOnView(button);
        assertTrue("Add new thread fragment is not displayed", solo.waitForFragmentByTag("postFrag"));
        solo.goBack();
        assertTrue("Did not return to thread list", solo.waitForFragmentById(R.id.fragment_container));
    }
    
    /**
     * Test each of the sorting buttons on the threadList fragment
     */
    public void testSortButtons() {
        assertTrue("Thread view fragment is not displayed", solo.waitForFragmentById(R.id.fragment_container));
        solo.clickOnActionBarItem(R.id.thead_sort);

        //Check that text is properly displayed
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.score_highest))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.score_lowest))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.date_new))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.date_old))));
        assertTrue(solo.searchText(Pattern.quote(solo.getString(R.string.sort_location))));
        
        //Check that all menu buttons are clickable
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.score_highest)));
        solo.clickOnActionBarItem(R.id.thead_sort);
  
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.score_lowest)));
        solo.clickOnActionBarItem(R.id.thead_sort);
        
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.date_new)));
        solo.clickOnActionBarItem(R.id.thead_sort);
        
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.date_old)));
        solo.clickOnActionBarItem(R.id.thead_sort);
        
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.sort_location)));
        assertTrue("Sort by location fragment did not open", solo.waitForFragmentByTag("customLocFrag"));
        
        solo.clickOnButton(solo.getString(R.string.current_location_button_text));
        assertTrue("Sort by location fragment did not return", solo.waitForFragmentById(R.id.fragment_container));
        
        //Set custom location buttons
        solo.clickOnActionBarItem(R.id.thead_sort);
        solo.clickOnMenuItem(Pattern.quote(solo.getString(R.string.sort_location)));
        assertTrue("Sort by location fragment did not open", solo.waitForFragmentByTag("customLocFrag"));
        solo.clickLongOnView(solo.getView(R.id.map_view), 1000);
        solo.clickOnButton(solo.getString(R.string.new_location_button_text));
        assertTrue("Sort by location fragment did not return", solo.waitForFragmentById(R.id.fragment_container));
        
    }
    
    /**
     * Test for successfull launch of favorites fragment
     */
    public void testFavoritesButton() {
        solo.clickOnActionBarItem(R.id.action_favourites);
        assertTrue("Favorites fragment is not displayed", solo.waitForFragmentByTag("favouritesFrag"));
        solo.pressSpinnerItem(0, 0);
        solo.pressSpinnerItem(0, 1);
        solo.goBack();
        assertTrue("Did not return to thread list", solo.waitForFragmentById(R.id.fragment_container));
    }
    
    /**
     * Test pull to refresh with a simulated screen drag
     */
    public void testPullToRefresh() {
        
        // For different resolutions
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int midX = size.x / 2;
        int midY = size.y / 2;
        int dragLength = size.y / 5;
        solo.drag(midX, midX, midY - dragLength, midY + dragLength, 10);
        assertTrue("List did not refresh correctly", solo.waitForView(R.id.thread_list));
    }
    
    /**
     * Test that the list UI is scrollable and clickable
     */
    public void testListDisplay() {
        solo.scrollDownList(0);
        solo.scrollUpList(0);
        
        // Get number of list items
        ListView list = (ListView) solo.getView(R.id.thread_list);
        int count = list.getChildCount();
        // First 2 views are PullToRefresh views
        for (int i = 2; i < count; ++i) {
            // Only can click visible list items
            solo.clickInList(i, 0);
            assertTrue("Thread view frag did not launch", solo.waitForFragmentByTag("thread_view_fragment"));
            solo.goBack();
            assertTrue("Did not return to thread list fragment", solo.waitForFragmentById(R.id.fragment_container));
        }
        
    }
     
    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    
}
