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

import java.io.OutputStream;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;

/**
 * Fragment responsible for displaying an image full screen
 * Image is expanded upon clicking the comment thumbnail
 *
 * @author Artem Chikn
 */
public class ExpandImageFragment extends Fragment {
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_expand_image, container, false);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageView imageView = (ImageView) getView().findViewById(R.id.expanded_image);
        final Bitmap image = CacheManager.getInstance().deserializeImage(id);
        if (image == null) {
            // Start the image getter thread.
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Downloading Image");
            ThreadManager.startGetImage(id, imageView, dialog);
        } else {
            imageView.setImageBitmap(image);
        }
        LinearLayout rlayout = (LinearLayout) getView().findViewById(R.id.expanded_image_relative);
        rlayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        Button saveButton = (Button) getView().findViewById(R.id.save_image_button);
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	ContentValues values = new ContentValues();
                values.put(Images.Media.TITLE, id);
                values.put(Images.Media.DESCRIPTION, id);
                values.put(Images.Media.MIME_TYPE, "image/jpeg");
                values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
                
                Uri uri = null;
                ContentResolver contentResolver = getActivity().getContentResolver();
                try {
	                uri = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	                OutputStream imageOut = contentResolver.openOutputStream(uri);
		            try {
		                image.compress(Bitmap.CompressFormat.JPEG, 90, imageOut);
		            } finally {
		            	imageOut.close();
		            }
	                Toast.makeText(getActivity(), "Saved to gallery.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                	Toaster.toastShort("Failed to save to gallery.");
                    if (uri != null) {
                        contentResolver.delete(uri, null, null);
                        uri = null;
                    }
                }
            }
        });
    }
}