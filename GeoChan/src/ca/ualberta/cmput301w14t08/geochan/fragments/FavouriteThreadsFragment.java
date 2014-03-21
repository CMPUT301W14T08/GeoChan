package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadListAdapter;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

public class FavouriteThreadsFragment extends Fragment {
    ArrayList<ThreadComment> list;
    FavouritesLog log;
    ListView favouritesListView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = FavouritesLog.getInstance(getActivity());
        list = log.getThreads();
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
        ThreadListAdapter adapter = new ThreadListAdapter(getActivity(), list);
        // Assign custom adapter to the thread listView.
        favouritesListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}


