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
}
