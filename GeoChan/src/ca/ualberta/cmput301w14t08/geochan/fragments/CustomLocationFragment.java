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
import android.os.Bundle;
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
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

public class CustomLocationFragment extends Fragment {

    private ArrayList<LogEntry> logArray;
    private CustomLocationAdapter customLocationAdapter;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private int postType;

    public static final int THREAD = 1;
    public static final int COMMENT = 2;

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
        GeoLocationLog log = GeoLocationLog.getInstance();
        logArray = log.getLogEntries();
        latitudeEditText = (EditText) getView().findViewById(R.id.latitude_edit_text);
        longitudeEditText = (EditText) getView().findViewById(R.id.longitude_edit_text);
        ListView lv = (ListView) getView().findViewById(R.id.custom_location_list_view);
        lv.setOnItemClickListener(new OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Clicked", "An Item in the previous locations");
            }
        });
        customLocationAdapter = new CustomLocationAdapter(logArray);
        lv.setAdapter(customLocationAdapter);
    }

    public void submitLocation(View v) {
        if (v.getId() == R.id.new_location_button) {

            if (latitudeEditText.getText().toString().equals("") && longitudeEditText.getText().toString().equals("")) {
                ErrorDialog.show(getActivity(), "Coordinates can not be left blank.");

            } else {
                Bundle bundle = getArguments();
                postType = bundle.getInt("postType");
                if (postType == THREAD) {
                    Log.e("post type: ", "THREAD");
                    PostThreadFragment fragment = (PostThreadFragment) getFragmentManager().findFragmentByTag("postThreadFrag");
                    Bundle args = fragment.getArguments();
                    args.putDouble("LATITUDE", Double.valueOf(latitudeEditText.getText().toString()));
                    args.putDouble("LONGITUDE", Double.valueOf(longitudeEditText.getText().toString()));
                } else if (postType == COMMENT) {
                    Log.e("post type: ", "COMMENT");
                    PostCommentFragment fragment = (PostCommentFragment) getFragmentManager().findFragmentByTag("comFrag");
                    Bundle args = fragment.getArguments();
                    args.putDouble("LATITUDE", Double.valueOf(latitudeEditText.getText().toString()));
                    args.putDouble("LONGITUDE", Double.valueOf(longitudeEditText.getText().toString()));
                }
                this.getFragmentManager().popBackStackImmediate();
            }
        }

        else if (v.getId() == R.id.current_location_button) {
            this.getFragmentManager().popBackStackImmediate();
        }
    }
}
