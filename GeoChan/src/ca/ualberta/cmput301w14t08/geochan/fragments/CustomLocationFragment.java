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
import android.widget.EditText;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.CustomLocationAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;

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
    private LocationListenerService listener;
    private int postType;
    private FragmentManager fm;

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
     * @author AUTHOR HERE
     */
    public void onStart() {
        super.onStart();
        listener = new LocationListenerService(getActivity());
        listener.startListening();
        GeoLocationLog log = GeoLocationLog.getInstance(getActivity());
        logArray = log.getLogEntries();

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
    }
    
    @Override
    public void onPause() {
        super.onPause();
        listener.stopListening();
    }

    /**
     * Called when a user enters custom Long/Lat coordinates and clicks Submit
     * Location
     * 
     * @param v WHAT DOTH V?
     * 
     * @author AUTHOR HERE
     */
    public void submitNewLocationFromCoordinates(View v) {
        String latStr = latitudeEditText.getText().toString();
        String longStr = longitudeEditText.getText().toString();

        if (latStr.equals("") && longStr.equals("")) {
            ErrorDialog.show(getActivity(), "Coordinates can not be left blank.");
        } else if (-90 > Double.valueOf(latStr) || 90 < Double.valueOf(latStr)
                || -180 > Double.valueOf(longStr) || 180 < Double.valueOf(longStr)) {
            ErrorDialog.show(getActivity(), "Latitude must be between -90 and 90, "
                    + "Longitude must be between -180 and 180");
        } else {
            Double latVal = Double.valueOf(latStr);
            Double longVal = Double.valueOf(longStr);
            GeoLocation geoLocation = new GeoLocation(latVal, longVal);
            setBundleArguments(geoLocation, "NEW_LOCATION");
            fm.popBackStackImmediate();
        }
    }

    /**
     * Called when a user clicks the current location button
     * 
     * @param v WHAT DOTH V?
     * 
     * @author AUTHOR HERE
     */
    public void submitCurrentLocation(View v) {
        
        GeoLocation geoLocation = new GeoLocation(listener);
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Could not obtain location");
        } else {
            setBundleArguments(geoLocation, "CURRENT_LOCATION");
        }
        fm.popBackStackImmediate();
    }

    /**
     * Sets the Bundle arguments for passing back the location to the previous
     * fragment
     * 
     * @param geoLocation WHAT DOTH geoLocation?
     * 
     * @author AUTHOR HERE
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
        } else if (postType == COMMENT) {
            PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                    .findFragmentByTag("repFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", geoLocation.getLatitude());
            args.putDouble("LONGITUDE", geoLocation.getLongitude());
            args.putString("LocationType", locationType);
        } else if (postType == SORT_THREAD) {
            SortUtil.setThreadSortGeo(geoLocation);
        } else if (postType == SORT_COMMENT){
            SortUtil.setCommentSortGeo(geoLocation);
        }
    }
}
