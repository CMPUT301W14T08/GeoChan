package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.Date;

import junit.framework.TestCase;
import android.graphics.Picture;
import ca.ualberta.cmput301w14t08.geochan.Comment;

public class CommentTest extends TestCase {

    public void testHasImage() {
        Comment comment = new Comment("test", new Picture(), null);
        assertTrue("Comment has image", comment.hasImage());
    }

    @SuppressWarnings("unused")
    public void testAddChild() {
        Comment parent = new Comment("test", null);
        Comment reply = new Comment("test_reply", null, parent);
        assertNotNull("comment has a reply", parent.getChildren());
    }
    
    public void testConstruct() {
        Comment comment = new Comment("Hola", null);
        assertNull(comment.getParent());
    }
    
    public void testSortByDateNewest(){
        /*
         * Tests the implementation of Comment.sortChildren("DATE_NEWEST");
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
        
        c1.addChild(c5);
        c1.addChild(c3);
        c1.addChild(c4);
        c1.addChild(c2);
        
        c1.sortChildren("DATE_NEWEST");

        assertTrue("c5 is at index 0", (c1.getChildren().get(0)) == c5);
        assertTrue("c4 is at index 1", (c1.getChildren().get(1)) == c4);
        assertTrue("c3 is at index 2", (c1.getChildren().get(2)) == c3);
        assertTrue("c2 is at index 3", (c1.getChildren().get(3)) == c2);
    }
    
    public void testSortByDateOldest(){
        /*
         * Tests the implementation of Comment.sortChildren("DATE_OLDEST");
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
        
        c1.addChild(c5);
        c1.addChild(c3);
        c1.addChild(c4);
        c1.addChild(c2);
        
        c1.sortChildren("DATE_OLDEST");

        assertTrue("c2 is at index 0", (c1.getChildren().get(0)) == c2);
        assertTrue("c3 is at index 1", (c1.getChildren().get(1)) == c3);
        assertTrue("c4 is at index 2", (c1.getChildren().get(2)) == c4);
        assertTrue("c5 is at index 3", (c1.getChildren().get(3)) == c5);
    }
    
    
}
