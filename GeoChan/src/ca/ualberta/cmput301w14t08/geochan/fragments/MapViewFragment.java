package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * COMMENT GOES HERE
 * @author 
 *
 */
public class MapViewFragment extends Fragment {

    private MapView openMapView;
    private LocationListenerService locationListenerService;
    private GeoLocation currentLocation;
    private GeoPoint startGeoPoint;
    private Polyline roadOverlay;
    private Comment topComment;

    private double MAX_LAT;
    private double MAX_LONG;
    private double MIN_LAT;
    private double MIN_LONG;

    final public static int ZOOM_FACTOR = 2000000;
    
    /**
     * Async task class. This task is designed to retrieve directions from the
     * users current location to the location of the original post of the thread.
     * It displays a ProgressDialog while the location is being retrieved. 
     * 
     * @author bradsimons
     */
    class MapAsyncTask extends AsyncTask<Void,Void,Void> {

        ProgressDialog directionsLoadingDialog = new ProgressDialog(getActivity());

        /**
         * Displays a ProgessDialog while the task is executing
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            directionsLoadingDialog.setMessage("Getting Directions");
            directionsLoadingDialog.show();
        }

        /**
         * Calculating the directions from the current to the location 
         * of the topComment. 
         */
        @Override
        protected Void doInBackground(Void ... params) {
            RoadManager roadManager = new OSRMRoadManager();
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

            waypoints.add(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
            waypoints.add(startGeoPoint);
            Road road = roadManager.getRoad(waypoints);

            roadOverlay = RoadManager.buildRoadOverlay(road, getActivity());
            openMapView.getOverlays().add(roadOverlay);

            return null;
        }

        /**
         * Task is now finished, dismiss the ProgressDialog
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            directionsLoadingDialog.dismiss();
        }
    }  

    /**
     * Gets the view when inflated, then calls setZoomLevel to display the correct map area.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        setZoomLevel();
        return view;

    }

    /**
     * inflates the menu and adds and add items to action bar if present
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Initiates a location listener which immediately starts listening for 
     * location updates. Gets the current location as well. Then unpacks the 
     * bundle passed to the fragment. It then gets the map setup and prepares
     * the min and max latitude and longitude required to display the map 
     * properly for calculation
     */
    @Override
    public void onStart() {
        super.onStart();

        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        currentLocation = new GeoLocation(locationListenerService);

        Bundle args = getArguments();
        topComment = (Comment) args.getParcelable("thread_comment");

        // To calculate the max and min latitude and longitude of all
        // the comments, we set the min's to max's and vice versa
        // then have values of each comment modify these
        MAX_LAT = -90;
        MIN_LAT = 90;
        MAX_LONG = -180;
        MIN_LONG = 180;

        GeoLocation geoLocation = topComment.getLocation();
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Thread has no location");
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();
        } else {
            this.setupMap(topComment);
        }
    }

    
    /**
     * Calls onStop in the superclass, and tells the locationListener
     * to stop listening. 
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * This sets up the comment location the map. The map is centered at the location
     * of the comment GeoLocation, and places a pin at this point. It then calls 
     * handleChildComments to place pins for each child comment in the thread. 
     * @param comment
     */
    public void setupMap(Comment comment) {
        openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        openMapView.getController().setZoom(18);

        GeoLocation geoLocation = comment.getLocation();
        startGeoPoint = new GeoPoint(geoLocation.getLocation());
        setGeoPointMarker(startGeoPoint);
        handleChildComments(comment);
    }

    /**
     * Sets the default zoom level for the mapview. This takes the max and min of 
     * both lat and long, and zooms to span the area required. It also animates to the
     * startGeoPoint, which is the location of the topComment. The values must be 
     * padded with a zoom_factor, which is a static class variable
     */
    public void setZoomLevel() {
        int maxLatitude = (int) Math.round(MAX_LAT);
        int maxLongitude = (int) Math.round(MAX_LONG);
        int minLatitude = (int) Math.round(MIN_LAT);
        int minLongitude = (int) Math.round(MIN_LONG);

        // get the mapController and set the zoom and location
        IMapController mapController = openMapView.getController();
        mapController.zoomToSpan(Math.round(maxLongitude - minLongitude) * ZOOM_FACTOR,
                Math.round(maxLatitude - minLatitude) * ZOOM_FACTOR);
        mapController.animateTo(startGeoPoint);
    }

    /**
     * Recursive method for handling all comments in the thread. First checks if the 
     * comment has any children or not. If none, simply return. Otherwise, call 
     * setGeoPointMarker for each child of the comment. Call checkCommmentLocation
     * to calculate the min and max of the lat and long for the entire thread. 
     * Then finally make a recursive call to check if a child comment has
     * any children.
     * 
     * @param comment
     */
    private void handleChildComments(Comment comment) {
        ArrayList<Comment> children = comment.getChildren();
        if (children.size() == 0) {
            return;
        } else {
            for (Comment c : children) {
                GeoLocation commentLocation = c.getLocation();
                setGeoPointMarker(new GeoPoint(commentLocation.getLatitude(), 
                        commentLocation.getLongitude()));
                checkCommentLocationDistance(c);
                handleChildComments(c);
            }
        }
    }

    /**
     * Gets the location of the comment passed in, compares it to the 
     * minimum latitude and the maximum latitude. This method is called on each
     * comment in the thread. The end result is that the four min/max lat long vars
     * will contain their appropriate value for the entire thread. This used
     * to zoom to include all location pins in the view
     * 
     * @param comment
     */
    public void checkCommentLocationDistance(Comment comment) {
        double commentLat = comment.getLocation().getLatitude();
        double commentLong = comment.getLocation().getLongitude();

        if (commentLat > MAX_LAT) {
            MAX_LAT = commentLat;
        }
        if (commentLat < MIN_LAT) {
            MIN_LAT = commentLat;
        }
        if (commentLong > MAX_LONG) {
            MAX_LONG = commentLong;
        }
        if (commentLong < MIN_LONG) {
            MIN_LONG = commentLong;
        }
    }

    /**
     * Creates a marker obejct, sets its position to the GeoPoint location
     * passed, and then adds the marker to the map overlays.
     * 
     * @param geoPoint
     */
    public void setGeoPointMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(openMapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        openMapView.getOverlays().add(marker);
    }


    /**
     * Called when the get_directions_button is clicked. Displays directions from the users
     * current location to the comment location. Uses an Async task to get map overlay.
     * If the users current location cannot be obtained, an error is shown to the screen
     * and the async task is not called
     */
    public void getDirections() {   
        if (currentLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Could not retrieve your location");
        } else {
            new MapAsyncTask().execute();
        }
        openMapView.invalidate();
    }
}