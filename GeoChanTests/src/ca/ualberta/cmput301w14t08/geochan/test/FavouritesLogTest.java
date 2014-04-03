package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Tests for the functionality of the FavouritesLog
 *
 */
public class FavouritesLogTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private FavouritesLog favLog = null;

    public FavouritesLogTest() {
        super(MainActivity.class);
    }
    
    protected void setUp() throws Exception {
        favLog = FavouritesLog.getInstance(getActivity());
        assertNotNull(favLog);
        super.setUp();
    }
    
    /**
     * test adding a comment as a favourite, assert it is logged in the comments log
     */
    public void testAddFavouriteComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null, null);
        Comment comment2 = new Comment(commentText, null, null);
        Comment comment3 = new Comment(commentText, null, null);
        favLog.addComment(comment1);
        favLog.addComment(comment2);
        favLog.addComment(comment3);

        int count = 0;
        for(Comment c : favLog.getComments()) {
            ++count;
            assertTrue("Comment text must match", c.getTextPost() == commentText);
        }
        assertTrue("count must be 3", count == 3);
    }
    
    /**
     * test adding a thread as a favourite, assert it is logged in the threads log
     */
    public void testAddFavouriteThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null, null), threadText);
        favLog.addThreadComment(thread1);
        favLog.addThreadComment(thread2);
        favLog.addThreadComment(thread3);
        
        int count = 0;
        for(ThreadComment t : favLog.getThreads()) {
            ++count;
            assertTrue("Comment text must match", t.getBodyComment().getTextPost() == commentText);
            assertTrue("Thread title must match", t.getTitle() == threadText);
        }
        assertTrue("count must be 3", count == 3);
    }
    
    /**
     * Test the hasComment method by adding comments to the log
     * and calling the hasComment method on the log with those comments
     * to verify that it works
     */
    public void testHasComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null, null);
        Comment comment2 = new Comment(commentText, null, null);
        Comment comment3 = new Comment(commentText, null, null);
        favLog.addComment(comment1);
        favLog.addComment(comment2);
        favLog.addComment(comment3);
        assertTrue("Must Have Comment", favLog.hasComment(comment1.getId()));
        assertTrue("Must Have Comment", favLog.hasComment(comment2.getId()));
        assertTrue("Must Have Comment", favLog.hasComment(comment3.getId()));
    }
    
    /**
     * Test the hasThreadComment method by adding threadComments to the log
     * and calling the hasThreadComment method on the log with those threadComments
     * to verify that it works
     */
    public void testHasThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null, null), threadText);
        favLog.addThreadComment(thread1);
        favLog.addThreadComment(thread2);
        favLog.addThreadComment(thread3);
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread1.getId()));
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread2.getId()));
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread3.getId()));
    }
    
    /**
     * Test removing a comment from the log by first adding it,
     * then calling the remove method and then verifying that the log
     * no longer contains said comment.
     */
    public void testRemoveComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null, null);
        Comment comment2 = new Comment(commentText, null, null);
        Comment comment3 = new Comment(commentText, null, null);
        favLog.addComment(comment1);
        favLog.addComment(comment2);
        favLog.addComment(comment3);
        assertTrue("count must be 3", favLog.getComments().size() == 3);
        favLog.removeComment(comment1);
        favLog.removeComment(comment2);
        favLog.removeComment(comment3);
        assertTrue("count must be 0", favLog.getComments().size() == 0);
    }
    
    /**
     * Test removing a threadComment from the log by first adding it,
     * then calling the remove method and then verifying that the log
     * no longer contains said threadComment.
     */
    public void testRemoveThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null, null), threadText);
        favLog.addThreadComment(thread1);
        favLog.addThreadComment(thread2);
        favLog.addThreadComment(thread3);
        assertTrue("count must be 3", favLog.getThreads().size() == 3);
        favLog.removeThreadComment(thread1);
        favLog.removeThreadComment(thread2);
        favLog.removeThreadComment(thread3);
        assertTrue("count must be 0", favLog.getThreads().size() == 0);
    }
}
