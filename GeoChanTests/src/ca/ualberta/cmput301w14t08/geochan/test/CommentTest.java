package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.ArrayList;
import java.util.Date;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/*
 * Test the main functionality of the Comment class
 */
public class CommentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    private MainActivity activity;
    private LocationListenerService locationListenerService;
    
    public CommentTest(){
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
    
    // Test presence of an image in a comment object by creating an object
    // with an image and calling hasImage()
    public void testHasImage() {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(256, 256, conf);
        Comment comment = new Comment("test", bitmap, null, null);
        assertTrue("Comment has image", comment.hasImage());
    }
    
    // Test adding a child, i.e. a reply comment
    @SuppressWarnings("unused")
    public void testAddChild() {
        Comment parent = new Comment("test", null, null);
        Comment reply = new Comment("test_reply", null, parent);
        assertNotNull("comment has a reply", parent.getChildren());
    }
    
    // Test that the constructor functions properly
    public void testConstruct() {
        Comment comment = new Comment("Hola", null, null);
        assertNull(comment.getParent());
    }
    
    /**
     * Tests sorting comments according to date (newest).
     */
    public void testSortByDateNewest(){
        long extraTime = 1320000;
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_DATE_NEWEST, carrier);
        
        assertTrue("c5 at index 0", carrier.get(0) == c5);
        assertTrue("c4 at index 1", carrier.get(1) == c4);
        assertTrue("c3 at index 2", carrier.get(2) == c3);
        assertTrue("c2 at index 3", carrier.get(3) == c2);
        assertTrue("c1 at index 4", carrier.get(4) == c1);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c10);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c9);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c8);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c6);
    }
    
    /**
     * Tests sorting comments according to date (oldest).
     */
    public void testSortByDateOldest(){
        long extraTime = 1320000;
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_DATE_OLDEST, carrier);
        
        assertTrue("c1 at index 0", carrier.get(0) == c1);
        assertTrue("c2 at index 1", carrier.get(1) == c2);
        assertTrue("c3 at index 2", carrier.get(2) == c3);
        assertTrue("c4 at index 3", carrier.get(3) == c4);
        assertTrue("c5 at index 4", carrier.get(4) == c5);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c9);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c10);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c6);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c8);
    }
    
    /**
     * Tests sorting comments according to location.
     */
    public void testSortByLocation(){
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_LOCATION,carrier);
        
        assertTrue("c1 at index 0", carrier.get(0) == c1);
        assertTrue("c2 at index 1", carrier.get(1) == c2);
        assertTrue("c3 at index 2", carrier.get(2) == c3);
        assertTrue("c4 at index 3", carrier.get(3) == c4);
        assertTrue("c5 at index 4", carrier.get(4) == c5);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c9);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c10);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c6);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c8);     
    }
    
    /**
     * Test sorting by highest score. 
     * Score is a combination of location and time since posting.
     */
    public void testSortByScoreHighest(){
        long extraTime = 1320000;
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_USER_SCORE_HIGHEST,
                                carrier);
        
        assertTrue("c1 at index 0", carrier.get(0) == c1);
        assertTrue("c2 at index 1", carrier.get(1) == c2);
        assertTrue("c3 at index 2", carrier.get(2) == c3);
        assertTrue("c4 at index 3", carrier.get(3) == c4);
        assertTrue("c5 at index 4", carrier.get(4) == c5);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c9);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c10);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c6);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c8);  
    }
    
    /**
     * Tests sorting comments according to score (lowest).
     */
    public void testSortByScoreLowest(){
        long extraTime = 1320000;
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_USER_SCORE_LOWEST,
                                carrier);
        
        assertTrue("c5 at index 0", carrier.get(0) == c5);
        assertTrue("c4 at index 1", carrier.get(1) == c4);
        assertTrue("c3 at index 2", carrier.get(2) == c3);
        assertTrue("c2 at index 3", carrier.get(3) == c2);
        assertTrue("c1 at index 4", carrier.get(4) == c1);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c10);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c9);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c8);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c6);
    }
    
    /**
     * Tests sorting comments according to image.
     */
    public void testSortByImage(){
        long extraTime = 1320000;
        ArrayList<Comment> carrier = new ArrayList<Comment>();
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
        
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bitmap0 = Bitmap.createBitmap(256, 256, conf);
        Bitmap bitmap1 = Bitmap.createBitmap(256, 256, conf);
        Bitmap bitmap2 = Bitmap.createBitmap(256, 256, conf);
        Bitmap bitmap3 = Bitmap.createBitmap(256, 256, conf);
        
        c10.setImage(bitmap0);
        c8.setImage(bitmap1);
        c5.setImage(bitmap2);
        c3.setImage(bitmap3);
        
        carrier.add(c1);
        carrier.add(c2);
        carrier.add(c3);
        carrier.add(c4);
        carrier.add(c5);
        
        c2.addChild(c10);
        c2.addChild(c9);
        
        c3.addChild(c8);
        c3.addChild(c6);
        c3.addChild(c7);
        
        SortUtil.sortComments(SortUtil.SORT_IMAGE, carrier);
        
        assertTrue("c5 at index 0", carrier.get(0) == c5);
        assertTrue("c3 at index 1", carrier.get(1) == c3);
        assertTrue("c4 at index 2", carrier.get(2) == c4);
        assertTrue("c2 at index 3", carrier.get(3) == c2);
        assertTrue("c1 at index 4", carrier.get(4) == c1);
        
        assertTrue("c2 children sorted", c2.getChildAtIndex(0) == c10);
        assertTrue("c2 children sorted", c2.getChildAtIndex(1) == c9);
        
        assertTrue("c3 children sorted", c3.getChildAtIndex(0) == c8);
        assertTrue("c3 children sorted", c3.getChildAtIndex(1) == c7);
        assertTrue("c3 children sorted", c3.getChildAtIndex(2) == c6);
    }
    
    /**
     * Tests the calculation of comment scores in relation to their parent.
     */
    public void testGetParentScore(){
        /*
         * Test the score calculation for child comments.
         */
        long extraTime = 1320000;
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
        
        locationListenerService = new LocationListenerService(activity);
        
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation4 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation5 = new GeoLocation(locationListenerService);
        
        c1.setLocation(geoLocation1);
        c2.setLocation(geoLocation2);
        c3.setLocation(geoLocation3);
        c4.setLocation(geoLocation4);
        c5.setLocation(geoLocation5);
        
        c1.getLocation().setLocation(location1);
        c2.getLocation().setLocation(location2);
        c3.getLocation().setLocation(location3);
        c4.getLocation().setLocation(location4);
        c5.getLocation().setLocation(location5);
        

        c1.getLocation().setCoordinates(53.526802,-113.527170);
        c2.getLocation().setCoordinates(53.523636,-113.527437);
        c3.getLocation().setCoordinates(53.527047,-113.525662);

        c2.setParent(c1);
        c3.setParent(c1);
        c4.setParent(c1);
        c5.setParent(c1);
        

        c1.addChild(c5);
        c1.addChild(c3);
        c1.addChild(c4);
        c1.addChild(c2);
        
        Date currentDate = new Date();
        
        c1.setCommentDate(currentDate);
        c2.setCommentDate(new Date(currentDate.getTime() + 20*extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 30*extraTime));
        c4.setCommentDate(new Date(currentDate.getTime() + 40*extraTime));
        c5.setCommentDate(new Date(currentDate.getTime() + 50*extraTime));

        assertTrue("c5 is > 0", c5.getScoreFromParent() > 0);
        assertTrue("c4 is > 0", c4.getScoreFromParent() > 0);
        assertTrue("c3 is > 0", c3.getScoreFromParent() > 0);
        assertTrue("c2 is > 0", c2.getScoreFromParent() > 0);
    }
    
    /**
     * Tests the calculation of the score of one comment relative to another
     */
    public void testGetUserScore(){
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        GeoLocation g1 = new GeoLocation(0,0);
        
        c1.getLocation().setCoordinates(0, 0);
        c2.getLocation().setCoordinates(5, 5);
        
        assertTrue("Scores are correct relatively.", 
                    c1.getScoreFromUser(g1) > c2.getScoreFromUser(g1));
    }
    
    /**
     * Test correctness of calculating distance between two points.
     */
    public void testGetDistanceFrom(){
        Comment c1 = new Comment();
        GeoLocation g1 = new GeoLocation(5,5);
        
        c1.getLocation().setCoordinates(0,0);
        
        double dist = c1.getDistanceFrom(g1);
        
        Log.e("Value of dist:", String.valueOf(dist));
        assertTrue("Distance calculated correctly.", dist == Math.sqrt(50));
    }

    
    /**
     * Test correct calculation of time differences between comment postings.
     */
    public void testGetTimeFrom(){
        Comment c1 = new Comment();
        Date d1 = new Date();
        
        assertEquals("Returns minimum 0.5:", c1.getTimeFrom(d1), 0.5);
        
        d1 = new Date(c1.getCommentDate().getTime() + 3600000);
        
        assertEquals("Returns correct hour amount.", c1.getTimeFrom(d1), 1.0);
    }
}
