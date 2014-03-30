/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.CustomLocationAdapter;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationMapFragment.GetPOIAsyncTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

/**
 * This class is a fragment which allows the user to specify a custom location
 * for their post/comment via either a custom long/latt or selecting a
 * previously used location.
 * 
 * @author AUTHOR HERE
 * 
 */
public class CustomLocationFragment extends Fragment {

    private ArrayList<LogEntry> logArray;
    private CustomLocationAdapter customLocationAdapter;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private int postType;
    private FragmentManager fm;
    private MapView openMapView;
    private LocationListenerService locationListenerService;
    private Marker locationMarker;

    // flags for type of post that initiated this fragment
    public static final int THREAD = 1;
    public static final int COMMENT = 2;
    public static final int REPLY = 3;
    public static final int SORT_THREAD = 4;
    public static final int SORT_COMMENT = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_custom_location, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // inflater.inflate(R.menu.thread_list, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Setups up the Location Log and creates connection to buttons and text
     * fields. Also setups an onItemClickListener for previous location items.
     * 
     */
    public void onStart() {
        super.onStart();
        GeoLocationLog log = GeoLocationLog.getInstance(getActivity());
        logArray = log.getLogEntries();

        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();

        fm = getFragmentManager();

        latitudeEditText = (EditText) getView().findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) getView().findViewById(R.id.longitude_edit_text);

        ListView lv = (ListView) getView().findViewById(R.id.custom_location_list_view);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicks a previous location item in the list
                LogEntry logEntry = (LogEntry) parent.getItemAtPosition(position);
                setBundleArguments(logEntry.getGeoLocation(), "PREVIOUS_LOCATION");
                fm.popBackStackImmediate();
            }
        });

        customLocationAdapter = new CustomLocationAdapter(getActivity(), logArray);
        lv.setAdapter(customLocationAdapter);

        setupMap();
    }

    private void setupMap() {
        openMapView = (MapView) getActivity().findViewById(R.id.select_location_map_view);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {

            @Override
            public boolean singleTapUpHelper(IGeoPoint clickedPoint) {
                return false;
            }

            @Override
            public boolean longPressHelper(IGeoPoint clickedPoint) {
                Log.e("clicked", "longpress");
                createNewMarker(clickedPoint.getLatitude(), clickedPoint.getLongitude());
                addNewLocationMarker();
                return false;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(getActivity(), mapEventsReceiver);
        openMapView.getOverlays().add(mapEventsOverlay);

        GeoLocation geoLocation = new GeoLocation(locationListenerService);

        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);

        openMapView.getController().setZoom(13);
        openMapView.getController().setCenter(new GeoPoint(geoLocation.getLocation()));
    }

    /**
     * Called when a user enters custom Long/Lat coordinates and clicks Submit
     * Location
     * 
     * @param v
     *            WHAT DOTH V?
     * 
     */
    public void submitNewLocationFromCoordinates(View v) {
        /*
         * String latStr = latitudeEditText.getText().toString(); String longStr
         * = longitudeEditText.getText().toString();
         * 
         * if (latStr.equals("") && longStr.equals("")) {
         * ErrorDialog.show(getActivity(),
         * "Coordinates can not be left blank."); } else if (-90 >
         * Double.valueOf(latStr) || 90 < Double.valueOf(latStr) || -180 >
         * Double.valueOf(longStr) || 180 < Double.valueOf(longStr)) {
         * ErrorDialog.show(getActivity(),
         * "Latitude must be between -90 and 90, " +
         * "Longitude must be between -180 and 180"); } else { Double latVal =
         * Double.valueOf(latStr); Double longVal = Double.valueOf(longStr);
         * GeoLocation geoLocation = new GeoLocation(latVal, longVal);
         * setBundleArguments(geoLocation, "NEW_LOCATION");
         * fm.popBackStackImmediate(); }
         */
        GeoLocation geoLocation = new GeoLocation(locationMarker.getPosition().getLatitude(),
                locationMarker.getPosition().getLongitude());
        setBundleArguments(geoLocation, "NEW_LOCATION");
        fm.popBackStackImmediate();
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
     * Called when a user clicks the current location button
     * 
     * @param v
     *            WHAT DOTH V?
     * 
     */
    public void submitCurrentLocation(View v) {
        LocationListenerService listener = new LocationListenerService(getActivity());
        listener.startListening();
        GeoLocation geoLocation = new GeoLocation(listener);
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Could not obtain location");
        } else {
            setBundleArguments(geoLocation, "CURRENT_LOCATION");
        }
        fm.popBackStackImmediate();
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
     * Sets the Bundle arguments for passing back the location to the previous
     * fragment
     * 
     * @param geoLocation
     *            WHAT DOTH geoLocation?
     * 
     */
    public void setBundleArguments(GeoLocation geoLocation, String locationType) {
        Bundle bundle = getArguments();
        postType = bundle.getInt("postType");
        if (postType == THREAD) {
            PostThreadFragment fragment = (PostThreadFragment) getFragmentManager()
                    .findFragmentByTag("postThreadFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", geoLocation.getLatitude());
            args.putDouble("LONGITUDE", geoLocation.getLongitude());
            args.putString("LocationType", locationType);
            args.putString("locationDescription", locationMarker.getSubDescription());
        } else if (postType == COMMENT) {
            PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                    .findFragmentByTag("repFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", geoLocation.getLatitude());
            args.putDouble("LONGITUDE", geoLocation.getLongitude());
            args.putString("LocationType", locationType);
            args.putString("locationDescription", locationMarker.getSubDescription());
        } else if (postType == SORT_THREAD) {
            SortUtil.setThreadSortGeo(geoLocation);
        } else if (postType == SORT_COMMENT) {
            SortUtil.setCommentSortGeo(geoLocation);
        }
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
