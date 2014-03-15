package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.Date;

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

        assertTrue("c5 is at index 0", (thread.getComments().get(0)) == c5);
        assertTrue("c4 is at index 1", (thread.getComments().get(1)) == c4);
        assertTrue("c3 is at index 2", (thread.getComments().get(2)) == c3);
        assertTrue("c2 is at index 3", (thread.getComments().get(3)) == c2);
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

        assertTrue("c2 is at index 0", (thread.getComments().get(0)) == c2);
        assertTrue("c3 is at index 1", (thread.getComments().get(1)) == c3);
        assertTrue("c4 is at index 2", (thread.getComments().get(2)) == c4);
        assertTrue("c5 is at index 3", (thread.getComments().get(3)) == c5);
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
        
        assertTrue("c1 is at location 0", t.getComments().get(0) == c1);
        assertTrue("c2 is at location 1", t.getComments().get(1) == c2);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c4 is at location 3", t.getComments().get(3) == c4);
        assertTrue("c5 is at location 4", t.getComments().get(4) == c5);
        
        c5.setCommentDate(currentDate);
        c4.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c1.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_HIGHEST);
        
        assertTrue("c5 is at location 0", t.getComments().get(0) == c5);
        assertTrue("c4 is at location 1", t.getComments().get(1) == c4);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c2 is at location 3", t.getComments().get(3) == c2);
        assertTrue("c1 is at location 4", t.getComments().get(4) == c1);
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
        
        assertTrue("c5 is at location 0", t.getComments().get(0) == c5);
        assertTrue("c4 is at location 1", t.getComments().get(1) == c4);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c2 is at location 3", t.getComments().get(3) == c2);
        assertTrue("c1 is at location 4", t.getComments().get(4) == c1);
        
        c5.setCommentDate(currentDate);
        c4.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c2.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c1.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_USER_SCORE_LOWEST);
        
        assertTrue("c1 is at location 0", t.getComments().get(0) == c1);
        assertTrue("c2 is at location 1", t.getComments().get(1) == c2);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c4 is at location 3", t.getComments().get(3) == c4);
        assertTrue("c5 is at location 4", t.getComments().get(4) == c5);
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
        
        Log.i("Comment at index 0:", t.getComments().get(0).getTextPost());
        Log.i("Comment at index 1:", t.getComments().get(1).getTextPost());
        Log.i("Comment at index 2:", t.getComments().get(2).getTextPost());
        Log.i("Comment at index 3:", t.getComments().get(3).getTextPost());
        Log.i("Comment at index 4:", t.getComments().get(4).getTextPost());
        
        Log.i("Lat of c1:", String.valueOf(c1.getLocation().getLatitude()));
        Log.i("Lat of c2:", String.valueOf(c2.getLocation().getLatitude()));
        Log.i("Lat of c3:", String.valueOf(c3.getLocation().getLatitude()));
        Log.i("Lat of c4:", String.valueOf(c4.getLocation().getLatitude()));
        Log.i("Lat of c5:", String.valueOf(c5.getLocation().getLatitude()));
        
        assertTrue("c1 is at location 0", t.getComments().get(0) == c1);
        assertTrue("c2 is at location 1", t.getComments().get(1) == c2);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c4 is at location 3", t.getComments().get(3) == c4);
        assertTrue("c5 is at location 4", t.getComments().get(4) == c5);
        
        t.getSortLoc().setCoordinates(20,20);
        
        t.sortComments(SortTypes.SORT_LOCATION_MISC);
        
        assertTrue("c5 is at location 0", t.getComments().get(0) == c5);
        assertTrue("c4 is at location 1", t.getComments().get(1) == c4);
        assertTrue("c3 is at location 2", t.getComments().get(2) == c3);
        assertTrue("c2 is at location 3", t.getComments().get(3) == c2);
        assertTrue("c1 is at location 4", t.getComments().get(4) == c1);
    }
}
