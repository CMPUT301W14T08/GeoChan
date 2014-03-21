package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;
import java.util.Date;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class ThreadTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    private MainActivity activity;
    private LocationListenerService locationListenerService;
    
    public ThreadTest(){
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        //Shamelessly ripped from GeolocationTest to test Comments with Geolocations.
        super.setUp();
        this.activity = getActivity();
        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();
    }
    
    public void testAddComment(){
        ThreadComment t1 = new ThreadComment();
        Comment c1 = new Comment();
        
        t1.addComment(c1);
        
        assertTrue("Comment added successfuly.", t1.getBodyComment().getChildren().contains(c1));
    }
    
    public void testGetDistanceFrom(){
        ThreadComment t1 = new ThreadComment();
        GeoLocation g1 = new GeoLocation(5,5);
        
        t1.getBodyComment().getLocation().setCoordinates(0, 0);
        
        assertEquals("Distance calculated correctly.", t1.getDistanceFrom(g1),
                      Math.sqrt(50));
    }
    
    public void testGetTimeFrom(){
        ThreadComment t1 = new ThreadComment();
        Date d1 = new Date();
        
        assertEquals("Minimum time value calculated correctly.",t1.getTimeFrom(d1),
                    0.5);
        
        d1 = new Date(t1.getThreadDate().getTime() + 3600000);
        
        assertEquals("Time calculated correctly.", t1.getTimeFrom(d1),
                    1.0);
    }
    
    public void testGetScoreFromUser(){
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        GeoLocation g = new GeoLocation(0,0);
        
        t1.getBodyComment().getLocation().setCoordinates(0,0);
        t2.getBodyComment().getLocation().setCoordinates(5, 5);
        
        assertTrue("Scores calculated relatively correctly.",
                    t1.getScoreFromUser(g) >
                    t2.getScoreFromUser(g)); 
    }
    
    public void testSortByDateNewest(){
        ArrayList<ThreadComment> carrier = new ArrayList<ThreadComment>();
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        ThreadComment t4 = new ThreadComment();
        ThreadComment t5 = new ThreadComment();
        ThreadComment t6 = new ThreadComment();
        ThreadComment t7 = new ThreadComment();
        ThreadComment t8 = new ThreadComment();
        ThreadComment t9 = new ThreadComment();
        ThreadComment t10 = new ThreadComment();
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Comment c6 = new Comment();
        Comment c7 = new Comment();
        Comment c8 = new Comment();
        Comment c9 = new Comment();
        Comment c10 = new Comment();
        Date currentDate = new Date();
        
        c1.setCommentDate(new Date(currentDate.getTime() + 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 5*extraTime));
        c6.setCommentDate(new Date(currentDate.getTime() + 6*extraTime));
        c7.setCommentDate(new Date(currentDate.getTime() + 7*extraTime));
        c8.setCommentDate(new Date(currentDate.getTime() + 8*extraTime));
        c9.setCommentDate(new Date(currentDate.getTime() + 9*extraTime));
        c10.setCommentDate(new Date(currentDate.getTime() + 10*extraTime));
        
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        t6.setBodyComment(c6);
        t7.setBodyComment(c7);
        t8.setBodyComment(c8);
        t9.setBodyComment(c9);
        t10.setBodyComment(c10);
        
        carrier.add(t1);
        carrier.add(t2);
        carrier.add(t3);
        carrier.add(t4);
        carrier.add(t5);
        carrier.add(t6);
        carrier.add(t7);
        carrier.add(t8);
        carrier.add(t9);
        carrier.add(t10);
        
        SortUtil.sortThreads(SortUtil.SORT_DATE_NEWEST, 
                            carrier, new GeoLocation(0,0));
        
        assertTrue("t10 at index 0", carrier.get(0) == t10);
        assertTrue("t9 at index 1", carrier.get(1) == t9);
        assertTrue("t8 at index 2", carrier.get(2) == t8);
        assertTrue("t7 at index 3", carrier.get(3) == t7);
        assertTrue("t6 at index 4", carrier.get(4) == t6);
        assertTrue("t5 at index 5", carrier.get(5) == t5);
        assertTrue("t4 at index 6", carrier.get(6) == t4);
        assertTrue("t3 at index 7", carrier.get(7) == t3);
        assertTrue("t2 at index 8", carrier.get(8) == t2);
        assertTrue("t1 at index 9", carrier.get(9) == t1);   
    }
    
    public void testSortByDateOldest(){
        ArrayList<ThreadComment> carrier = new ArrayList<ThreadComment>();
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        ThreadComment t4 = new ThreadComment();
        ThreadComment t5 = new ThreadComment();
        ThreadComment t6 = new ThreadComment();
        ThreadComment t7 = new ThreadComment();
        ThreadComment t8 = new ThreadComment();
        ThreadComment t9 = new ThreadComment();
        ThreadComment t10 = new ThreadComment();
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Comment c6 = new Comment();
        Comment c7 = new Comment();
        Comment c8 = new Comment();
        Comment c9 = new Comment();
        Comment c10 = new Comment();
        Date currentDate = new Date();
        
        c1.setCommentDate(new Date(currentDate.getTime() + 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 5*extraTime));
        c6.setCommentDate(new Date(currentDate.getTime() + 6*extraTime));
        c7.setCommentDate(new Date(currentDate.getTime() + 7*extraTime));
        c8.setCommentDate(new Date(currentDate.getTime() + 8*extraTime));
        c9.setCommentDate(new Date(currentDate.getTime() + 9*extraTime));
        c10.setCommentDate(new Date(currentDate.getTime() + 10*extraTime));
        
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        t6.setBodyComment(c6);
        t7.setBodyComment(c7);
        t8.setBodyComment(c8);
        t9.setBodyComment(c9);
        t10.setBodyComment(c10);
        
        carrier.add(t1);
        carrier.add(t2);
        carrier.add(t3);
        carrier.add(t4);
        carrier.add(t5);
        carrier.add(t6);
        carrier.add(t7);
        carrier.add(t8);
        carrier.add(t9);
        carrier.add(t10);
        
        SortUtil.sortThreads(SortUtil.SORT_DATE_OLDEST, 
                            carrier, new GeoLocation(0,0));
        
        assertTrue("t1 at index 0", carrier.get(0) == t1);
        assertTrue("t2 at index 1", carrier.get(1) == t2);
        assertTrue("t3 at index 2", carrier.get(2) == t3);
        assertTrue("t4 at index 3", carrier.get(3) == t4);
        assertTrue("t5 at index 4", carrier.get(4) == t5);
        assertTrue("t6 at index 5", carrier.get(5) == t6);
        assertTrue("t7 at index 6", carrier.get(6) == t7);
        assertTrue("t8 at index 7", carrier.get(7) == t8);
        assertTrue("t9 at index 8", carrier.get(8) == t9);
        assertTrue("t10 at index 9", carrier.get(9) == t10);         
    }

    /**
     * Tests the sorting of comments in a thread by the score relative to the user.
     */
    public void testSortByUserScoreHighest(){
        ArrayList<ThreadComment> carrier = new ArrayList<ThreadComment>();
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        ThreadComment t4 = new ThreadComment();
        ThreadComment t5 = new ThreadComment();
        ThreadComment t6 = new ThreadComment();
        ThreadComment t7 = new ThreadComment();
        ThreadComment t8 = new ThreadComment();
        ThreadComment t9 = new ThreadComment();
        ThreadComment t10 = new ThreadComment();
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Comment c6 = new Comment();
        Comment c7 = new Comment();
        Comment c8 = new Comment();
        Comment c9 = new Comment();
        Comment c10 = new Comment();
        Date currentDate = new Date();
        GeoLocation geoLocation1 = new GeoLocation(5,5);
        GeoLocation geoLocation2 = new GeoLocation(10,10);
        GeoLocation geoLocation3 = new GeoLocation(15,15);
        GeoLocation geoLocation4 = new GeoLocation(20,20);
        GeoLocation geoLocation5 = new GeoLocation(25,25);
        GeoLocation geoLocation6 = new GeoLocation(30,30);
        GeoLocation geoLocation7 = new GeoLocation(35,35);
        GeoLocation geoLocation8 = new GeoLocation(40,40);
        GeoLocation geoLocation9 = new GeoLocation(45,45);
        GeoLocation geoLocation10 = new GeoLocation(50,50);
        
        c1.setCommentDate(new Date(currentDate.getTime() - 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() - 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() - 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() - 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() - 5*extraTime));
        c6.setCommentDate(new Date(currentDate.getTime() - 6*extraTime));
        c7.setCommentDate(new Date(currentDate.getTime() - 7*extraTime));
        c8.setCommentDate(new Date(currentDate.getTime() - 8*extraTime));
        c9.setCommentDate(new Date(currentDate.getTime() - 9*extraTime));
        c10.setCommentDate(new Date(currentDate.getTime() - 10*extraTime));
        
        c1.setLocation(geoLocation1);
        c2.setLocation(geoLocation2);
        c3.setLocation(geoLocation3);
        c4.setLocation(geoLocation4);
        c5.setLocation(geoLocation5);
        c6.setLocation(geoLocation6);
        c7.setLocation(geoLocation7);
        c8.setLocation(geoLocation8);
        c9.setLocation(geoLocation9);
        c10.setLocation(geoLocation10);
        
        
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        t6.setBodyComment(c6);
        t7.setBodyComment(c7);
        t8.setBodyComment(c8);
        t9.setBodyComment(c9);
        t10.setBodyComment(c10);
        
        carrier.add(t3);
        carrier.add(t2);
        carrier.add(t4);
        carrier.add(t1);
        carrier.add(t5);
        carrier.add(t7);
        carrier.add(t6);
        carrier.add(t10);
        carrier.add(t8);
        carrier.add(t9);
        
        for(ThreadComment thread: carrier){
            Log.e("Score of thread:", String.valueOf(thread.getScoreFromUser(new GeoLocation(0,0))));
        }
        
        SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_HIGHEST, 
                            carrier, new GeoLocation(0,0));
        
        Log.e(""," ");
        for(ThreadComment thread: carrier){
            Log.e("Score of thread:", String.valueOf(thread.getScoreFromUser(new GeoLocation(0,0))));
        }
        
        assertTrue("t1 at index 0", carrier.get(0) == t1);
        assertTrue("t2 at index 1", carrier.get(1) == t2);
        assertTrue("t3 at index 2", carrier.get(2) == t3);
        assertTrue("t4 at index 3", carrier.get(3) == t4);
        assertTrue("t5 at index 4", carrier.get(4) == t5);
        assertTrue("t6 at index 5", carrier.get(5) == t6);
        assertTrue("t7 at index 6", carrier.get(6) == t7);
        assertTrue("t8 at index 7", carrier.get(7) == t8);
        assertTrue("t9 at index 8", carrier.get(8) == t9);
        assertTrue("t10 at index 9", carrier.get(9) == t10);  
    }
    
    /**
     * Tests the sorting of threads by the score relative to the user.
     */
    public void testSortByUserScoreLowest(){
        ArrayList<ThreadComment> carrier = new ArrayList<ThreadComment>();
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        ThreadComment t4 = new ThreadComment();
        ThreadComment t5 = new ThreadComment();
        ThreadComment t6 = new ThreadComment();
        ThreadComment t7 = new ThreadComment();
        ThreadComment t8 = new ThreadComment();
        ThreadComment t9 = new ThreadComment();
        ThreadComment t10 = new ThreadComment();
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Comment c6 = new Comment();
        Comment c7 = new Comment();
        Comment c8 = new Comment();
        Comment c9 = new Comment();
        Comment c10 = new Comment();
        Date currentDate = new Date();
        GeoLocation geoLocation1 = new GeoLocation(5,5);
        GeoLocation geoLocation2 = new GeoLocation(10,10);
        GeoLocation geoLocation3 = new GeoLocation(15,15);
        GeoLocation geoLocation4 = new GeoLocation(20,20);
        GeoLocation geoLocation5 = new GeoLocation(25,25);
        GeoLocation geoLocation6 = new GeoLocation(30,30);
        GeoLocation geoLocation7 = new GeoLocation(35,35);
        GeoLocation geoLocation8 = new GeoLocation(40,40);
        GeoLocation geoLocation9 = new GeoLocation(45,45);
        GeoLocation geoLocation10 = new GeoLocation(50,50);
        
        c1.setCommentDate(new Date(currentDate.getTime() - 1*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() - 2*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() - 3*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() - 4*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() - 5*extraTime));
        c6.setCommentDate(new Date(currentDate.getTime() - 6*extraTime));
        c7.setCommentDate(new Date(currentDate.getTime() - 7*extraTime));
        c8.setCommentDate(new Date(currentDate.getTime() - 8*extraTime));
        c9.setCommentDate(new Date(currentDate.getTime() - 9*extraTime));
        c10.setCommentDate(new Date(currentDate.getTime() - 10*extraTime));
        
        c1.setLocation(geoLocation1);
        c2.setLocation(geoLocation2);
        c3.setLocation(geoLocation3);
        c4.setLocation(geoLocation4);
        c5.setLocation(geoLocation5);
        c6.setLocation(geoLocation6);
        c7.setLocation(geoLocation7);
        c8.setLocation(geoLocation8);
        c9.setLocation(geoLocation9);
        c10.setLocation(geoLocation10);
        
        
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        t6.setBodyComment(c6);
        t7.setBodyComment(c7);
        t8.setBodyComment(c8);
        t9.setBodyComment(c9);
        t10.setBodyComment(c10);
        
        carrier.add(t3);
        carrier.add(t2);
        carrier.add(t4);
        carrier.add(t1);
        carrier.add(t5);
        carrier.add(t7);
        carrier.add(t6);
        carrier.add(t10);
        carrier.add(t8);
        carrier.add(t9);
        
        SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_LOWEST, 
                            carrier, new GeoLocation(0,0));
        
        assertTrue("t10 at index 0", carrier.get(0) == t10);
        assertTrue("t9 at index 1", carrier.get(1) == t9);
        assertTrue("t8 at index 2", carrier.get(2) == t8);
        assertTrue("t7 at index 3", carrier.get(3) == t7);
        assertTrue("t6 at index 4", carrier.get(4) == t6);
        assertTrue("t5 at index 5", carrier.get(5) == t5);
        assertTrue("t4 at index 6", carrier.get(6) == t4);
        assertTrue("t3 at index 7", carrier.get(7) == t3);
        assertTrue("t2 at index 8", carrier.get(8) == t2);
        assertTrue("t1 at index 9", carrier.get(9) == t1);
    }
    
    /**
     * Tests the sorting of threads by some location.
     */
    public void testSortByLocation(){
        ArrayList<ThreadComment> carrier = new ArrayList<ThreadComment>();
        ThreadComment t1 = new ThreadComment();
        ThreadComment t2 = new ThreadComment();
        ThreadComment t3 = new ThreadComment();
        ThreadComment t4 = new ThreadComment();
        ThreadComment t5 = new ThreadComment();
        ThreadComment t6 = new ThreadComment();
        ThreadComment t7 = new ThreadComment();
        ThreadComment t8 = new ThreadComment();
        ThreadComment t9 = new ThreadComment();
        ThreadComment t10 = new ThreadComment();
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        Comment c6 = new Comment();
        Comment c7 = new Comment();
        Comment c8 = new Comment();
        Comment c9 = new Comment();
        Comment c10 = new Comment();
        GeoLocation geoLocation1 = new GeoLocation(5,5);
        GeoLocation geoLocation2 = new GeoLocation(10,10);
        GeoLocation geoLocation3 = new GeoLocation(15,15);
        GeoLocation geoLocation4 = new GeoLocation(20,20);
        GeoLocation geoLocation5 = new GeoLocation(25,25);
        GeoLocation geoLocation6 = new GeoLocation(30,30);
        GeoLocation geoLocation7 = new GeoLocation(35,35);
        GeoLocation geoLocation8 = new GeoLocation(40,40);
        GeoLocation geoLocation9 = new GeoLocation(45,45);
        GeoLocation geoLocation10 = new GeoLocation(50,50);
        
        c1.setLocation(geoLocation1);
        c2.setLocation(geoLocation2);
        c3.setLocation(geoLocation3);
        c4.setLocation(geoLocation4);
        c5.setLocation(geoLocation5);
        c6.setLocation(geoLocation6);
        c7.setLocation(geoLocation7);
        c8.setLocation(geoLocation8);
        c9.setLocation(geoLocation9);
        c10.setLocation(geoLocation10);
           
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        t6.setBodyComment(c6);
        t7.setBodyComment(c7);
        t8.setBodyComment(c8);
        t9.setBodyComment(c9);
        t10.setBodyComment(c10);
        
        carrier.add(t3);
        carrier.add(t2);
        carrier.add(t4);
        carrier.add(t1);
        carrier.add(t5);
        carrier.add(t7);
        carrier.add(t6);
        carrier.add(t10);
        carrier.add(t8);
        carrier.add(t9);
        
        SortUtil.sortThreads(SortUtil.SORT_LOCATION, 
                            carrier, new GeoLocation(0,0));
        
        assertTrue("t1 at index 0", carrier.get(0) == t1);
        assertTrue("t2 at index 1", carrier.get(1) == t2);
        assertTrue("t3 at index 2", carrier.get(2) == t3);
        assertTrue("t4 at index 3", carrier.get(3) == t4);
        assertTrue("t5 at index 4", carrier.get(4) == t5);
        assertTrue("t6 at index 5", carrier.get(5) == t6);
        assertTrue("t7 at index 6", carrier.get(6) == t7);
        assertTrue("t8 at index 7", carrier.get(7) == t8);
        assertTrue("t9 at index 8", carrier.get(8) == t9);
        assertTrue("t10 at index 9", carrier.get(9) == t10); 
    }
}
