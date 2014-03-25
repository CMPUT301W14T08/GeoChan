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

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.UserHashManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Responsible for the UI fragment that allows a user to post a reply to a
 * thread.
 */
public class PostReplyFragment extends Fragment {
    ThreadComment thread;
    Comment commentToReplyTo;
    private LocationListenerService locationListenerService;
    private ImageHelper imageHelper;
    private Bitmap picture;
    private Bitmap thumb;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_post_reply, container, false);
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

        TextView replyTo = (TextView) getActivity().findViewById(R.id.comment_replyingTo);
        TextView bodyReplyTo = (TextView) getActivity().findViewById(R.id.reply_to_body);
        imageView = (ImageView) getActivity().findViewById(R.id.imageView1);
        bodyReplyTo.setMovementMethod(new ScrollingMovementMethod());
        bodyReplyTo.setText(commentToReplyTo.getTextPost());
        replyTo.setText(commentToReplyTo.getUser() + " says:");
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        thumb = null;
        picture = null;
    }

    public void postReply(View v) {
        if (v.getId() == R.id.post_reply_button) {
            EditText editComment = (EditText) this.getView().findViewById(R.id.replyBody);
            String comment = editComment.getText().toString();
            GeoLocation geoLocation = new GeoLocation(locationListenerService);
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
            }
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            this.getFragmentManager().popBackStackImmediate();
        }
    }

    public void attachImage(View v) {
        if (v.getId() == R.id.attach_image_button) {
            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
            myAlertDialog.setTitle(R.string.attach_image_title);
            myAlertDialog.setMessage(R.string.attach_image_dialog);

            myAlertDialog.setPositiveButton("Gallery",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            try {
                                File file = imageHelper.createImageFile();
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, file); // set the image file name
                                startActivityForResult(intent, 1); 
                            } catch (IOException e) {
                                //do something
                            }
                        }
                    });

            myAlertDialog.setNegativeButton("Camera",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            try {
                                File file = imageHelper.createImageFile();
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, file); // set the image file name
                                startActivityForResult(intent, 1);
                            } catch (IOException e) {
                                //do something
                            }
                        }
                    });
            myAlertDialog.show();             
        }
    }
 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 256, 256);
            picture = imageBitmap;
            thumb = squareBitmap;
            imageView.setImageBitmap(squareBitmap);
            Log.d("imagehelper","Image set successfully");
        }
    }
    
    public String retrieveUsername() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        UserHashManager manager = UserHashManager.getInstance();
        return preferences.getString("username", "Anon") + "#" + manager.getHash();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }
}
