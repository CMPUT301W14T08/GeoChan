package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.Comment;
import ca.ualberta.cmput301w14t08.geochan.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.Thread;

public class ThreadListTest extends TestCase {

    public void testAddThread() {
        Comment comment = new Comment("Test", null);
        ThreadList.addThread(comment, "Test title");
        assertTrue(ThreadList.getThreads().size() == 1);
    }
    
    @SuppressWarnings("static-access")
    public void testSortThreadsByDateNewest(){
        /*
         * Tests ThreadList.sortThreads("DATE_NEWEST")
         */
        long extraTime = 1320000;
        ThreadList tm = new ThreadList();
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        Thread t3 = new Thread();
        Thread t4 = new Thread();
        Thread t5 = new Thread();
        
        Date currentDate = new Date();
        
        t1.setThreadDate(new Date(currentDate.getTime() + 1*extraTime));
        t2.setThreadDate(new Date(currentDate.getTime() + 2*extraTime));
        t3.setThreadDate(new Date(currentDate.getTime() + 3*extraTime));
        t4.setThreadDate(new Date(currentDate.getTime() + 4*extraTime));
        t5.setThreadDate(new Date(currentDate.getTime() + 5*extraTime));
        
        tm.setThreads(new ArrayList<Thread>());
        tm.addThread(t1);
        tm.addThread(t2);
        tm.addThread(t5);
        tm.addThread(t3);
        tm.addThread(t4);
        
        tm.sortThreads("DATE_NEWEST");
        
        assertTrue("t5 is at index 0", tm.getThreads().get(0) == t5);
        assertTrue("t4 is at index 1", tm.getThreads().get(1) == t4);
        assertTrue("t3 is at index 2", tm.getThreads().get(2) == t3);
        assertTrue("t2 is at index 3", tm.getThreads().get(3) == t2);
        assertTrue("t1 is at index 4", tm.getThreads().get(4) == t1);
    }
    
    @SuppressWarnings("static-access")
    public void testSortThreadsByDateOldest(){
        /*
         * Tests ThreadList.sortThreads("DATE_OLDEST")
         */
        long extraTime = 1320000;
        ThreadList tm = new ThreadList();
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        Thread t3 = new Thread();
        Thread t4 = new Thread();
        Thread t5 = new Thread();
        
        Date currentDate = new Date();
        
        t1.setThreadDate(new Date(currentDate.getTime() + 1*extraTime));
        t2.setThreadDate(new Date(currentDate.getTime() + 2*extraTime));
        t3.setThreadDate(new Date(currentDate.getTime() + 3*extraTime));
        t4.setThreadDate(new Date(currentDate.getTime() + 4*extraTime));
        t5.setThreadDate(new Date(currentDate.getTime() + 5*extraTime));
        
        tm.setThreads(new ArrayList<Thread>());
        tm.addThread(t1);
        tm.addThread(t2);
        tm.addThread(t5);
        tm.addThread(t3);
        tm.addThread(t4);
        
        tm.sortThreads("DATE_OLDEST");
        
        assertTrue("t1 is at index 0", tm.getThreads().get(0) == t1);
        assertTrue("t2 is at index 1", tm.getThreads().get(1) == t2);
        assertTrue("t3 is at index 2", tm.getThreads().get(2) == t3);
        assertTrue("t4 is at index 3", tm.getThreads().get(3) == t4);
        assertTrue("t5 is at index 4", tm.getThreads().get(4) == t5);
    }
}
