package ca.ualberta.cmput301w14t08.geochan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.FavouriteCommentsFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.FavouriteThreadsFragment;

public class FavouritesActivity extends Activity implements
ActionBar.OnNavigationListener {
    
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        //Get which counter requested this activity
        //Intent intent = getIntent();

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
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
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
                .getSelectedNavigationIndex());
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        switch(position) {
        case 0:
            Fragment thrFragment = new FavouriteThreadsFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, thrFragment).commit();
            break;
        case 1:
            Fragment cmtFragment = new FavouriteCommentsFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.container, cmtFragment).commit();
            break;
        }
        return true;
    }
}
