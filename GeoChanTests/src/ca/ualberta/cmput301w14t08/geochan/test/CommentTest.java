package ca.ualberta.cmput301w14t08.geochan.test;

import android.graphics.Picture;
import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.Comment;

public class CommentTest extends TestCase {

    public void testHasImage() {
        Comment comment = new Comment("test",new Picture(),null);
        assertTrue("Comment has image",comment.hasImage());
    }

    @SuppressWarnings("unused")
    public void testAddChild() {
        Comment parent = new Comment("test", null);
        Comment reply = new Comment("test_reply",null,parent);
        assertNotNull("comment has a reply", parent.getChildren());
    }

}
