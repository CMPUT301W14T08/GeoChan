/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.test;

import com.robotium.solo.Solo;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;

/**
 * Tests for UI elements that are too small for their own testing class
 * Also general UI tests not specific to a particular fragment
 * Uses Robotium library
 * 
 * @author Tom Krywitsky
 */
public class MiscUITest extends ActivityInstrumentationTestCase2<MainActivity> {

    Solo solo;
    
    public MiscUITest() {
        super(MainActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    public void testFavoritesSpinners() {
        assertTrue("Did not launch thread list", solo.waitForFragmentById(R.id.fragment_container));
        solo.clickOnActionBarItem(R.id.action_favourites);
        assertTrue("Favorites fragment is not displayed", solo.waitForFragmentByTag("favouritesFrag"));
        solo.pressSpinnerItem(0, 0);
        solo.pressSpinnerItem(0, 1);
    }
    
    /**
     * Test orientation changes for each fragment
     * 
     * @author Tom Krywitsky
     */
    public void testOrientationChange() {
        assertTrue("Did not launch thread list", solo.waitForFragmentById(R.id.fragment_container));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.setActivityOrientation(Solo.PORTRAIT);
    }
    
    
    @Override
    public void tearDown() throws Exception {
      solo.finishOpenedActivities();
    }
    
}
