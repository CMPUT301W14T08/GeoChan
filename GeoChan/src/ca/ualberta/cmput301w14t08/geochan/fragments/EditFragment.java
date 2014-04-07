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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
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
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Allows the user to edit the text, location, and image of a comment
 * they have made. The user can only edit a comment that they have made if
 * their current hash (based on device ID and current username) matches the
 * hash stored in the Comment being edited.
 * 
 * @author Henry Pabst
 * 
 */
public class EditFragment extends Fragment {
    private static final int MAX_BITMAP_DIMENSIONS = 600;
    private Comment editComment;
    private ThreadComment thread;
    private EditText newTextPost;
    /**
     * The original thumbnail of the Comment being edited. Contained as a variable in
     * EditCommentFragment so that it persists and does not change as the user alters the
     * original Comment.
     */
    private static Bitmap oldThumbnail;
    /**
     * The original text of the Comment being edited. Contained as a variable in
     * EditCommentFragment so that it persists and does not change as the user alters the
     * original Comment.
     */
    private static String oldText;
    private boolean isThread;
    private File imageFile;

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

    
    /**
     * Overriden method from Fragment. Determines the Comment being edited based on the id
     * contained in fragment arguments as well as the ThreadComment containing said Comment.
     * After the Comment is found the appropriate UI elements and state variables are set.
     */

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        String commentId = bundle.getString("commentId");
        int threadIndex = bundle.getInt("threadIndex");
        boolean fromFavs = bundle.getBoolean("fromFavs");
        if(fromFavs == true){
            FavouritesLog log = FavouritesLog.getInstance(getActivity());
            thread = log.getThreads().get(threadIndex);
        } else {
            thread = ThreadList.getThreads().get(threadIndex);
        }
        if (thread.getBodyComment().getId().equals(commentId)){
            editComment = thread.getBodyComment();
            isThread = true;
        } else {
            getCommentFromId(commentId, thread.getBodyComment().getChildren());
            isThread = false;
        }
        if (EditFragment.oldText == null){
            Log.e("DEBUG","comment not null" + editComment.toString());
            EditFragment.oldText = editComment.getTextPost();
            TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
            oldTextView.setText(EditFragment.oldText);
        }
        if (EditFragment.oldThumbnail == null && editComment.getImageThumb() != null) {
            EditFragment.oldThumbnail = editComment.getImageThumb();
            //oldThumbView = (ImageView) getActivity().findViewById(R.id.old_thumb);
            //oldThumbView.setImageBitmap(EditFragment.oldThumbnail);
        }
        newTextPost = (EditText) getActivity().findViewById(R.id.editBody);
        newTextPost.setText(editComment.getTextPost());
        newTextPost.setMovementMethod(new ScrollingMovementMethod());
    }

    
    /**
     * Overriden method from Fragment. Sets the appropriate TextView and ImageView
     * if the user is returning from changing the location or image. If the user is
     * returning from changing the location, the new coordinates are placed on the
     * edit_location_button Button.
     */

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (EditFragment.oldText != null) {
            TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
            oldTextView.setText(EditFragment.oldText);
        }
        if (EditFragment.oldThumbnail != null){
            //oldThumbView = (ImageView) getActivity().findViewById(R.id.old_thumb);
            //oldThumbView.setImageBitmap(EditFragment.oldThumbnail);
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
     * Recursively finds the Comment with the passed ID and sets it to
     * the variable editComment. If a Comment with the passed ID is not
     * found in the passed ArrayList of Comments then the method is recursively
     * called on the children of Comments in the ArrayList.
     * @param id The ID of the comment to be found.
     * @param comments An ArrayList of Comments to start searching in.
     */ 
    public void getCommentFromId(String id, ArrayList<Comment> comments){
        for(Comment com: comments){
            if(com.getId().equals(id)){
                editComment = com;
                return;
            } else {
                getCommentFromId(id, com.getChildren());
            }
        }
        return;
    }

    /**
     * Allows the user to change the image attached to their comment or remove it
     * entirely. Prompts the user with an AlertDialog as to which option they would like
     * to select. 
     * @param view The Button pressed to call editImage.
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
                    startActivityForResult(Intent.createChooser(intent, "Test"),
                            ImageHelper.REQUEST_GALLERY);
                }
            });
            dialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent();
                    intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        imageFile = ImageHelper.createImageFile();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
                        startActivityForResult(Intent.createChooser(intent, "Test"),
                                ImageHelper.REQUEST_CAMERA);
                    } catch (IOException e) {
                        e.printStackTrace();
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
     * Overriden method from Fragment. Sets the image and thumbnail in the comment being
     * edited to the user selected image. Is called automatically after the user
     * returns from selecting an image from either the camera or photo gallery.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImageHelper.REQUEST_CAMERA) {
				Bitmap imageBitmap = null;
				try {
					imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(Uri.fromFile(imageFile)));
				} catch (FileNotFoundException e) {
					Toaster.toastShort("Error. Could not load image.");
				}
                image = scaleImage(imageBitmap);
            } else if (requestCode == ImageHelper.REQUEST_GALLERY) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getActivity()
                            .getContentResolver(), data.getData());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image = scaleImage(imageBitmap);
            }
        }
        editComment.setImage(image);
        Bitmap imageThumb = ThumbnailUtils.extractThumbnail(image, 96, 96);
        editComment.setImageThumb(imageThumb);
    }
    
    /**
     * Scales the passed bitmap down so that it does not exceed the
     * maximum dimensions specified in EditCommentFragment.MAX_BITMAP_DIMENSIONS.
     * @param bitmap The bitmap that is to be scaled down.
     * @return A new Bitmap that is a scaled down version of the passed Bitmap.
     */
    private Bitmap scaleImage(Bitmap bitmap) {
        if (bitmap.getWidth() > MAX_BITMAP_DIMENSIONS
                || bitmap.getHeight() > MAX_BITMAP_DIMENSIONS) {
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
     * Sets the text of the comment being edited to the new text entered by the user,
     * sets the value of EditCommentFragment.oldText and EditCommentFragment.oldThumbnail
     * to null so that the state isn't preserved across comment edits, and returns the
     * user to their previous fragment.
     * @param view The button that was pressed to call makeEdit.
     */
    public void makeEdit(View view) {
        EditFragment.oldText = null;
        EditFragment.oldThumbnail = null;
        editComment.setTextPost(newTextPost.getText().toString());
        ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Getting Location Data");
        if (isThread) {
        	String threadTitle = thread.getTitle();
        	thread.setBodyComment(editComment);
            ThreadManager.startPost(editComment, threadTitle, editComment.getLocation(), dialog);
            CacheManager.getInstance().serializeThreadList(ThreadList.getThreads());
        } else {
            ThreadManager.startPost(editComment, null, editComment.getLocation(), dialog);
        }
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        getFragmentManager().popBackStackImmediate();

    }

}
