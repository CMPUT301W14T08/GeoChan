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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class MapViewFragment extends Fragment {

    private MapView openMapView;
    private IMapController mapController;
    private LocationListenerService locationListenerService;
    private GeoLocation currentLocation;
    private GeoPoint startGeoPoint;
    private Activity activity;
    private Polyline roadOverlay;
    private ArrayList<Comment> listOfComments;

    class MapAsyncTask extends AsyncTask<Void,Void,Void> {

        ProgressDialog directionsLoadingDialog = new ProgressDialog(activity);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            directionsLoadingDialog.setMessage("Getting Directions");
            directionsLoadingDialog.show();
        }

        @Override
        protected Void doInBackground(Void ... params) {
            RoadManager roadManager = new OSRMRoadManager();
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

            waypoints.add(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
            waypoints.add(startGeoPoint);
            Road road = roadManager.getRoad(waypoints);

            roadOverlay = RoadManager.buildRoadOverlay(road, activity);
            openMapView.getOverlays().add(roadOverlay);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            directionsLoadingDialog.dismiss();
        }
    }  


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * gets the comment from the bundle. If it has a valid location, set up the map
     * otherwise return to previous fragment
     */
    @Override 
    public void onStart() {
        super.onStart();

        activity = getActivity();

        locationListenerService = new LocationListenerService(activity);
        locationListenerService.startListening();

        currentLocation = new GeoLocation(locationListenerService);

        Bundle args = getArguments();
        Comment comment = (Comment) args.getParcelable("thread_comment");
        
        GeoLocation geoLocation = comment.getLocation();
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Thread has no location");
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();
        } else {
            this.setupMap(comment);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * This setups up the comment location the map. The map is center at the location
     * of the comment GeoLocation, and has a bubble on this point/
     * @param geoLocation
     */
    public void setupMap(Comment comment) {
        openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);

        GeoLocation geoLocation = comment.getLocation();
        startGeoPoint = new GeoPoint(geoLocation.getLocation());
        setGeoPointMarker(startGeoPoint);
        buildMarkersForChildComments(comment);

        mapController = openMapView.getController();
        mapController.setZoom(12);
        mapController.animateTo(startGeoPoint);
    }
    
    private void buildMarkersForChildComments(Comment comment) {
        ArrayList<Comment> children = comment.getChildren();
        if (children.size() == 0) {
            return;
        } else {
            for (Comment c : children) {
                GeoLocation commentLocation = c.getLocation();
                setGeoPointMarker(new GeoPoint(commentLocation.getLatitude(), commentLocation.getLongitude()));
            }
        }
    }
    
    public void setGeoPointMarker(GeoPoint geoPoint) {
        Marker marker = new Marker(openMapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        openMapView.getOverlays().add(marker);
    }

    /**
     * Called when the get_directions_button is clicked. Displays directions from
     * current location to the comment location. Uses an Async task to get map overlay
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