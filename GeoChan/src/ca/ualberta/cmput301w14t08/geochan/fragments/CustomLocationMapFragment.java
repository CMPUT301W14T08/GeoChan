package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * COMMENT HERE (I will comment this asap)
 * 
 * @author Brad Simons
 * 
 */
public class CustomLocationMapFragment extends Fragment {

    private LocationListenerService locationListenerService;
    private MapView openMapView;
    private Marker locationMarker;

    /**
     * Creates and inflates the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_custom_location_map, container, false);
    }

    /**
     * Inflates the menu and adds any items to the action bar if present
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onStart() {
        super.onStart();

        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        openMapView = (MapView) getActivity().findViewById(R.id.select_location_map_view_2);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {

            @Override
            public boolean singleTapUpHelper(IGeoPoint clickedPoint) {
                return false;
            }

            @Override
            public boolean longPressHelper(IGeoPoint clickedPoint) {
                createNewMarker(clickedPoint.getLatitude(), clickedPoint.getLongitude());
                addNewLocationMarker();
                return false;
            }
        };

        Button submitButton = (Button) getActivity().findViewById(
                R.id.submit_location_from_map_button);

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLocation();
            }
        });

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), mapEventsReceiver);
        openMapView.getOverlays().add(mapEventsOverlay);

        this.setupMap();
    }

    /**
     * Calls on stop in the super class and tells the locationListener to stop
     * listening
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * Sets up the map view. Sets the tile source to Mapnik, gets the users
     * location, creates a marker for that location, and sends a request for POI
     * information via an asynch task.
     */
    public void setupMap() {
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        
        openMapView.getController().setZoom(13);
        openMapView.getController().setCenter(new GeoPoint(geoLocation.getLocation())); 
    }

    /**
     * Clears the nodes off of the map, and then re-adds the current location
     */
    private void addNewLocationMarker() {
        openMapView.getOverlays().clear();

        new GetPOIAsyncTask().execute(locationMarker);
        locationMarker.setTitle("Dropped Pin");

        // add back currentlocation marker and new location marker
        // then refresh map
        openMapView.getOverlays().add(locationMarker);
        openMapView.invalidate();
    }

    /**
     * Creates a marker object by taking in latitude and longitude values and
     * sets its position on the map view
     * 
     * @param latitude
     * @param longitude
     * @return marker
     */
    private void createNewMarker(double latitude, double longitude) {
        locationMarker = new Marker(openMapView);
        locationMarker.setPosition(new GeoPoint(latitude, longitude));
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
    }

    /**
     * Takes a marker object as a parameter and sets it on the map view
     * 
     * @param marker
     */
    private void setMarkerOnMap() {
        openMapView.getOverlays().add(locationMarker);
        openMapView.getController().setCenter(locationMarker.getPosition());
        openMapView.invalidate();
    }

    /**
     * submit the location from the map
     */
    private void submitLocation() {
        Log.e("subDescription", locationMarker.getSubDescription());
        
        Bundle args = new Bundle();
        
        //args.putDouble("Latitude", newLocationMarker.getPosition().getLatitude());
        //args.putDouble("Longitude", newLocationMarker.getPosition().getLongitude());
        //args.putString("LocationName", locationMarker.getSubDescription());

        getFragmentManager().popBackStackImmediate();
    }

    /**
     * Async task for getting the POI of a location and place a marker on the
     * map
     * 
     * @author bradsimons
     */
    class GetPOIAsyncTask extends AsyncTask<Marker, Void, Marker> {

        ProgressDialog directionsLoadingDialog = new ProgressDialog(getActivity());
        POI poi;

        /**
         * Displays a ProgessDialog while the task is executing
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            directionsLoadingDialog.setMessage("Loading");
            directionsLoadingDialog.show();
        }

        /**
         * Get the points of interest
         */
        @Override
        protected Marker doInBackground(Marker... markers) {
            for (Marker marker : markers) {
                GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("bradleyjsimons");
                ArrayList<POI> pois = poiProvider.getPOICloseTo(marker.getPosition(), 2, 0.3);

                if (pois.size() > 0 && pois != null) {
                    poi = pois.get(0);
                } else {
                    poi = null;
                }

                return marker;
            }
            return null;
        }

        /**
         * Task is now finished, dismiss the ProgressDialog
         */
        @Override
        protected void onPostExecute(Marker marker) {
            super.onPostExecute(marker);
            directionsLoadingDialog.dismiss();

            if (poi != null) {
                marker.setSubDescription(poi.mType);
            } else {
                marker.setSubDescription("Unknown Location");
            }
            
            marker.showInfoWindow();
            setMarkerOnMap();
        }
    }
}
