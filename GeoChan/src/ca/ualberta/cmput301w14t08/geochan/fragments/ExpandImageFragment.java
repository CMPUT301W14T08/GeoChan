package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;

/**
 * Fragment responsible for displaying an image full screen
 * Image is expanded upon clicking the comment thumbnail
 *
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
            	MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), image, id+".jpeg" , id + "image");
                Toast.makeText(getActivity(), "Saved to gallery.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
