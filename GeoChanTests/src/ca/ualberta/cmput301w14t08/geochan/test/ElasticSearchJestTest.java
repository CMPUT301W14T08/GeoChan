package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.Thread;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;

public class ElasticSearchJestTest extends TestCase {

    public ElasticSearchJestTest(String name) {
        super(name);
    }

    public void testPutAndSetThreads() {
        ElasticSearchClient elasticClient = new ElasticSearchClient();
        Thread thread1 = new Thread();
        Comment c1 = new Comment("Test Message", null);
        Comment c2 = new Comment("Test Message", null);
        
        thread1.addComment(c1);
        thread1.addComment(c2);
        
        elasticClient.putThread(thread1);
        ArrayList<Thread> threadList = elasticClient.setThreads();
        
        
        
    }
}
