package ca.ualberta.cmput301w14t08.geochan.test;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.Comment;
import ca.ualberta.cmput301w14t08.geochan.ThreadList;

public class ThreadListTest extends TestCase {

    public void testAddThread() {
        Comment comment = new Comment("Test", null);
        ThreadList.addThread(comment, "Test title");
        assertTrue(ThreadList.getThreads().size() == 1);
    }

}
