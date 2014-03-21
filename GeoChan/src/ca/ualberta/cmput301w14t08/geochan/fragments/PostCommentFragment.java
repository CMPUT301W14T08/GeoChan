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

import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * This class is responsible for the fragment that allows user to post a reply
 * to an existing comment.
 */
public class PostCommentFragment extends Fragment {
    ThreadComment thread;
    Comment commentToReplyTo;
    private GeoLocation geoLocation;
    private LocationListenerService locationListenerService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_post_comment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        commentToReplyTo = (Comment) bundle.getParcelable("cmt");
        thread = ThreadList.getThreads().get((int) bundle.getLong("id"));
        TextView replyTo = (TextView) getActivity().findViewById(R.id.comment_replyingTo);
        TextView bodyReplyTo = (TextView) getActivity().findViewById(R.id.reply_to_body);
        bodyReplyTo.setMovementMethod(new ScrollingMovementMethod());
        bodyReplyTo.setText(commentToReplyTo.getTextPost());
        replyTo.setText(commentToReplyTo.getUser() + " says:");
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        geoLocation = new GeoLocation(locationListenerService);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey("LATITUDE") && args.containsKey("LONGITUDE")) {
                Button locButton = (Button) getActivity().findViewById(R.id.location_button);
                if (args.getString("LocationType") == "CURRENT_LOCATION") {
                    locButton.setText("Current Location");
                } else {
                    Double lat = args.getDouble("LATITUDE");
                    Double lon = args.getDouble("LONGITUDE");
                    geoLocation.setCoordinates(lat, lon);
                    
                    DecimalFormat format = new DecimalFormat();
                    format.setRoundingMode(RoundingMode.HALF_EVEN);
                    format.setMinimumFractionDigits(0);
                    format.setMaximumFractionDigits(4);

                    locButton.setText("Lat: " + format.format(lat) + ", Lon: " + format.format(lon));
                }
            }
        }
    }

    public void postReply(View v) {
        if (v.getId() == R.id.post_reply_button) {
            EditText editComment = (EditText) this.getView().findViewById(R.id.replyBody);
            String comment = editComment.getText().toString();
            if (geoLocation.getLocation() == null) {
                // ErrorDialog.show(getActivity(),
                // "Could not obtain location.");
                // Create a new comment object and set username
                Comment newComment = new Comment(comment, null, commentToReplyTo);
                ElasticSearchClient client = ElasticSearchClient.getInstance();
                client.postComment(thread, commentToReplyTo, newComment);
            } else {
                // Create a new comment object and set username
                Comment newComment = new Comment(comment, geoLocation, commentToReplyTo);
                ElasticSearchClient client = ElasticSearchClient.getInstance();
                client.postComment(thread, commentToReplyTo, newComment);
                GeoLocationLog geoLocationLog = GeoLocationLog.getInstance(getActivity());
                geoLocationLog.addLogEntry(thread.getTitle(), geoLocation);
            }

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }
}
