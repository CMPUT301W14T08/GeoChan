package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.Date;

import android.graphics.Picture;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

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
        
        t1.getBodyComment().getLocation().setCoordinates(0,0);
        t2.getBodyComment().getLocation().setCoordinates(5, 5);
        
        t1.setSortLoc(new GeoLocation(0,0));
        t2.setSortLoc(new GeoLocation(0,0));
        
        assertTrue("Scores calculated relatively correctly.",
                    t1.getScoreFromUser(t1.getSortLoc()) >
                    t2.getScoreFromUser(t2.getSortLoc()));
        
    }
    
    public void testSortByDateNewest(){
        /*
         * Tests the implementation of Thread.sortComments("DATE_NEWEST");
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
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        ThreadComment thread = new ThreadComment(c1, "This thread is for testing!");
        thread.addComment(c4);
        thread.addComment(c3);
        thread.addComment(c5);
        thread.addComment(c2);
        thread.sortComments(SortTypes.SORT_DATE_NEWEST);

        assertTrue("c5 is at index 0", (thread.getBodyComment().getChildAtIndex(0)) == c5);
        assertTrue("c4 is at index 1", (thread.getBodyComment().getChildAtIndex(1)) == c4);
        assertTrue("c3 is at index 2", (thread.getBodyComment().getChildAtIndex(2)) == c3);
        assertTrue("c2 is at index 3", (thread.getBodyComment().getChildAtIndex(3)) == c2);
    }
    
    public void testSortByDateOldest(){
        /*
         * Tests the implementation of Thread.sortComments("DATE_OLDEST");
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
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        ThreadComment thread = new ThreadComment(c1, "This thread is for testing!");
        thread.addComment(c4);
        thread.addComment(c3);
        thread.addComment(c5);
        thread.addComment(c2);
        thread.sortComments(SortTypes.SORT_DATE_OLDEST);

        assertTrue("c2 is at index 0", (thread.getBodyComment().getChildAtIndex(0)) == c2);
        assertTrue("c3 is at index 1", (thread.getBodyComment().getChildAtIndex(1)) == c3);
        assertTrue("c4 is at index 2", (thread.getBodyComment().getChildAtIndex(2)) == c4);
        assertTrue("c5 is at index 3", (thread.getBodyComment().getChildAtIndex(3)) == c5);
    }

    /**
     * Tests the sorting of comments in a thread by the score relative to the user.
     */
    public void testSortByUserScoreHighest(){
        long extraTime = 1320000;
        ThreadComment t = new ThreadComment();
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        
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
        t.setSortLoc(geoLocationT);
        
        c1.getLocation().setLocation(location1);
        c2.getLocation().setLocation(location2);
        c3.getLocation().setLocation(location3);
        c4.getLocation().setLocation(location4);
        c5.getLocation().setLocation(location5);
        t.getSortLoc().setLocation(locationT);
        
        c1.getLocation().setCoordinates(0,0);
        c2.getLocation().setCoordinates(5,5);
        c3.getLocation().setCoordinates(10,10);
        c4.getLocation().setCoordinates(15,15);
        c5.getLocation().setCoordinates(20,20);
        
        Date currentDate = new Date();
        
        c1.setCommentDate(currentDate);
        c2.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.addComment(c3);
        t.addComment(c2);
        t.addComment(c4);
        t.addComment(c5);
        t.addComment(c1);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_HIGHEST);
        
        assertTrue("c1 is at location 0", t.getBodyComment().getChildAtIndex(0) == c1);
        assertTrue("c2 is at location 1", t.getBodyComment().getChildAtIndex(1) == c2);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c4 is at location 3", t.getBodyComment().getChildAtIndex(3) == c4);
        assertTrue("c5 is at location 4", t.getBodyComment().getChildAtIndex(4) == c5);
        
        c5.setCommentDate(currentDate);
        c4.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c1.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_HIGHEST);
        
        assertTrue("c5 is at location 0", t.getBodyComment().getChildAtIndex(0) == c5);
        assertTrue("c4 is at location 1", t.getBodyComment().getChildAtIndex(1) == c4);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c2 is at location 3", t.getBodyComment().getChildAtIndex(3) == c2);
        assertTrue("c1 is at location 4", t.getBodyComment().getChildAtIndex(4) == c1);
    }
    
    /**
     * Tests the sorting of comments in a thread by the score relative to the user.
     */
    public void testSortByUserScoreLowest(){
        long extraTime = 1320000;
        ThreadComment t = new ThreadComment();
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
        t.setSortLoc(geoLocationT);
        
        c1.getLocation().setLocation(location1);
        c2.getLocation().setLocation(location2);
        c3.getLocation().setLocation(location3);
        c4.getLocation().setLocation(location4);
        c5.getLocation().setLocation(location5);
        t.getSortLoc().setLocation(locationT);
        
        c1.getLocation().setCoordinates(0,0);
        c2.getLocation().setCoordinates(5,5);
        c3.getLocation().setCoordinates(10,10);
        c4.getLocation().setCoordinates(15,15);
        c5.getLocation().setCoordinates(20,20);
        
        Date currentDate = new Date();
        
        c1.setCommentDate(currentDate);
        c2.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.addComment(c3);
        t.addComment(c2);
        t.addComment(c4);
        t.addComment(c5);
        t.addComment(c1);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_LOWEST);
        
        assertTrue("c5 is at location 0", t.getBodyComment().getChildAtIndex(0) == c5);
        assertTrue("c4 is at location 1", t.getBodyComment().getChildAtIndex(1) == c4);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c2 is at location 3", t.getBodyComment().getChildAtIndex(3) == c2);
        assertTrue("c1 is at location 4", t.getBodyComment().getChildAtIndex(4) == c1);
        
        c5.setCommentDate(currentDate);
        c4.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c1.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_LOWEST);
        
        assertTrue("c1 is at location 0", t.getBodyComment().getChildAtIndex(0) == c1);
        assertTrue("c2 is at location 1", t.getBodyComment().getChildAtIndex(1) == c2);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c4 is at location 3", t.getBodyComment().getChildAtIndex(3) == c4);
        assertTrue("c5 is at location 4", t.getBodyComment().getChildAtIndex(4) == c5);
    }
    
    /**
     * Tests the sorting of comments in a thread by the thread's sortLoc.
     */
    public void testSortByLocation(){
        ThreadComment t = new ThreadComment();
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        
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
        t.setSortLoc(geoLocationT);
        
        c1.getLocation().setLocation(location1);
        c2.getLocation().setLocation(location2);
        c3.getLocation().setLocation(location3);
        c4.getLocation().setLocation(location4);
        c5.getLocation().setLocation(location5);
        t.getSortLoc().setLocation(locationT);
        
        t.getSortLoc().setCoordinates(0,0);
        c1.getLocation().setCoordinates(0,0);
        c2.getLocation().setCoordinates(5,5);
        c3.getLocation().setCoordinates(10,10);
        c4.getLocation().setCoordinates(15,15);
        c5.getLocation().setCoordinates(20,20);
        
        t.addComment(c3);
        t.addComment(c2);
        t.addComment(c4);
        t.addComment(c5);
        t.addComment(c1);
        
        
        t.sortComments(SortTypes.SORT_LOCATION_MISC);
        
        Log.i("Comment at index 0:", t.getBodyComment().getChildAtIndex(0).getTextPost());
        Log.i("Comment at index 1:", t.getBodyComment().getChildAtIndex(1).getTextPost());
        Log.i("Comment at index 2:", t.getBodyComment().getChildAtIndex(2).getTextPost());
        Log.i("Comment at index 3:", t.getBodyComment().getChildAtIndex(3).getTextPost());
        Log.i("Comment at index 4:", t.getBodyComment().getChildAtIndex(4).getTextPost());
        
        Log.i("Lat of c1:", String.valueOf(c1.getLocation().getLatitude()));
        Log.i("Lat of c2:", String.valueOf(c2.getLocation().getLatitude()));
        Log.i("Lat of c3:", String.valueOf(c3.getLocation().getLatitude()));
        Log.i("Lat of c4:", String.valueOf(c4.getLocation().getLatitude()));
        Log.i("Lat of c5:", String.valueOf(c5.getLocation().getLatitude()));
        
        assertTrue("c1 is at location 0", t.getBodyComment().getChildAtIndex(0) == c1);
        assertTrue("c2 is at location 1", t.getBodyComment().getChildAtIndex(1) == c2);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c4 is at location 3", t.getBodyComment().getChildAtIndex(3) == c4);
        assertTrue("c5 is at location 4", t.getBodyComment().getChildAtIndex(4) == c5);
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_LOCATION_MISC);
        
        assertTrue("c5 is at location 0", t.getBodyComment().getChildAtIndex(0) == c5);
        assertTrue("c4 is at location 1", t.getBodyComment().getChildAtIndex(1) == c4);
        assertTrue("c3 is at location 2", t.getBodyComment().getChildAtIndex(2) == c3);
        assertTrue("c2 is at location 3", t.getBodyComment().getChildAtIndex(3) == c2);
        assertTrue("c1 is at location 4", t.getBodyComment().getChildAtIndex(4) == c1);
    }
    
    public void testSortByImage(){
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Comment c4 = new Comment();
        Comment c5 = new Comment();
        ThreadComment t1 = new ThreadComment();
        
        c1.setImage(new Picture());
        c3.setImage(new Picture());
        c4.setImage(new Picture());
        
        t1.addComment(c2);
        t1.addComment(c1);
        
        t1.sortComments(SortTypes.SORT_IMAGE);
        
        assertTrue("c1 was pushed up", t1.getBodyComment().getChildAtIndex(0) == c1);
        assertTrue("c2 was pushed down",
                    t1.getBodyComment().getChildAtIndex(1) == c2);
        
        c2.setCommentDate(new Date(c1.getCommentDate().getTime() - 80000));
        c3.setCommentDate(new Date(c1.getCommentDate().getTime() - 20000));
        c4.setCommentDate(new Date(c1.getCommentDate().getTime() - 50000));
        
        t1.addComment(c3);
        t1.addComment(c4);
        t1.addComment(c5);
        
        t1.sortComments(SortTypes.SORT_IMAGE);
        
        c1.setTextPost("c1");
        c2.setTextPost("c2");
        c3.setTextPost("c3");
        c4.setTextPost("c4");
        c5.setTextPost("c5");
        
        Log.e("Pos 0:", t1.getBodyComment().getChildAtIndex(0).getTextPost());
        Log.e("Pos 1:", t1.getBodyComment().getChildAtIndex(1).getTextPost());
        Log.e("Pos 2:", t1.getBodyComment().getChildAtIndex(2).getTextPost());
        Log.e("Pos 3:", t1.getBodyComment().getChildAtIndex(3).getTextPost());
        Log.e("Pos 4:", t1.getBodyComment().getChildAtIndex(4).getTextPost());
        assertTrue("Sort was done correctly.",
                    t1.getBodyComment().getChildAtIndex(0) == c4);
        assertTrue("Sort was done correctly.",
                    t1.getBodyComment().getChildAtIndex(1) == c3);
        assertTrue("Sort was done correctly.",
                    t1.getBodyComment().getChildAtIndex(2) == c1);
        assertTrue("Sort was done correctly.",
                    t1.getBodyComment().getChildAtIndex(3) == c2);
        assertTrue("Sort was done correctly.",
                    t1.getBodyComment().getChildAtIndex(4) == c5);
        
        
        
    }
}
