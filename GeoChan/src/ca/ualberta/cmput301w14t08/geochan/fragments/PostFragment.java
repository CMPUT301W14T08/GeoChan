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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Responsible for the UI fragment that allows a user to post a new thread.
 * 
 * @author Artem Chikin
 */
public class PostFragment extends Fragment {
    public static final int MAX_BITMAP_DIMENSIONS = 900;

	private LocationListenerService locationListenerService;
	private GeoLocation geoLocation;
	private Bitmap image = null;
	private Bitmap imageThumb = null;
	private File imageFile = null;
	private ThreadComment thread = null;
	private Comment commentToReplyTo = null;

	/**
	 * Initializes several of the member variables used
	 * by PostFragment.
	 * @param savedInstanceState The previously saved state of the fragment.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locationListenerService = new LocationListenerService(getActivity());
		locationListenerService.startListening();
		Bundle args = getArguments();
		if (args.getLong("id") != -1) {
			commentToReplyTo = (Comment) args.getParcelable("cmt");
	        boolean fromFavs = args.getBoolean("fromFavs");
	        if (fromFavs) {
	        	FavouritesLog log = FavouritesLog.getInstance(getActivity());
	            thread = log.getThreads().get((int) args.getLong("id"));
	        } else {
				thread = ThreadList.getThreads().get((int) args.getLong("id"));
	        }
		}
	}

    /**
     * Set up the fragment's UI.
     * 
     * @param inflater The LayoutInflater used to inflate the fragment's UI.
     * @param container The parent View that the  fragment's UI is attached to.
     * @param savedInstanceState The previously saved state of the fragment.
     * @return The View for the fragment's UI.
     * 
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(false);
		if (thread == null) {
			return inflater.inflate(R.layout.fragment_post_thread, container, false);
		} else {
			return inflater.inflate(R.layout.fragment_post_comment, container, false);
		}
	}

	/**
	 * Initializes some UI elements if the user is posting a reply rather than a new ThreadComment.
	 */
	@Override
	public void onStart() {
		super.onStart();
		if (commentToReplyTo != null) {
			TextView replyTo = (TextView) getActivity().findViewById(R.id.comment_replyingTo);
			TextView bodyReplyTo = (TextView) getActivity().findViewById(R.id.reply_to_body);
			bodyReplyTo.setMovementMethod(new ScrollingMovementMethod());
			bodyReplyTo.setText(commentToReplyTo.getTextPost());
			replyTo.setText(commentToReplyTo.getUser() + " says:");
		}
	}

	/**
	 * Resumes the fragment, updating the location and textview states accordingly.
	 */
	@Override
	public void onResume() {
		super.onResume();
		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey("LATITUDE") && args.containsKey("LONGITUDE")) {
				Button locButton = (Button) getActivity().findViewById(R.id.location_button);
				if (args.getString("LocationType") == "CURRENT_LOCATION") {
					locButton.setText("Location: Set");
				} else {
					Double lat = args.getDouble("LATITUDE");
					Double lon = args.getDouble("LONGITUDE");
					geoLocation = new GeoLocation(lat, lon);

					String locationDescription = args.getString("locationDescription");
					geoLocation.setLocationDescription(locationDescription);

					locButton.setText("Location: Set");
				}
			}
			if (args.containsKey("IMAGE_THUMB") && args.containsKey("IMAGE_FULL")) {
				imageThumb = args.getParcelable("IMAGE_THUMB");
				image = args.getParcelable("IMAGE_FULL");
			}
		}
	}

    /**
     * onClick method for the post button. Extracts the textView information,
     * creates the threadComment object and posts it to the server.
     * 
     * @param view
     *            The post button in the PostThreadFragment.
     */
    public void post(View view) {
    	if (geoLocation == null) {
        	geoLocation = new GeoLocation(locationListenerService);
    	}
    	if (geoLocation.getLocation() == null) {
    		ErrorDialog.show(getActivity(), "Could not retrieve location. Please specify a custom location.");
    		return;
    	}
        if (view.getId() == R.id.post_button) {
            String title = null;
            EditText editTitle = null;
            EditText editComment = (EditText) this.getView().findViewById(R.id.commentBody);
            ThreadComment threadComment = null;
            String comment = editComment.getText().toString();
            if (thread == null) {
                editTitle = (EditText) this.getView().findViewById(R.id.titlePrompt);
                title = editTitle.getText().toString();
            }
            if (title != null && title.equals("")) {
                ErrorDialog.show(getActivity(), "Title can not be left blank.");
            } else {
                Comment newComment = new Comment(comment, image, geoLocation, commentToReplyTo);
                if (commentToReplyTo != null) {
                    Comment c = thread.findCommentById(thread.getBodyComment(),
                            commentToReplyTo.getId());
                    c.addChild(newComment);
                    int tag = PreferencesManager.getInstance().getCommentSort();
                    SortUtil.sortComments(tag, thread.getBodyComment().getChildren());
                } else {
                	threadComment = new ThreadComment(newComment, title);
                    CacheManager.getInstance().serializeThreadList(ThreadList.getThreads());
                    int tag = PreferencesManager.getInstance().getThreadSort();
                    SortUtil.sortThreads(tag, ThreadList.getThreads());
                }
            	if (!ConnectivityHelper.getInstance().isConnected()) {
            		CacheManager cacheManager = CacheManager.getInstance();
            		if (title == null) {
            			cacheManager.addCommentToQueue(newComment);
            		} else {
            			cacheManager.addThreadCommentToQueue(threadComment);
            		}
            		Toaster.toastShort("No internet connection detected. Your post will automatically send on connection.");
            	} else {
            		ProgressDialog dialog = new ProgressDialog(getActivity());
            		dialog.setMessage("Getting Location Data");
            		ThreadManager.startPost(newComment, title, geoLocation, dialog, false);
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
	 *            the AttachPhoto button in postThreadFragment.
	 */
	public void attachImage(View view) {
		if (view.getId() == R.id.attach_image_button) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle(R.string.attach_image_title);
			dialog.setMessage(R.string.attach_image_dialog);
			dialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					FavouritesFragment favFrag = (FavouritesFragment) getParentFragment();
					boolean fromFav;
					if(favFrag != null){
						fromFav = true;
					} else {
						fromFav = false;
					}
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					if(fromFav == true){
						arg0.dismiss();
						getParentFragment().startActivityForResult(Intent.createChooser(intent, "Test"),
								ImageHelper.REQUEST_GALLERY);
					} else {
						arg0.dismiss();
						startActivityForResult(Intent.createChooser(intent, "Test"),
								ImageHelper.REQUEST_GALLERY);
					}
				}
			});

			dialog.setNegativeButton("Camera", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					FavouritesFragment favFrag = (FavouritesFragment) getParentFragment();
					boolean fromFav;
					if(favFrag != null){
						fromFav = true;
					} else {
						fromFav = false;
					}
					Intent intent = new Intent();
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					try {
						imageFile = ImageHelper.createImageFile();
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
						if(fromFav == true){
							arg0.dismiss();
							getParentFragment().startActivityForResult(Intent.createChooser(intent, "Test"),
									ImageHelper.REQUEST_CAMERA);
						} else {
							arg0.dismiss();
							startActivityForResult(Intent.createChooser(intent, "Test"),
									ImageHelper.REQUEST_CAMERA);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			dialog.show();
		}
	}

	
	/**
	 * Handles the return from the camera activity
	 * 
	 * @param requestCode Type of activity requested
	 * @param resultCode Code indicating activity success/failure
	 * @param data Image data associated with the camera activity
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ImageHelper.REQUEST_CAMERA) {
				Bitmap imageBitmap = null;
				try {
					imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(Uri.fromFile(imageFile)));
				} catch (FileNotFoundException e) {
					Toaster.toastShort("Error. Could not load image.");
				}
				Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
				image = scaleImage(imageBitmap);
				imageThumb = squareBitmap;
				Bundle bundle = getArguments();
				bundle.putParcelable("IMAGE_THUMB", imageThumb);
				bundle.putParcelable("IMAGE_FULL", image);
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
				Bitmap squareBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
				image = scaleImage(imageBitmap);
				imageThumb = squareBitmap;
				Bundle bundle = getArguments();
				bundle.putParcelable("IMAGE_THUMB", imageThumb);
				bundle.putParcelable("IMAGE_FULL", image);
			}
		}
	}

	/**
	 * Scales a bitmap to a suitable size for display.
	 * @param bitmap The Bitmap to be scaled.
	 * @return The rescaled Bitmap.
	 * 
	 */
	private Bitmap scaleImage(Bitmap bitmap) {
		// https://github.com/bradleyjsimons/PicPoster/blob/master/src/ca/ualberta/cs/picposter/controller/PicPosterController.java
		// Scale the pic if it is too large:
		if (bitmap.getWidth() > MAX_BITMAP_DIMENSIONS || bitmap.getHeight() > MAX_BITMAP_DIMENSIONS) {
			double scalingFactor = bitmap.getWidth() * 1.0 / MAX_BITMAP_DIMENSIONS;
			if (bitmap.getHeight() > bitmap.getWidth())
				scalingFactor = bitmap.getHeight() * 1.0 / MAX_BITMAP_DIMENSIONS;

			int newWidth = (int) Math.round(bitmap.getWidth() / scalingFactor);
			int newHeight = (int) Math.round(bitmap.getHeight() / scalingFactor);

			bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
		}
		return bitmap;
	}

	/**
	 * Stops the locationListenerService from listening.
	 */
	@Override
	public void onStop() {
		super.onStop();
		locationListenerService.stopListening();
	}
}
