package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadListAdapter;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

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
        favouritesListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            /*
             * On click, launch the fragment responsible for thread viewing
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ThreadViewFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("thread", list.get((int) id));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "thread_view_fragment")
                        .addToBackStack("thread_view_fragment").commit();
                // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
                getFragmentManager().executePendingTransactions();
            }
        });
        adapter.notifyDataSetChanged();
    }
}


