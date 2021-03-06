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

import android.support.v4.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Test the ThreadView fragment for proper functionality
 */
public class ThreadViewFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    Fragment fragment;
    ListView threadViewList;
    MainActivity activity;
    
    public ThreadViewFragmentTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
        Comment testComment = new Comment("hello", null, null);
        //testComment.addChild(new Comment("test", null));
        ThreadList.addThread(testComment, "test thread");
        //Click the thread to open the fragment
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        });
        ThreadViewFragment fragment = (ThreadViewFragment) waitForFragment("thread_view_fragment", 5000);
        assertNotNull("fragment not initialized",fragment);
    }
   
    /**
     * Test that the listview is displayed correctly on the screen
     */
    public void testListViewVisibility() {
        threadViewList = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_view_list);
        View rootView = activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(rootView, threadViewList);
    }
    
    /**
     * http://stackoverflow.com/a/17789933
     * Sometimes the emulator is too slow.
     */
    protected Fragment waitForFragment(String tag, int timeout) {
        long endTime = SystemClock.uptimeMillis() + timeout;
        while (SystemClock.uptimeMillis() <= endTime) {

            Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment != null) {
                return fragment;
            }
        }
        return null;
    }
}
