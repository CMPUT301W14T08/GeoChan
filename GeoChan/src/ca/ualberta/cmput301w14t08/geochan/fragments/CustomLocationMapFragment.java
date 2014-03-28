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

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
    private Marker currentLocationMarker;
    private Marker newLocationMarker;
    private String locationDescription;
    private GeoPoint currentGeoPoint;

    class GetPOIAsyncTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog directionsLoadingDialog = new ProgressDialog(getActivity());

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
         * Calculating the directions from the current to the location of the
         * topComment.
         */
        @Override
        protected Void doInBackground(Void... params) {
            
            GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("shadowsirens");
            ArrayList<POI> pois = poiProvider.getPOICloseTo(currentGeoPoint, 30, 20.0);
            POI poi = pois.get(0);
            locationDescription = poi.mCategory;
            
            return null;
        }

        /**
         * Task is now finished, dismiss the ProgressDialog
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            directionsLoadingDialog.dismiss();            
            currentLocationMarker.setTitle("Current Location");
            currentLocationMarker.setTitle(locationDescription);
            currentLocationMarker.showInfoWindow();
        }
    }
    
    /**
     * COMMENT HERE
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_custom_location_map, container, false);
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

        openMapView = (MapView) getActivity().findViewById(R.id.select_location_map_view);

        MapEventsReceiver mapReceiver = new MapEventsReceiver() {

            @Override
            public boolean singleTapUpHelper(IGeoPoint clickedPoint) {
                return false;
            }

            @Override
            public boolean longPressHelper(IGeoPoint clickedPoint) {
                newLocationMarker = createNewMarker(clickedPoint.getLatitude(),
                        clickedPoint.getLongitude());
                addNewLocationMarker();
                return false;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), mapReceiver);
        openMapView.getOverlays().add(mapEventsOverlay);

        this.setupMap();
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * COMMENT HERE
     */
    public void setupMap() {
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        openMapView.getController().setZoom(11);

        // get users current location and center map around it
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        currentGeoPoint = new GeoPoint(geoLocation.getLocation());
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Could not obtain location");
        } else {

            GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());
            openMapView.getController().setCenter(geoPoint);
            currentLocationMarker = createNewMarker(geoLocation.getLatitude(),
                    geoLocation.getLongitude());
            new GetPOIAsyncTask().execute();
            this.setMarkerOnMap(currentLocationMarker);
        }
    }

    /**
     * COMMENT HERE
     */
    private void addNewLocationMarker() {
        // clear existing nodes
        currentLocationMarker.hideInfoWindow();
        openMapView.getOverlays().clear();

        // add back currentlocation marker and new location marker
        // then refresh map
        openMapView.getOverlays().add(currentLocationMarker);
        openMapView.getOverlays().add(newLocationMarker);
        openMapView.invalidate();
    }

    /**
     * COMMENT
     * 
     * @param latitude
     * @param longitude
     * @return
     */
    private Marker createNewMarker(double latitude, double longitude) {
        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
        Marker marker = new Marker(openMapView);
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        return marker;
    }

    /**
     * COMMENT HERE
     * 
     * @param clickedPoint
     */
    private void setMarkerOnMap(Marker marker) {
        openMapView.getOverlays().add(marker);
        openMapView.invalidate();

        openMapView.getController().setCenter(marker.getPosition());
        openMapView.getController().setZoom(12);

        openMapView.invalidate();
    }
}
