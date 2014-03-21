package ca.ualberta.cmput301w14t08.geochan.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import ca.ualberta.cmput301w14t08.geochan.R;

public class FavouritesFragment extends Fragment implements
ActionBar.OnNavigationListener {
    private static ActionBar actionBar;
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Get which counter requested this activity
        //Intent intent = getIntent();

        // Set up the action bar to show a dropdown list.
        actionBar = getActivity().getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // Show the Up button in the action bar.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1, new String[] {
                    getString(R.string.threads_fav),
                    getString(R.string.comments_fav), }), this);
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                actionBar.setSelectedNavigationItem(
                        savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, actionBar
                .getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        switch(position) {
        case 0:
            Fragment thrFragment = new FavouriteThreadsFragment();
            getChildFragmentManager().beginTransaction()
            .replace(R.id.container, thrFragment, "favThrFragment").commit();
            break;
        case 1:
            Fragment cmtFragment = new FavouriteCommentsFragment();
            getChildFragmentManager().beginTransaction()
            .replace(R.id.container, cmtFragment, "favComFragment").commit();
            break;
        }
        return true;
    }
}