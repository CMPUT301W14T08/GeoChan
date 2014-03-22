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

/**
 * This fragment is used to display a list of threads,
 * which is the main view the user sees when they open the app. 
 */
import java.util.ArrayList;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadListAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.loaders.ThreadCommentLoader;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Responsible for the UI fragment that displays multiple ThreadComments to the
 * user.
 * 
 */
public class ThreadListFragment extends Fragment implements
        LoaderCallbacks<ArrayList<ThreadComment>> {
    private ListView threadListView;
    private ThreadListAdapter adapter;
    private LocationListenerService locationListener = null;
   // private GeoLocation sortGeo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //locationListener = new LocationListenerService(getActivity());
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onResume(){
        if(locationListener == null){
            locationListener = new LocationListenerService(getActivity());
        }
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ThreadCommentLoader.LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.thread_list, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case R.id.thread_sort_date_new:
            SortUtil.sortThreads(SortUtil.SORT_DATE_NEWEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_date_old:
            Log.e("sorting by date old in threadListFragment","");
            SortUtil.sortThreads(SortUtil.SORT_DATE_OLDEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_score_high:
            SortUtil.setThreadSortGeo(new GeoLocation(locationListener.getCurrentLocation()));
            SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_HIGHEST,
                                ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_score_low:
            SortUtil.setThreadSortGeo(new GeoLocation(locationListener.getCurrentLocation()));
            SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_LOWEST,
                                ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_location_current:
            SortUtil.setThreadSortGeo(new GeoLocation(locationListener.getCurrentLocation()));
            SortUtil.sortThreads(SortUtil.SORT_LOCATION,
                                ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            //Sorting stuff for sorting by location here.
            return true;
        case R.id.thread_sort_location_other:
            //Sorting stuff for getting a location and sorting here.
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    

    @Override
    public void onStart() {
        super.onStart();
        threadListView = (ListView) getActivity().findViewById(R.id.thread_list);
        adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
        threadListView.setEmptyView(getActivity().findViewById(R.id.empty_list_view));
        threadListView.setAdapter(adapter);
        threadListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            /*
             * On click, launch the fragment responsible for thread viewing
             */
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ThreadViewFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("id", id);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, "thread_view_fragment")
                        .addToBackStack("thread_view_fragment").commit();
                // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
                getFragmentManager().executePendingTransactions();
            }
        });
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int sort = pref.getInt("sortThreads", SortUtil.SORT_DATE_NEWEST);
        //ThreadList.sortThreads(sort);
        //Will need to figure out sortLoc here somehow.
        //SortUtil.sortThreads(sort, ThreadList.getThreads(), sortLoc);
        adapter.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     */
    @Override
    public Loader<ArrayList<ThreadComment>> onCreateLoader(int id, Bundle args) {
        return new ThreadCommentLoader(getActivity());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content
     * .Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<ThreadComment>> loader,
            ArrayList<ThreadComment> list) {
        ThreadList.setThreads(list);
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content
     * .Loader)
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<ThreadComment>> loader) {
        adapter.setList(new ArrayList<ThreadComment>());
    }
}
