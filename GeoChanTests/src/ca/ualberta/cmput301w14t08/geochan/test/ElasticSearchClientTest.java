package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

public class ElasticSearchClientTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private ElasticSearchClient client;
    
    public ElasticSearchClientTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testConstruction() {
        client = ElasticSearchClient.getInstance();
        assertNotNull(client.getThreadCount());
    }
    
    public void testPostAndGetThread() {
        client = ElasticSearchClient.getInstance();
        Comment comment = new Comment();
        ThreadComment threadComment = new ThreadComment(comment, "Test");
        
        Thread thread = client.postThread(threadComment);
        while (thread.isAlive()) {}; // Wait for server
        
        ArrayList<ThreadComment> list = client.getThreads();
        assertTrue(list.contains(threadComment));
    }
    
    public void testPostAndGetComment() {
        client = ElasticSearchClient.getInstance();
        ArrayList<ThreadComment> list = client.getThreads();
        assertTrue(list.size() > 0);
        
        Comment comment = new Comment("Test", null, list.get(0).getBodyComment());
        Thread thread = client.postComment(list.get(0), list.get(0).getBodyComment(), comment);
        while (thread.isAlive()) {}; // Wait for server
        
        ArrayList<Comment> commentList = client.getComments(list.get(0).getBodyComment());
        assertTrue(commentList.contains(comment));
    }
    
    public void testCountThreads() {
        client = ElasticSearchClient.getInstance();
        ArrayList<ThreadComment> list = client.getThreads();
        assertTrue(list.size() > 0);
        
        int count = client.getThreadCount();
        Comment comment = new Comment();
        ThreadComment threadComment = new ThreadComment(comment, "Test");
        
        Thread thread = client.postThread(threadComment);
        while (thread.isAlive()) {}; // Wait for server
        
        assertTrue(client.getThreadCount() == (count + 1));
    }
    
    public void testCountComments() {
        client = ElasticSearchClient.getInstance();
        ArrayList<ThreadComment> list = client.getThreads();
        assertTrue(list.size() > 0);
        
        int count = client.getCommentCount(list.get(0).getBodyComment());
        Comment comment = new Comment("Test", null, list.get(0).getBodyComment());
        
        Thread thread = client.postComment(list.get(0), list.get(0).getBodyComment(), comment);
        while (thread.isAlive()) {}; // Wait for server
        
        assertTrue(client.getCommentCount(list.get(0).getBodyComment()) == (count + 1));
    }
}
