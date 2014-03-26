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
import android.util.Log;
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
 * 
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
    private ArrayList<GeoPoint> geoPoints;

    private int maxLat;
    private int maxLong;
    private int minLat;
    private int minLong;

    final public static double ZOOM_FACTOR = 1.2;

    /**
     * Async task class. This task is designed to retrieve directions from the
     * users current location to the location of the original post of the
     * thread. It displays a ProgressDialog while the location is being
     * retrieved.
     * 
     * @author bradsimons
     */
    class MapAsyncTask extends AsyncTask<Void, Void, Void> {

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
         * Calculating the directions from the current to the location of the
         * topComment.
         */
        @Override
        protected Void doInBackground(Void... params) {
            RoadManager roadManager = new OSRMRoadManager();
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

            waypoints.add(new GeoPoint(currentLocation.getLatitude(), currentLocation
                    .getLongitude()));
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
     * Gets the view when inflated, then calls setZoomLevel to display the
     * correct map area.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_map_view, container, false);
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
     * properly for calculation. Then finally sets the zoom level
     */
    @Override
    public void onStart() {
        super.onStart();

        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        currentLocation = new GeoLocation(locationListenerService);

        Bundle args = getArguments();
        topComment = (Comment) args.getParcelable("thread_comment");

        geoPoints = new ArrayList<GeoPoint>();

        // To calculate the max and min latitude and longitude of all
        // the comments, we set the min's to max integer values and vice versa
        // then have values of each comment modify these variables
        minLat = Integer.MAX_VALUE;
        maxLat = Integer.MIN_VALUE;
        minLong = Integer.MAX_VALUE;
        maxLong = Integer.MIN_VALUE;

        GeoLocation geoLocation = topComment.getLocation();
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Thread has no location");
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();
        } else {
            this.setupMap(topComment);
            this.setGeoPointMarkers();
            this.calculateZoomSpan();
            this.setZoomLevel();
        }
    }

    /**
     * Calls onStop in the superclass, and tells the locationListener to stop
     * listening.
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * This sets up the comment location the map. The map is centered at the
     * location of the comment GeoLocation, and places a pin at this point. It
     * then calls handleChildComments to place pins for each child comment in
     * the thread.
     * 
     * @param comment
     */
    public void setupMap(Comment comment) {
        openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        openMapView.getController().setZoom(18);

        if (commentLocationIsValid(comment)) {
            Log.e("TopComment Location", "is valid");
            GeoLocation geoLocation = comment.getLocation();
            startGeoPoint = new GeoPoint(geoLocation.getLatitude() * 1E6, 
                    geoLocation.getLongitude() * 1E6);
            geoPoints.add(startGeoPoint);
            handleChildComments(comment);
        }
    }

    /**
     * Sets the default zoom level for the mapview. This takes the max and min
     * of both lat and long, and zooms to span the area required. It also
     * animates to the startGeoPoint, which is the location of the topComment.
     * The values must be padded with a zoom_factor, which is a static class
     * variable
     */
    public void setZoomLevel() {
        Log.e("maxLong", Integer.toString(maxLat));
        Log.e("minLong", Integer.toString(minLat));
        Log.e("maxLat", Integer.toString(maxLat));
        Log.e("minLat", Integer.toString(minLat));

        Log.e("longSpan", Integer.toString(maxLat - minLat));
        Log.e("latSpan", Integer.toString(maxLat - minLat));

        // get the mapController and set the zoom and location
        IMapController mapController = openMapView.getController();
        mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * ZOOM_FACTOR),
                (int) (Math.abs(maxLong - minLong) * ZOOM_FACTOR));
        mapController.animateTo(new GeoPoint((maxLat - minLat) / 2, (maxLong - minLong) /2));
    }

    /**
     * Recursive method for handling all comments in the thread. First checks if
     * the comment has any children or not. If none, simply return. Otherwise,
     * call setGeoPointMarker for each child of the comment. Call
     * checkCommmentLocation to calculate the min and max of the lat and long
     * for the entire thread. Then finally make a recursive call to check if a
     * child comment has any children.
     * 
     * @param comment
     */
    private void handleChildComments(Comment comment) {
        ArrayList<Comment> children = comment.getChildren();
        if (children.size() == 0) {
            return;
        } else {
            for (Comment childComment : children) {
                GeoLocation commentLocation = childComment.getLocation();
                if (commentLocationIsValid(childComment)) {
                    Log.e("Child Location", "is valid");
                    geoPoints.add(new GeoPoint(commentLocation.getLatitude() * 1E6, 
                            commentLocation.getLongitude() * 1E6));
                    handleChildComments(childComment);
                }
            }
        }
    }

    /**
     * Checks to see if a comment in the thread has valid GPS coordinates. Valid
     * coordinates are -90 < lat < 90, and -180 < longitude < 180. It also does
     * a null check on location.
     * 
     * @param comment
     * @return isValidLocation
     */
    public boolean commentLocationIsValid(Comment comment) {
        GeoLocation location = comment.getLocation();
        if (location.getLocation() == null) {
            return false;
        } else {
            return (location.getLatitude() >= -90.0 || location.getLatitude() <= 90.0
                    || location.getLongitude() >= -180.0 || location.getLongitude() <= 180.0);
        }
    }

    /**
     * 
     */
    public void calculateZoomSpan() {
        for (GeoPoint geoPoint : geoPoints) {

            Log.e("maxLong", Integer.toString(maxLat));
            Log.e("minLong", Integer.toString(minLat));
            Log.e("maxLat", Integer.toString(maxLat));
            Log.e("minLat", Integer.toString(minLat));
            
            int geoLat = geoPoint.getLatitudeE6();
            int geoLong = geoPoint.getLongitudeE6();

            Log.e("geoLat", Integer.toString(geoLat));
            Log.e("geoLong", Integer.toString(geoLong));

            maxLat = Math.max(geoLat, maxLat);
            minLat = Math.min(geoLat, minLat);
            maxLong = Math.max(geoLong, maxLong);
            minLong = Math.min(geoLong, minLong);
        }
    }

    /**
     * Creates a marker obejct, sets its position to the GeoPoint location
     * passed, and then adds the marker to the map overlays.
     * 
     * @param geoPoint
     */
    public void setGeoPointMarkers() {
        for (GeoPoint geoPoint : geoPoints) {
            Marker marker = new Marker(openMapView);
            marker.setPosition(geoPoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            openMapView.getOverlays().add(marker);
        }
    }

    /**
     * Called when the get_directions_button is clicked. Displays directions
     * from the users current location to the comment location. Uses an Async
     * task to get map overlay. If the users current location cannot be
     * obtained, an error is shown to the screen and the async task is not
     * called
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