package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

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
    
    public void testAddFavouriteComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null);
        Comment comment2 = new Comment(commentText, null);
        Comment comment3 = new Comment(commentText, null);
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
    
    public void testAddFavouriteThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null), threadText);
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
    
    public void testHasComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null);
        Comment comment2 = new Comment(commentText, null);
        Comment comment3 = new Comment(commentText, null);
        favLog.addComment(comment1);
        favLog.addComment(comment2);
        favLog.addComment(comment3);
        assertTrue("Must Have Comment", favLog.hasComment(comment1.getId()));
        assertTrue("Must Have Comment", favLog.hasComment(comment2.getId()));
        assertTrue("Must Have Comment", favLog.hasComment(comment3.getId()));
    }
    
    public void testHasThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null), threadText);
        favLog.addThreadComment(thread1);
        favLog.addThreadComment(thread2);
        favLog.addThreadComment(thread3);
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread1.getId()));
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread2.getId()));
        assertTrue("Must Have Thread", favLog.hasThreadComment(thread3.getId()));
    }
    
    public void testRemoveComment() {
        String commentText = "test";
        Comment comment1 = new Comment(commentText, null);
        Comment comment2 = new Comment(commentText, null);
        Comment comment3 = new Comment(commentText, null);
        favLog.addComment(comment1);
        favLog.addComment(comment2);
        favLog.addComment(comment3);
        assertTrue("count must be 3", favLog.getComments().size() == 3);
        favLog.removeComment(comment1);
        favLog.removeComment(comment2);
        favLog.removeComment(comment3);
        assertTrue("count must be 0", favLog.getComments().size() == 0);
    }
    
    public void testRemoveThreadComment() {
        String commentText = "comment";
        String threadText = "thread";
        ThreadComment thread1 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread2 = new ThreadComment(new Comment(commentText, null), threadText);
        ThreadComment thread3 = new ThreadComment(new Comment(commentText, null), threadText);
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
