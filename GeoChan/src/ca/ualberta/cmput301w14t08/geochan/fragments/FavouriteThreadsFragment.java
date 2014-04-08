/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadListAdapter;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Display the user's favourited threadComments in a list using
 * ThreadListAdapter. Allow user to browse and enter threadComments.
 * 
 * @author Artem Chikin
 * 
 */
public class FavouriteThreadsFragment extends Fragment {
    private ArrayList<ThreadComment> list;
    private FavouritesLog log;
    private ListView favouritesListView;

    /**
     * Initializes the log and list member variables.
     * @param savedInstanceState The previously saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = FavouritesLog.getInstance(getActivity());
        list = log.getThreads();
    }
    /**
     * Set up the fragment's View.
     * 
     * @param inflater The LayoutInflater used to inflate the fragment's UI.
     * @param container The parent View that the  fragment's UI is attached to.
     * @param savedInstanceState The previously saved state of the fragment.
     * @return The View for the fragment's UI.
     * 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites_list, container, false);
    }

    /**
     * Set up the listView, adapter and listen for list item clicks.
     */
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
                        .addToBackStack(null).commit();
                getFragmentManager().executePendingTransactions();
            }
        });
        adapter.notifyDataSetChanged();
    }
}
