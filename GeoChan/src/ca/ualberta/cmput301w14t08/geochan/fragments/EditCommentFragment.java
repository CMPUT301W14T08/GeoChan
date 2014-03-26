package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class EditCommentFragment extends Fragment {
    private Comment editComment;
    private EditText newTextPost;
    
    
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
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState); 
    }
    
    @Override
    public void onStart(){
        super.onStart();
        Bundle bundle = getArguments();
        String commentId = bundle.getString("commentId");
        int threadIndex = bundle.getInt("threadIndex");
        ThreadComment thread = ThreadList.getThreads().get(threadIndex);
        if(thread.getBodyComment().getId().equals(commentId)){
            editComment = thread.getBodyComment();
        } else {
            getCommentFromId(commentId, thread.getBodyComment().getChildren());
        }
        String oldText = editComment.getTextPost();
        TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
        oldTextView.setText(oldText);
        newTextPost = (EditText) getActivity().findViewById(R.id.editBody);
        newTextPost.setText(oldText);
        newTextPost.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public void onResume(){
        super.onResume();
        Bundle args = getArguments();
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

                    locButton
                            .setText("Lat: " + format.format(lat) + ", Lon: " + format.format(lon));
                }
            }
        }
    }
    
    @Override
    public void onPause(){
        super.onPause();
    }
    
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
    
    public void makeEdit(View view){
        editComment.setTextPost(newTextPost.getText().toString());
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        getFragmentManager().popBackStackImmediate();
        
    }
    
    public void changeLocation(View v){
        //Can probably adapt this from PostCommentFragment.
    }

}
