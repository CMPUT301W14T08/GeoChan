package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.FavouriteCommentsAdapter;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;

/**
 * COMMENT EXPLAINING THE CLASS HERE
 * @author AUTHOR HERE
 *
 */
public class FavouriteCommentsFragment extends Fragment {
    private static ArrayList<Comment> list;
    private FavouritesLog log;
    private ListView favouritesListView;


    /**
     * COMMENT HERE
     * 
     * @author AUTHOR HERE
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = FavouritesLog.getInstance(getActivity());
        list = log.getComments();
    }

    /**
     * COMMENT HERE
     * 
     * @author AUTHOR HERE
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_favourites_list, container, false);
    }

    @Override
    /**
     * Displays a list view of favourites upon starting the fragment.
     * 
     * @author AUTHOR HERE
     */
    public void onStart() {
        super.onStart();
        favouritesListView = (ListView) getView().findViewById(R.id.favourites_list);
        FavouriteCommentsAdapter adapter = new FavouriteCommentsAdapter(list, getActivity());
        // Assign custom adapter to the thread listView.
        favouritesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
