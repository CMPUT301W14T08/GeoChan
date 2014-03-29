package ca.ualberta.cmput301w14t08.geochan.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ca.ualberta.cmput301w14t08.geochan.R;

public class ExpandImageFragment extends Fragment {
    Bitmap image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_expand_image, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ImageView imageView = (ImageView) getView().findViewById(R.id.expanded_image);
        imageView.setImageBitmap(image);
        LinearLayout rlayout = (LinearLayout) getView().findViewById(R.id.expanded_image_relative);
        rlayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
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
        image = bundle.getParcelable("img");
    }

}
