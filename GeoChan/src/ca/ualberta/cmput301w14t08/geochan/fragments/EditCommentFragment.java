package ca.ualberta.cmput301w14t08.geochan.fragments;

import ca.ualberta.cmput301w14t08.geochan.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class EditCommentFragment extends Fragment {
    
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
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public void onPause(){
        super.onPause();
    }
    
    public void makeEdit(View v){
        
    }
    
    public void changeLocation(View v){
        //Can probably adapt this from PostCommentFragment.
    }

}
