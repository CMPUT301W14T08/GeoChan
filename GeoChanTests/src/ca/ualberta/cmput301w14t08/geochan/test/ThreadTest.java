package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.Date;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.Comment;
import ca.ualberta.cmput301w14t08.geochan.Thread;

public class ThreadTest extends TestCase {
    
    public void testSortByDate(){
        /*
         * Tests the implementation of Thread.sortByDate();
         */
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Date currentDate = new Date();
        c1.setCommentDate(new Date(currentDate.getTime() + 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 5*extraTime));
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        Thread thread = new Thread(c1, "This thread is for testing!");
        thread.addComment(c4);
        thread.addComment(c3);
        thread.addComment(c5);
        thread.addComment(c2);
        thread.sortByDate();

        assertTrue("c5 is at index 0", (thread.getComments().get(0)) == c5);
        assertTrue("c4 is at index 1", (thread.getComments().get(1)) == c4);
        assertTrue("c3 is at index 2", (thread.getComments().get(2)) == c3);
        assertTrue("c2 is at index 3", (thread.getComments().get(3)) == c2);
    }
    
    public void testSortByDefault(){
        /*
         * Tests the implementation of Thread.sortByDefault();
         */
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Date currentDate = new Date();
        c1.setCommentDate(new Date(currentDate.getTime() + 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 5*extraTime));
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        Thread thread = new Thread(c1, "This thread is for testing!");
        thread.addComment(c4);
        thread.addComment(c3);
        thread.addComment(c5);
        thread.addComment(c2);
        thread.sortByDefault();

        assertTrue("c2 is at index 0", (thread.getComments().get(0)) == c2);
        assertTrue("c3 is at index 1", (thread.getComments().get(1)) == c3);
        assertTrue("c4 is at index 2", (thread.getComments().get(2)) == c4);
        assertTrue("c5 is at index 3", (thread.getComments().get(3)) == c5);
    }

}
