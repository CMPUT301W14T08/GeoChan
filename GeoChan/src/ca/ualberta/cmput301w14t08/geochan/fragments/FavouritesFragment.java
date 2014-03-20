package ca.ualberta.cmput301w14t08.geochan.fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import ca.ualberta.cmput301w14t08.geochan.R;

public class FavouritesFragment extends Fragment implements ActionBar.OnNavigationListener {
    //private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
        // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1,
                        new String[] { getString(R.string.threads_fav),
                                getString(R.string.comments_fav), }), this);
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view. Do this by inflating appropriate fragment.

        // getFragmentManager().beginTransaction()
        // .replace(R.id.container, fragment).commit();
        return true;
    }

}
