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

import android.app.Fragment;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Tests for our PostFragment class.
 * @author Henry Pabst
 *
 */
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
                
                //Fragment fragment = (ThreadViewFragment) waitForFragment("thread_view_fragment", 2000);
                assertNotNull("threadViewFragment is null", fragment);
                ImageButton reply = (ImageButton) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.post_button);
                assertNotNull("reply button is null", reply);
                //reply.performClick();
            }
        });
        //fragment = (PostCommentFragment) waitForFragment("comFrag", 5000);
    }
    
   /**
    * Test that the activity and fragment were created
    */
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
