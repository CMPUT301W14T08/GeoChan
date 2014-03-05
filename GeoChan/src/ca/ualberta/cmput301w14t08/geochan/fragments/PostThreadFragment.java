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

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashGenerator;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Responsible for the UI fragment that allows a user to post a new thread.
 */
public class PostThreadFragment extends Fragment {
    private LocationListenerService locationListenerService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_post_thread, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
    }

    public void postNewThread(View v) {
        if (v.getId() == R.id.post_thread_button) {
            EditText editTitle = (EditText) this.getView().findViewById(R.id.titlePrompt);
            EditText editComment = (EditText) this.getView().findViewById(R.id.commentBody);
            String title = editTitle.getText().toString();
            String comment = editComment.getText().toString();
            if (title.equals("")) {
                ErrorDialog.show(getActivity(), "Title can not be left blank.");
            } else {
                GeoLocation geoLocation = new GeoLocation(locationListenerService);
                if (geoLocation.getLocation() == null) {
                    ErrorDialog.show(getActivity(), "Could not obtain location.");
                    // Create a new comment object and set username
                    Comment newComment = new Comment(comment, null);
                    newComment.setUser(retrieveUsername());
                    ThreadList.addThread(newComment, title);
                } else {
                    // Create a new comment object and set username
                    Comment newComment = new Comment(comment, geoLocation);
                    newComment.setUser(retrieveUsername());
                    ThreadList.addThread(newComment, title);
                }
                InputMethodManager inputManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                this.getFragmentManager().popBackStackImmediate();
            }
        }
    }

    public String retrieveUsername() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        return preferences.getString("username", "Anon") + "#" + HashGenerator.getHash();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }
}
