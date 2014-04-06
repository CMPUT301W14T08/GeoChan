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

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

/**
 * This class is a fragment which allows the user to specify a custom location
 * for their post/comment via either a custom long/latt or selecting a
 * previously used location.
 * 
 * @author Brad Simons
 * 
 */
public class CustomLocationFragment extends Fragment {

    // private ArrayList<LogEntry> logArray;
    // private CustomLocationAdapter customLocationAdapter;
    private int postType;
    private FragmentManager fm;
    private MapView openMapView;
    private LocationListenerService locationListenerService;
    // private Marker locationMarker;
    private GeoLocation newLocation;
    private GeoLocation currentLocation;
    private MapEventsOverlay mapEventsOverlay;

    // flags for type of post that initiated this fragment
    public static final int POST = 1;
    public static final int REPLY = 3;
    public static final int SORT_THREAD = 4;
    public static final int SORT_COMMENT = 5;
    public static final int EDIT = 6;

    /**
     * Inflates the custom location fragment view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_custom_location, container, false);
    }

    /**
     * Inflates the menu and adds any action bar items that are present
     */
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
        // GeoLocationLog log = GeoLocationLog.getInstance(getActivity());
        // logArray = log.getLogEntries();
        FavouritesFragment favFrag = (FavouritesFragment) getFragmentManager()
                .findFragmentByTag("favouritesFrag");
        if(favFrag != null){
            fm = getChildFragmentManager();
        } else {
            fm = getFragmentManager();
        }

        // get the views
        ListView lv = (ListView) getView().findViewById(R.id.custom_location_list_view);
        openMapView = (MapView) getActivity().findViewById(R.id.map_view);

        // setup all listeners
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        
        // get the OP
        Bundle args = getArguments();
        Comment topComment = (Comment) args.getParcelable("original post");
        if (topComment != null) {
         
        }

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // clicks a previous location item in the list
                LogEntry logEntry = (LogEntry) parent.getItemAtPosition(position);
                setBundleArguments(logEntry.getGeoLocation(), "PREVIOUS_LOCATION");
                fm.popBackStackImmediate();
            }
        });

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {

            /**
             * Called on a single tap
             */
            @Override
            public boolean singleTapUpHelper(IGeoPoint clickedPoint) {
                return false;
            }

            /**
             * Called on a long press on the map. A location marker is created
             * and placed on the map where the user clicked
             */
            @Override
            public boolean longPressHelper(IGeoPoint clickedPoint) {
                newLocation = new GeoLocation(clickedPoint.getLatitude(),
                        clickedPoint.getLongitude());
                handleNewLocationPressed(newLocation);
                return false;
            }
        };

        // customLocationAdapter = new CustomLocationAdapter(getActivity(),
        // logArray);
        // lv.setAdapter(customLocationAdapter);

        setupMap(mapEventsReceiver);
    }

    /**
     * Sets up the map. Implements a map button receiver so that the user can
     * click on the map to set their location. Then gets the users current
     * location and centers the map around their location
     */
    private void setupMap(MapEventsReceiver mapEventsReceiver) {

        mapEventsOverlay = new MapEventsOverlay(getActivity(), mapEventsReceiver);
        openMapView.getOverlays().add(mapEventsOverlay);

        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);

        currentLocation = new GeoLocation(locationListenerService);

        if (currentLocation.getLocation() != null) {
            openMapView.getController().setCenter(new GeoPoint(currentLocation.getLocation()));
            openMapView.getController().setZoom(13);
        } else {
            openMapView.getController().setZoom(2);
        }
    }

    /**
     * Calls onStop in the super class and tells the locationListenerService to
     * stop listening for location updates
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * Called when a user clicks the current location button. Gets the user's
     * current location, puts it in a bundle and passes it back to the previous
     * fragment
     * 
     * @param v
     * 
     */
    public void submitCurrentLocation(View v) {
        GeoLocation currentGeoLocation = new GeoLocation(locationListenerService);
        if (currentGeoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Could not obtain location");
        } else {
            setBundleArguments(currentGeoLocation, "CURRENT_LOCATION");
        }
        fm.popBackStackImmediate();
    }

    /**
     * Called when a user clicks the submit button. If the user has placed a
     * location marker on the map, that location is placed in a bundle and
     * passed back to the previous fragment
     * 
     * @param v
     * 
     */
    public void submitNewLocationFromCoordinates(View v) {
        if (newLocation == null) {
            ErrorDialog.show(getActivity(), "Please select a location on the map");
        } else {
            setBundleArguments(newLocation, "NEW_LOCATION");
            fm.popBackStackImmediate();
        }
    }

    /**
     * Creates a marker object by taking in latitude and longitude values and
     * sets its position on the map view
     * 
     * @param geoLocation
     * @return marker
     */
    private void handleNewLocationPressed(GeoLocation geoLocation) {

        // create the marker and set its position
        Marker locationMarker = new Marker(openMapView);
        locationMarker.setPosition(new GeoPoint(geoLocation.getLatitude(), geoLocation
                .getLongitude()));
        locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        locationMarker.setDraggable(true);
        
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Retrieving Location");
        ThreadManager.startGetPOI(newLocation, dialog, locationMarker);

        // clear map, then re-add events Overlay and add new location marker,
        // then refresh
        // the map
        openMapView.getOverlays().clear();
        openMapView.getOverlays().add(mapEventsOverlay);
        openMapView.getOverlays().add(locationMarker);
        openMapView.invalidate();
    }

    /**
     * Bundles up all arguments required to be passed to previous fragment. This
     * depends on the post type. It will attach latitude, longitude and
     * description of the location to be submitted, as well as the type of
     * location being returned (current location of user or a new location set
     * on the map)
     * 
     * @param newLocation
     */
    public void setBundleArguments(GeoLocation locationToSubmit, String locationType) {
        Bundle bundle = getArguments();
        postType = bundle.getInt("postType");
        if (postType == POST) {
            PostFragment fragment = (PostFragment) getFragmentManager()
                    .findFragmentByTag("postFrag");
            if(fragment == null){
                fragment = (PostFragment) getChildFragmentManager()
                        .findFragmentByTag("postFrag");
            }
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", locationToSubmit.getLatitude());
            args.putDouble("LONGITUDE", locationToSubmit.getLongitude());
            args.putString("LocationType", locationType);
            args.putString("locationDescription", locationToSubmit.getLocationDescription());
        } else if (postType == SORT_THREAD) {
            SortUtil.setThreadSortGeo(locationToSubmit);
        } else if (postType == SORT_COMMENT) {
            SortUtil.setCommentSortGeo(locationToSubmit);
        } else if (postType == EDIT) {
            EditFragment fragment = (EditFragment) fm.findFragmentByTag("editFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", locationToSubmit.getLatitude());
            args.putDouble("LONGITUDE", locationToSubmit.getLongitude());
            args.putString("LocationType", locationType);
            args.putString("locationDescription", locationToSubmit.getLocationDescription());
        }
    }
}
