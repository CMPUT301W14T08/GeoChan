package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.regex.Pattern;

import io.searchbox.indices.settings.GetSettings;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.activities.PreferencesActivity;
import android.content.res.Resources;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ListView;

import com.robotium.solo.Solo;



/**
 * Tests for the UI components of the main ThreadList.
 *
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
        solo.assertCurrentActivity("Not main activity", MainActivity.class);
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
        solo.assertCurrentActivity("Not main activity", MainActivity.class);
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
     * Test that the change username dialog is reflected in the preferences view
     */
    public void testListDisplay() {
        
        // Robotium clickOnList() method does not work with pull to refresh list
        // Must click on text contained within each list item
        
        
    }
    
    /**
     * Test that the new thread button displays the new thread fragment
     */
    public void testNewThreadButton() {
        assertTrue("Thread view fragment is not displayed", solo.waitForFragmentById(R.id.fragment_container));
        View button = solo.getView(R.id.action_add_thread);
        solo.clickOnView(button);
        assertTrue("Add new thread fragment is not displayed", solo.waitForFragmentByTag("postThreadFrag"));
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
        solo.goBack();
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

    }
    
    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    

}
