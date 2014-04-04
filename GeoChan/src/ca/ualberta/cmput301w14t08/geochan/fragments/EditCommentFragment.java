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
import java.util.ArrayList;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Allows the user to edit the text, location, and image of a comment they have
 * made.
 * 
 * @author Henry Pabst
 * 
 */
public class EditCommentFragment extends Fragment {
    private static final int MAX_BITMAP_DIMENSIONS = 600;
    private Comment editComment;
    private EditText newTextPost;
    private ImageView oldThumbView;
    private static Bitmap oldThumbnail;
    private static String oldText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_edit_comment, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        String commentId = bundle.getString("commentId");
        int threadIndex = bundle.getInt("threadIndex");
        ThreadComment thread = ThreadList.getThreads().get(threadIndex);
        if (thread.getBodyComment().getId().equals(commentId)) {
            editComment = thread.getBodyComment();
        } else {
            getCommentFromId(commentId, thread.getBodyComment().getChildren());
        }
        if (EditCommentFragment.oldText == null) {
            EditCommentFragment.oldText = editComment.getTextPost();
            TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
            oldTextView.setText(EditCommentFragment.oldText);
        }
        if (EditCommentFragment.oldThumbnail == null && editComment.getImageThumb() != null) {
            EditCommentFragment.oldThumbnail = editComment.getImageThumb();
            oldThumbView = (ImageView) getActivity().findViewById(R.id.old_thumb);
            oldThumbView.setImageBitmap(EditCommentFragment.oldThumbnail);
        }
        newTextPost = (EditText) getActivity().findViewById(R.id.editBody);
        newTextPost.setText(editComment.getTextPost());
        newTextPost.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (EditCommentFragment.oldText != null) {
            TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
            oldTextView.setText(EditCommentFragment.oldText);
        }
        if (args != null) {
            if (args.containsKey("LATITUDE") && args.containsKey("LONGITUDE")) {
                Button locButton = (Button) getActivity().findViewById(R.id.edit_location_button);
                if (args.getString("LocationType") == "CURRENT_LOCATION") {
                    locButton.setText("Current Location");
                } else {
                    GeoLocation geoLocation = editComment.getLocation();
                    Double lat = args.getDouble("LATITUDE");
                    Double lon = args.getDouble("LONGITUDE");
                    geoLocation.setCoordinates(lat, lon);

                    DecimalFormat format = new DecimalFormat();
                    format.setRoundingMode(RoundingMode.HALF_EVEN);
                    format.setMinimumFractionDigits(0);
                    format.setMaximumFractionDigits(4);

                    locButton.setText(format.format(lat) + ", " + format.format(lon));
                    locButton
                            .setHint("Lat: " + format.format(lat) + ", Lon: " + format.format(lon));
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        editComment.setTextPost(newTextPost.getText().toString());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Recursively finds the comment with the passed ID and sets it to the
     * variable editComment.
     * 
     * @param id
     *            The ID of the comment to be found.
     * @param comments
     *            An ArrayList of Comments to start searching.
     */
    public void getCommentFromId(String id, ArrayList<Comment> comments) {
        for (Comment com : comments) {
            if (com.getId().equals(id)) {
                editComment = com;
                return;
            } else {
                getCommentFromId(id, com.getChildren());
            }
        }
        return;
    }

    /**
     * Allows the user to change the image attached to their comment. Copied
     * from attachImageReply in PostCommentFragment, presumably originally
     * written by either ArtemC or ArtemH.
     * 
     * @param view
     */
    public void editImage(View view) {
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
            dialog.setNeutralButton("Remove Image", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    editComment.setImage(null);
                    editComment.setImageThumb(null);
                }
            });
            dialog.show();
        }
    }

    /**
     * Gets called after the user selects a new image to post from their gallery
     * or one that they've taken. Sets the image and image thumb in editComment
     * to the new image they want attached to their comment.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageHelper.REQUEST_CAMERA) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                image = scaleImage(imageBitmap);
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
                image = scaleImage(imageBitmap);
            }
        }
        editComment.setImage(image);
        Bitmap imageThumb = ThumbnailUtils.extractThumbnail(image, 96, 96);
        editComment.setImageThumb(imageThumb);
    }

    private Bitmap scaleImage(Bitmap bitmap) {
        // https://github.com/bradleyjsimons/PicPoster/blob/master/src/ca/ualberta/cs/picposter/controller/PicPosterController.java
        // Scale the pic if it is too large:
        if (bitmap.getWidth() > MAX_BITMAP_DIMENSIONS || bitmap.getHeight() > MAX_BITMAP_DIMENSIONS) {
            double scalingFactor = bitmap.getWidth() * 1.0 / MAX_BITMAP_DIMENSIONS;
            if (bitmap.getHeight() > bitmap.getWidth())
                scalingFactor = bitmap.getHeight() * 1.0 / MAX_BITMAP_DIMENSIONS;

            int newWidth = (int) Math.round(bitmap.getWidth() / scalingFactor);
            int newHeight = (int) Math.round(bitmap.getHeight() / scalingFactor);

            bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        }
        return bitmap;
    }

    /**
     * Returns the user to the previous fragment after the Comment has been
     * altered.
     * 
     * @param view
     */
    public void makeEdit(View view) {
        EditCommentFragment.oldText = null;
        EditCommentFragment.oldThumbnail = null;
        editComment.setTextPost(newTextPost.getText().toString());
        ThreadManager.startPost(editComment, null);
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        getFragmentManager().popBackStackImmediate();

    }

}
