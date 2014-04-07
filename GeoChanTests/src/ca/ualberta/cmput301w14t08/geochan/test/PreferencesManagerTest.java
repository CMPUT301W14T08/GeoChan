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

import android.provider.Settings.Secure;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;

/**
 * Tests the functionality of the PreferencesManager
 *
 */
public class PreferencesManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;
    private PreferencesManager manager;
    
    public PreferencesManagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        PreferencesManager.generateInstance(activity);
        this.manager = PreferencesManager.getInstance();
    }
    
    /**
     * Test that saving the ThreadSort method works correctly by saving the 
     * sorting method and verifying that retrieving the ThreadSort method returns the 
     * correct result
     */
    public void testThreadSort() {
        manager.setThreadSort(SortUtil.SORT_DATE_NEWEST);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_DATE_NEWEST);
        manager.setThreadSort(SortUtil.SORT_DATE_OLDEST);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_DATE_OLDEST);
        manager.setThreadSort(SortUtil.SORT_IMAGE);
        assertTrue("should be date newest", manager.getThreadSort() == SortUtil.SORT_IMAGE);
    }
    
    /**
     * Test that saving the CommentSort method works correctly by saving the 
     * sorting method and verifying that retrieving the CommentSort method returns the 
     * correct result
     */
    public void testCommentSort() {
        manager.setCommentSort(SortUtil.SORT_DATE_NEWEST);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_DATE_NEWEST);
        manager.setCommentSort(SortUtil.SORT_DATE_OLDEST);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_DATE_OLDEST);
        manager.setCommentSort(SortUtil.SORT_IMAGE);
        assertTrue("should be date newest", manager.getCommentSort() == SortUtil.SORT_IMAGE);
    }
    
    /**
     * Tests that the testID method does indeed return the correct android ID
     */
    public void testID() {
        String id = Secure.getString(activity.getContentResolver(), Secure.ANDROID_ID);
        assertTrue("id must match", id == manager.getId());
    }
}
