package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.GridMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
 * A Fragment class for displaying Maps. The Map will display the locations of
 * each comment in the thread, and will center around the original post. It will
 * provide the feature of getting directions from the users current location to
 * the location of the original post.
 * 
 * @author Brad Simons
 * 
 */
public class MapViewFragment extends Fragment {

    final public static double ZOOM_FACTOR = 1.2;

    private MapView openMapView;
    private LocationListenerService locationListenerService;
    private GeoLocation currentLocation;
    private GeoPoint startGeoPoint;
    private Polyline roadOverlay;
    private Comment topComment;
    private ArrayList<GeoPoint> geoPoints;
    private ArrayList<Marker> markers;
    private GridMarkerClusterer poiMarkers;

    /**
     * Gets the view when inflated, then calls setZoomLevel to display the
     * correct map area.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);

        geoPoints = new ArrayList<GeoPoint>();
        markers = new ArrayList<Marker>();

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

        poiMarkers = new GridMarkerClusterer(getActivity());
        Drawable clusterIconD = getResources().getDrawable(R.drawable.marker_cluster);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();
        poiMarkers.setIcon(clusterIcon);
        
        GeoLocation geoLocation = topComment.getLocation();
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Thread has no location");
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();
        } else {
            this.setupMap(topComment);
            this.setMarkers();
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
     * @param topComment
     */
    public void setupMap(Comment topComment) {
        openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        openMapView.getController().setZoom(5);

        if (commentLocationIsValid(topComment)) {
            GeoLocation geoLocation = topComment.getLocation();
            startGeoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
            geoPoints.add(startGeoPoint);

            Marker startMarker = createMarker(geoLocation, "OP");
            startMarker.showInfoWindow();

            poiMarkers.add(startMarker);
            handleChildComments(topComment);
        }
        openMapView.invalidate();
    }

    /**
     * Sets the default zoom level for the mapview. This takes the max and min
     * of both lat and long, and zooms to span the area required. It also
     * animates to the startGeoPoint, which is the location of the topComment.
     * The values must be padded with a zoom_factor, which is a static class
     * variable
     */
    public void setZoomLevel() {
        // get the mapController and set the zoom
        IMapController mapController = openMapView.getController();

        int zoomFactor;
        int zoomSpan = calculateZoomSpan();

        // calculates the appropriate zoom level
        zoomFactor = 19 - (int) (Math.log10(zoomSpan) * 1.9);
        if (zoomFactor > 18 || zoomSpan < 1) {
            zoomFactor = 18;
        } else if (zoomFactor < 2) {
            zoomFactor = 2;
        }

        // set the zoom center
        Log.e("setting zoom level to", Integer.toString(zoomFactor));
        mapController.setZoom(zoomFactor);
        mapController.animateTo(geoPoints.get(0));
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
                    geoPoints.add(commentLocation.makeGeoPoint());
                    Marker replyMarker = createMarker(commentLocation, "Reply");
                    poiMarkers.add(replyMarker);
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
     * Calculates the minimum and maximum values for latitude and longitude
     * between an array of GeoPoints. This is used to
     */
    private int calculateZoomSpan() {

        // To calculate the max and min latitude and longitude of all
        // the comments, we set the min's to max integer values and vice versa
        // then have values of each comment modify these variables
        int minLat = Integer.MAX_VALUE;
        int maxLat = Integer.MIN_VALUE;
        int minLong = Integer.MAX_VALUE;
        int maxLong = Integer.MIN_VALUE;

        for (GeoPoint geoPoint : geoPoints) {
            int geoLat = geoPoint.getLatitudeE6();
            int geoLong = geoPoint.getLongitudeE6();

            maxLat = Math.max(geoLat, maxLat);
            minLat = Math.min(geoLat, minLat);
            maxLong = Math.max(geoLong, maxLong);
            minLong = Math.min(geoLong, minLong);
        }

        int deltaLong = maxLong - minLong;
        int deltaLat = maxLat - minLat;
        return Math.max(deltaLong, deltaLat);
    }

    /**
     * Creates a marker object, sets its position to the GeoPoint location
     * passed, and then adds the marker to the map overlays.
     * 
     * @param geoPoint
     */
    public void setMarkers() {
        for (Marker marker : markers) {
            openMapView.getOverlays().add(marker);
        }
    }

    /**
     * 
     * @param geoLocation
     * @param postType
     * @return
     */
    public Marker createMarker(GeoLocation geoLocation, String postType) {

        Marker marker = new Marker(openMapView);
        marker.setTitle(postType);

        GeoPoint geoPoint = geoLocation.makeGeoPoint();
        marker.setPosition(geoPoint);

        if (geoLocation.getLocationDescription() != null) {
            marker.setSubDescription(geoLocation.getLocationDescription());
        } else {
            marker.setSubDescription("Unknown Location");
        }

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        return marker;
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
            new GetDirectionsAsyncTask().execute();
            this.setMarkers();
        }

        openMapView.invalidate();
    }

    /**
     * Async task class. This task is designed to retrieve directions from the
     * users current location to the location of the original post of the
     * thread. It displays a ProgressDialog while the location is being
     * retrieved.
     * 
     * @author Brad Simons
     */
    class GetDirectionsAsyncTask extends AsyncTask<Void, Void, Void> {

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
         * topComment. Builds a road overlay and adds it to the openMapView
         * objects overlays
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
            
            Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
            for (int i=0; i<road.mNodes.size(); i++){
                    RoadNode node = road.mNodes.get(i);
                    Marker nodeMarker = new Marker(openMapView);
                    nodeMarker.setPosition(node.mLocation);
                    nodeMarker.setIcon(nodeIcon);
                    nodeMarker.setTitle("Step "+i);
                    nodeMarker.setSnippet(node.mInstructions);
                    nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
                    openMapView.getOverlays().add(nodeMarker);
                    Drawable icon = getResources().getDrawable(R.drawable.ic_continue);
                    nodeMarker.setImage(icon);
                    poiMarkers.add(nodeMarker);
            }
            return null;
        }

        /**
         * Task is now finished, dismiss the ProgressDialog and refresh the map
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            directionsLoadingDialog.dismiss();

            GeoLocation currentLocation = new GeoLocation(locationListenerService);
            geoPoints.add(new GeoPoint(currentLocation.getLatitude(), currentLocation
                    .getLongitude()));
            Marker currentLocationMarker = createMarker(currentLocation, "Your Location");
            poiMarkers.add(currentLocationMarker);
            
            currentLocationMarker.showInfoWindow();

            setZoomLevel();

            openMapView.getOverlays().add(poiMarkers);
            openMapView.invalidate();
        }
    }

}