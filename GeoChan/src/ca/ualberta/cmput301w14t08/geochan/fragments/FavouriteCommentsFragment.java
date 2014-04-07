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
import ca.ualberta.cmput301w14t08.geochan.adapters.FavouriteCommentsAdapter;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Fragment for displaying the child comments of a saved thread.
 * 
 * @author Artem Chikin
 * 
 */
public class FavouriteCommentsFragment extends Fragment {
    private static ArrayList<ThreadComment> list;
    private FavouritesLog log;
    private ListView favouritesListView;

    /**
     * Set up the log instance and get the comment list.
     * 
     * @param savedInstanceState The previously saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = FavouritesLog.getInstance(getActivity());
        list = log.getFavComments();
    }

    /**
     * Inflate the fragment view from the given layout.
     * 
     * @param inflater The LayoutInflater used to inflate the fragment's UI.
     * @param contained The parent view that the fragment's UI is attached to.
     * @param savedInstanceState The previously saved state of the fragment.
     * 
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites_list, container, false);
    }

    @Override
    /**
     * Displays a list view of favourites upon starting the fragment.
     * 
     */
    public void onStart() {
        super.onStart();
        favouritesListView = (ListView) getView().findViewById(R.id.favourites_list);
        FavouriteCommentsAdapter adapter = new FavouriteCommentsAdapter(list, getActivity());
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
                bundle.putInt("favCom", -1);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, "thread_view_fragment")
                        .addToBackStack(null).commit();
                // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
                getFragmentManager().executePendingTransactions();
            }
        });
        adapter.notifyDataSetChanged();
    }
}
