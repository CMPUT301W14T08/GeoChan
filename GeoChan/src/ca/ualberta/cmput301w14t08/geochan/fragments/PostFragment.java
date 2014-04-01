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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Responsible for the UI fragment that allows a user to post a new thread.
 * 
 * @author Artem Chikin
 */
public class PostFragment extends Fragment {
    public static final int MAX_BITMAP_DIMENSIONS = 600;

    private LocationListenerService locationListenerService;
    private GeoLocation geoLocation;
    private Bitmap image = null;
    private Bitmap imageThumb = null;
    private ThreadComment thread = null;
    private Comment commentToReplyTo = null;

    // private ImageView thumbnail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args.getLong("id") != -1) {
            commentToReplyTo = (Comment) args.getParcelable("cmt");
            thread = ThreadList.getThreads().get((int) args.getLong("id"));
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        if (thread == null) {
            return inflater.inflate(R.layout.fragment_post_thread, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_post_comment, container, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        geoLocation = new GeoLocation(locationListenerService);
        if (commentToReplyTo != null) {
            TextView replyTo = (TextView) getActivity().findViewById(R.id.comment_replyingTo);
            TextView bodyReplyTo = (TextView) getActivity().findViewById(R.id.reply_to_body);
            bodyReplyTo.setMovementMethod(new ScrollingMovementMethod());
            bodyReplyTo.setText(commentToReplyTo.getTextPost());
            replyTo.setText(commentToReplyTo.getUser() + " says:");
        }
    }

    /**
     * COMMENT GOES HERE
     */
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
                    
                    String locationDescription = args.getString("locationDescription");
                    geoLocation.setLocationDescription(locationDescription);

                    DecimalFormat format = new DecimalFormat();
                    format.setRoundingMode(RoundingMode.HALF_EVEN);
                    format.setMinimumFractionDigits(0);
                    format.setMaximumFractionDigits(4);

                    if (locationDescription.equals("Unknown Location")) {
                        locButton.setText("Lat: " + format.format(lat) + ", Long: "
                                + format.format(lon));
                    } else {
                        locButton.setText("Location: " + locationDescription);
                    }
                }
            }
            if (args.containsKey("IMAGE_THUMB") && args.containsKey("IMAGE_FULL")) {
                imageThumb = args.getParcelable("IMAGE_THUMB");
                image = args.getParcelable("IMAGE_FULL");
                // thumbnail.setImageBitmap(imageThumb);
            }
        }
    }

    /**
     * onClick method for the post button. Extracts the textView information,
     * creates the threadComment object and posts it to the server.
     * 
     * @param view
     *            The post button in the PostThreadFragment
     */
    public void post(View view) {
        if (view.getId() == R.id.post_button) {
            String title = null;
            EditText editTitle = null;
            EditText editComment = (EditText) this.getView().findViewById(R.id.commentBody);
            String comment = editComment.getText().toString();
            if (thread == null) {
                editTitle = (EditText) this.getView().findViewById(R.id.titlePrompt);
                title = editTitle.getText().toString();
            }
            if (title != null && title.equals("")) {
                ErrorDialog.show(getActivity(), "Title can not be left blank.");
            } else {
                Comment newComment = new Comment(comment, image, geoLocation, commentToReplyTo);
                ElasticSearchClient client = ElasticSearchClient.getInstance();
                ThreadManager.startPost(newComment, title);
                if (commentToReplyTo != null) {
                    commentToReplyTo.addChild(newComment);
                    int tag = PreferencesManager.getInstance().getCommentSort();
                    SortUtil.sortComments(tag, thread.getBodyComment().getChildren());
                }
                // log the thread and the geolocation
                if (geoLocation.getLocation() == null) {
                    GeoLocationLog geoLocationLog = GeoLocationLog.getInstance(getActivity());
                    geoLocationLog.addLogEntry(title, geoLocation);
                }
                InputMethodManager inputManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                this.getFragmentManager().popBackStackImmediate();
            }
        }
    }

    /**
     * Displays dialog and either launches camera or gallery
     * 
     * @param View
     *            the AttachPhoto button in postThreadFragment
     */
    public void attachImage(View view) {
        if (view.getId() == R.id.attach_image_button) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.attach_image_title);
            dialog.setMessage(R.string.attach_image_dialog);

            dialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        File file = ImageHelper.createImageFile();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file); // set
                                                                        // the
                                                                        // image
                                                                        // file
                                                                        // name
                        startActivityForResult(Intent.createChooser(intent, "Test"),
                                ImageHelper.REQUEST_GALLERY);
                    } catch (IOException e) {
                        // do something
                    }
                }
            });
            dialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        File file = ImageHelper.createImageFile();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file); // set
                                                                        // the
                                                                        // image
                                                                        // file
                                                                        // name
                        startActivityForResult(Intent.createChooser(intent, "Test"),
                                ImageHelper.REQUEST_CAMERA);
                    } catch (IOException e) {
                        // do something
                    }
                }
            });
            dialog.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageHelper.REQUEST_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
                image = scaleImage(imageBitmap);
                imageThumb = squareBitmap;
                Bundle bundle = getArguments();
                bundle.putParcelable("IMAGE_THUMB", imageThumb);
                bundle.putParcelable("IMAGE_FULL", image);
                // thumbnail.setImageBitmap(squareBitmap);
            } else if (requestCode == ImageHelper.REQUEST_GALLERY) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity()
                            .getContentResolver(), data.getData());
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
                image = scaleImage(imageBitmap);
                imageThumb = squareBitmap;
                Bundle bundle = getArguments();
                bundle.putParcelable("IMAGE_THUMB", imageThumb);
                bundle.putParcelable("IMAGE_FULL", image);
                // thumbnail.setImageBitmap(squareBitmap);
            }
        }
    }
    
    private Bitmap scaleImage(Bitmap bitmap) {
        // https://github.com/bradleyjsimons/PicPoster/blob/master/src/ca/ualberta/cs/picposter/controller/PicPosterController.java
           // Scale the pic if it is too large:
           if (bitmap.getWidth() > MAX_BITMAP_DIMENSIONS
                   || bitmap.getHeight() > MAX_BITMAP_DIMENSIONS) {
               double scalingFactor = bitmap.getWidth() * 1.0 / MAX_BITMAP_DIMENSIONS;
               if (bitmap.getHeight() > bitmap.getWidth())
                   scalingFactor = bitmap.getHeight() * 1.0 / MAX_BITMAP_DIMENSIONS;

               int newWidth = (int) Math.round(bitmap.getWidth() / scalingFactor);
               int newHeight = (int) Math.round(bitmap.getHeight() / scalingFactor);

               bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
           }
           return bitmap;
       }

    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }
}