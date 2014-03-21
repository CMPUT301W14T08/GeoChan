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

public class FavouriteCommentsFragment extends Fragment {
    ArrayList<Comment> list;
    FavouritesLog log;
    ListView favouritesListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = FavouritesLog.getInstance(getActivity());
        list = log.getComments();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_favourites_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        favouritesListView = (ListView) getView().findViewById(R.id.favourites_list);
        FavouriteCommentsAdapter adapter = new FavouriteCommentsAdapter(list, getActivity());
        // Assign custom adapter to the thread listView.
        favouritesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
