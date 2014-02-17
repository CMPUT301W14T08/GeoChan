package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
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
}