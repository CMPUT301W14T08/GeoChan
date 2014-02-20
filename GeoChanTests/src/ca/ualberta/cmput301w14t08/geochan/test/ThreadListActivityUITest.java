package ca.ualberta.cmput301w14t08.geochan.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.ThreadListActivity;

public class ThreadListActivityUITest extends ActivityInstrumentationTestCase2<ThreadListActivity> {

    public ThreadListActivityUITest() {
        super(ThreadListActivity.class);
    }

    public void testDummyFail() {
        fail("LOL");
    }

    public void testDummyPass() {
        assertEquals("1 equals 1", 1, 1);
    }
    
    public void testListViewVisibility() {
        Intent intent = new Intent();
        setActivityIntent(intent);
        ThreadListActivity activity = getActivity();
        ListView listView = (ListView) activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.thread_list);
        View rootView = activity.getWindow().getDecorView();
        ViewAsserts.assertOnScreen(rootView, listView);
    }
}