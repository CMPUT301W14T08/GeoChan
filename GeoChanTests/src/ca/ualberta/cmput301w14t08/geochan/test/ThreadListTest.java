package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import junit.framework.TestCase;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortComparators;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.Thread;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class ThreadListTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    private MainActivity activity;
    private Location location;
    private LocationListenerService locationListenerService;
    
    public ThreadListTest(){
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        //Shamelessly ripped from GeolocationTest to test Threads with Geolocations.
        super.setUp();
        this.activity = getActivity();
        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();
    }

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
        ThreadList.clearThreads();
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
        
        tm.sortThreads(SortComparators.SORT_DATE_NEWEST);
        
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
        ThreadList.clearThreads();
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
        
        tm.sortThreads(SortComparators.SORT_DATE_OLDEST);
        
        assertTrue("t1 is at index 0", tm.getThreads().get(0) == t1);
        assertTrue("t2 is at index 1", tm.getThreads().get(1) == t2);
        assertTrue("t3 is at index 2", tm.getThreads().get(2) == t3);
        assertTrue("t4 is at index 3", tm.getThreads().get(3) == t4);
        assertTrue("t5 is at index 4", tm.getThreads().get(4) == t5);
    }
    
    /**
     * Tests the sorting of comments in a thread by the score relative to the user.
     */
    @SuppressWarnings("static-access")
    public void testSortByUserScoreHighest(){
        ThreadList.clearThreads();
        long extraTime = 1320000;
        ThreadList T = new ThreadList();
        
        Thread t1 = new Thread();
        Thread t2 = new Thread();
        Thread t3 = new Thread();
        Thread t4 = new Thread();
        Thread t5 = new Thread();
        
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);
        Location location3 = new Location(LocationManager.GPS_PROVIDER);
        Location location4 = new Location(LocationManager.GPS_PROVIDER);
        Location location5 = new Location(LocationManager.GPS_PROVIDER);
        Location locationT = new Location(LocationManager.GPS_PROVIDER);
        
        locationListenerService = new LocationListenerService(activity);
        
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation4 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation5 = new GeoLocation(locationListenerService);
        GeoLocation geoLocationT = new GeoLocation(locationListenerService);
        
        c1.setLocation(geoLocation1);
        c2.setLocation(geoLocation2);
        c3.setLocation(geoLocation3);
        c4.setLocation(geoLocation4);
        c5.setLocation(geoLocation5);
        T.setSortLoc(geoLocationT);
        
        c1.getLocation().setLocation(location1);
        c2.getLocation().setLocation(location2);
        c3.getLocation().setLocation(location3);
        c4.getLocation().setLocation(location4);
        c5.getLocation().setLocation(location5);
        T.getSortLoc().setLocation(locationT);
        
        c1.getLocation().setLatitude(0);
        c1.getLocation().setLongitude(0);
        c2.getLocation().setLatitude(5);
        c2.getLocation().setLongitude(5);
        c3.getLocation().setLatitude(10);
        c3.getLocation().setLongitude(10);
        c4.getLocation().setLatitude(15);
        c4.getLocation().setLongitude(15);
        c5.getLocation().setLatitude(20);
        c5.getLocation().setLongitude(20);
        T.getSortLoc().setLatitude(0);
        T.getSortLoc().setLongitude(0);
        
        Date currentDate = new Date();
        
        c1.setCommentDate(currentDate);
        c2.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        
        t1.setBodyComment(c1);
        t2.setBodyComment(c2);
        t3.setBodyComment(c3);
        t4.setBodyComment(c4);
        t5.setBodyComment(c5);
        
        T.addThread(t3);
        T.addThread(t2);
        T.addThread(t1);
        T.addThread(t5);
        T.addThread(t4);
        
        assertTrue("c1 location is not null", c1.getLocation() != null);
        assertTrue("c2 location is not null", c2.getLocation() != null);
        assertTrue("c3 location is not null", c3.getLocation() != null);
        assertTrue("c4 location is not null", c4.getLocation() != null);
        assertTrue("c5 location is not null", c5.getLocation() != null);
        
        assertTrue("t1 location is not null", t1.getBodyComment().getLocation() != null);
        assertTrue("t2 location is not null", t2.getBodyComment().getLocation() != null);
        assertTrue("t3 location is not null", t3.getBodyComment().getLocation() != null);
        assertTrue("t4 location is not null", t4.getBodyComment().getLocation() != null);
        assertTrue("t5 location is not null", t5.getBodyComment().getLocation() != null);
        
        Log.i("test thread sort user score", "null checks passed.");
        

        T.sortThreads(SortComparators.SORT_USER_SCORE_HIGHEST);
        
        Log.i("comment at loc 0",T.getThreads().get(0).getBodyComment().getTextPost());
        
        Log.i("score of comment at index 0", String.valueOf(T.getThreads().get(0).getBodyComment().getScoreFromUser(geoLocationT)));
        Log.i("score of comment at index 1", String.valueOf(T.getThreads().get(1).getBodyComment().getScoreFromUser(geoLocationT)));
        Log.i("score of comment at index 2", String.valueOf(T.getThreads().get(2).getBodyComment().getScoreFromUser(geoLocationT)));
        Log.i("score of comment at index 3", String.valueOf(T.getThreads().get(3).getBodyComment().getScoreFromUser(geoLocationT)));
        Log.i("score of comment at index 4", String.valueOf(T.getThreads().get(4).getBodyComment().getScoreFromUser(geoLocationT)));
        
        Log.i("Distance for thread 1:", String.valueOf(t1.getDistanceFrom(T.getSortLoc())));
        Log.i("Distance for thread 2:", String.valueOf(t1.getDistanceFrom(T.getSortLoc())));
        Log.i("Distance for thread 3:", String.valueOf(t1.getDistanceFrom(T.getSortLoc())));
        Log.i("Distance for thread 4:", String.valueOf(t1.getDistanceFrom(T.getSortLoc())));
        Log.i("Distance for thread 5:", String.valueOf(t1.getDistanceFrom(T.getSortLoc())));
        
        Log.i("Time for thread 1:", String.valueOf(t1.getTimeFrom(currentDate)));
        Log.i("Time for thread 2:", String.valueOf(t2.getTimeFrom(currentDate)));
        Log.i("Time for thread 3:", String.valueOf(t3.getTimeFrom(currentDate)));
        Log.i("Time for thread 4:", String.valueOf(t4.getTimeFrom(currentDate)));
        Log.i("Time for thread 5:", String.valueOf(t5.getTimeFrom(currentDate)));
        
        assertTrue("t1 is at location 0", T.getThreads().get(0) == t1);
        assertTrue("t2 is at location 1", T.getThreads().get(1) == t2);
        assertTrue("t3 is at location 2", T.getThreads().get(2) == t3);
        assertTrue("t4 is at location 3", T.getThreads().get(3) == t4);
        assertTrue("t5 is at location 4", T.getThreads().get(4) == t5);
        
        c5.setCommentDate(currentDate);
        c4.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c1.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        T.getSortLoc().setLatitude(20);
        T.getSortLoc().setLongitude(20);
        
        T.sortThreads(SortComparators.SORT_USER_SCORE_HIGHEST);
        
        assertTrue("t5 is at location 0", T.getThreads().get(0) == t1);
        assertTrue("t4 is at location 1", T.getThreads().get(1) == t1);
        assertTrue("t3 is at location 2", T.getThreads().get(2) == t1);
        assertTrue("t2 is at location 3", T.getThreads().get(3) == t1);
        assertTrue("t1 is at location 4", T.getThreads().get(4) == t1);
    }
}
