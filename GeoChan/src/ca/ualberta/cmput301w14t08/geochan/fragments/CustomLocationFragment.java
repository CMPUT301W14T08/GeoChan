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

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
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

/**
 * This class is a fragment which allows the user to specify a custom location
 * for their post/comment via either a custom long/latt or selecting a
 * previously used location.
 * 
 */
public class CustomLocationFragment extends Fragment {

    private ArrayList<LogEntry> logArray;
    private CustomLocationAdapter customLocationAdapter;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private int postType;
    private FragmentManager fm;

    public static final int THREAD = 1;
    public static final int COMMENT = 2;
    public static final int REPLY = 3;

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

    public void onStart() {
        super.onStart();
        // GeoLocationLog log = GeoLocationLog.getInstance();
        logArray = GeoLocationLog.getLogEntries();
        fm = getFragmentManager();

        latitudeEditText = (EditText) getView().findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) getView().findViewById(R.id.longitude_edit_text);
        ;
        ListView lv = (ListView) getView().findViewById(R.id.custom_location_list_view);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setArgsForPreviousLocation((LogEntry) parent.getItemAtPosition(position));
                fm.popBackStackImmediate();
            }
        });
        customLocationAdapter = new CustomLocationAdapter(getActivity(), logArray);
        lv.setAdapter(customLocationAdapter);
    }

    public void submitNewLocationFromCoordinates(View v) {
        if (latitudeEditText.getText().toString().equals("")
                && longitudeEditText.getText().toString().equals("")) {
            ErrorDialog.show(getActivity(), "Coordinates can not be left blank.");
        } else {
            setArgsForCustomCoordinates();
            fm.popBackStackImmediate();
        }
    }

    public void submitCurrentLocation(View v) {
        resetToCurrentLocation();
        fm.popBackStackImmediate();
    }

    // the next 3 methods need to be re-factored together as there is
    // significant overlap

    public void setArgsForCustomCoordinates() {
        Bundle bundle = getArguments();
        postType = bundle.getInt("postType");
        if (postType == THREAD) {
            PostThreadFragment fragment = (PostThreadFragment) getFragmentManager()
                    .findFragmentByTag("postThreadFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", Double.valueOf(latitudeEditText.getText().toString()));
            args.putDouble("LONGITUDE", Double.valueOf(longitudeEditText.getText().toString()));
        } else if (postType == COMMENT) {
            PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                    .findFragmentByTag("repFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", Double.valueOf(latitudeEditText.getText().toString()));
            args.putDouble("LONGITUDE", Double.valueOf(longitudeEditText.getText().toString()));
        }
    }

    public void setArgsForPreviousLocation(LogEntry clickedEntry) {
        Bundle bundle = getArguments();
        postType = bundle.getInt("postType");
        if (postType == THREAD) {
            PostThreadFragment fragment = (PostThreadFragment) getFragmentManager()
                    .findFragmentByTag("postThreadFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", clickedEntry.getGeoLocation().getLatitude());
            args.putDouble("LONGITUDE", clickedEntry.getGeoLocation().getLongitude());
        } else if (postType == COMMENT) {
            PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                    .findFragmentByTag("repFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", clickedEntry.getGeoLocation().getLatitude());
            args.putDouble("LONGITUDE", clickedEntry.getGeoLocation().getLongitude());
        }
    }

    public void resetToCurrentLocation() {
        Bundle bundle = getArguments();
        postType = bundle.getInt("postType");
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        if (postType == THREAD) {
            PostThreadFragment fragment = (PostThreadFragment) getFragmentManager()
                    .findFragmentByTag("postThreadFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", geoLocation.getLatitude());
            args.putDouble("LONGITUDE", geoLocation.getLongitude());
        } else if (postType == COMMENT) {
            PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                    .findFragmentByTag("repFrag");
            Bundle args = fragment.getArguments();
            args.putDouble("LATITUDE", geoLocation.getLatitude());
            args.putDouble("LONGITUDE", geoLocation.getLongitude());
        }
    }

}
