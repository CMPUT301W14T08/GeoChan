package ca.ualberta.cmput301w14t08.geochan.test;

import java.util.Date;

import android.content.Context;
import android.graphics.Picture;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.Comment;
import ca.ualberta.cmput301w14t08.geochan.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;

public class CommentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    private MainActivity activity;
    private Location location;
    
    public CommentTest(){
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        //Shamelessly ripped from GeolocationTest to test Comments with Geolocations.
        super.setUp();
        this.activity = getActivity();

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                // Called when a new location is found by the network location provider.
                location = newLocation;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationManager.removeUpdates(locationListener);
    }

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
    
    public void testGetScore(){
        /*
         * Test the score calculation for child comments.
         */
        long extraTime = 1320000;
        Comment c1 = new Comment();
        Comment c2 = new Comment();
        Comment c3 = new Comment();
        Date currentDate = new Date();
        
        c1.setCommentDate(currentDate);
        c2.setCommentDate(new Date(currentDate.getTime() + 500 * extraTime));
        c3.setCommentDate(new Date(currentDate.getTime() + 500 * extraTime));
        
        c2.setParent(c1);
        c3.setParent(c1);
        
        c1.addChild(c2);
        c1.addChild(c3);
        
        c1.setLocation(new GeoLocation(activity));
        c2.setLocation(new GeoLocation(activity));
        c3.setLocation(new GeoLocation(activity));
        
        assertTrue("c1 location not null", c1.getLocation().getLocation() != null);
        
        c1.getLocation().setLatitude(53.526802);
        c1.getLocation().setLongitude(-113.527170);
        
        c2.getLocation().setLatitude(53.523636);
        c2.getLocation().setLongitude(-113.527437);
        
        c3.getLocation().setLatitude(53.527047);
        c3.getLocation().setLongitude(-113.525662);
        
        Log.e("c3 distance:", String.valueOf(c3.getDistanceFrom(c3.getParent())));
        Log.e("c2 distance:", String.valueOf(c2.getDistanceFrom(c2.getParent())));
        Log.e("c3 time:", String.valueOf(c3.getTimeFrom(c3.getParent())));
        Log.e("c2 time:", String.valueOf(c2.getTimeFrom(c2.getParent())));
        Log.e("c3 Score:", String.valueOf(c3.getScore()));
        Log.e("c2 Score:", String.valueOf(c2.getScore()));
        
        assertTrue("Comment Scores calculated correctly.", c3.getScore() > c2.getScore());
    }
    
    
}
