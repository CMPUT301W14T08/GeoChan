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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadListAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * Responsible for the UI fragment that displays multiple ThreadComments to the
 * user.
 * 
 * @author Henry Pabst, Artem Chikin
 * 
 */
public class ThreadListFragment extends Fragment {
    private PullToRefreshListView threadListView;
    private ThreadListAdapter adapter;
    private LocationListenerService locationListener = null;
    private CacheManager cacheManager = null;
    private ConnectivityHelper connectHelper = null;
    private PreferencesManager prefManager = null;
    private static int locSortFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // locationListener = new LocationListenerService(getActivity());
        adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
        setHasOptionsMenu(true);
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading Threads.");
        ThreadManager.startGetThreadComments(this, dialog);
    }

    /**
     * Starts the location listener listening. If we're sorting threads by a
     * user-entered location, locSortFlag will be set to 1, so we sort according
     * to the specified location and set the flag back to 0.
     */
    @Override
    public void onResume() {
        if (locSortFlag == 1) {
            prefManager.setThreadSort(SortUtil.SORT_LOCATION);
            SortUtil.sortThreads(SortUtil.SORT_LOCATION, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            locSortFlag = 0;
        }
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectHelper = ConnectivityHelper.getInstance();
        if (!connectHelper.isConnected()) {
            Toaster.toastShort("No network connection.");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.thread_list, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
    }

    /**
     * Determines which sorting method was selected and calls the appropriate
     * sorting method on our list of threads.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.thread_sort_date_new:
            // User wants to push new threads to the top.
            prefManager.setThreadSort(SortUtil.SORT_DATE_NEWEST);
            SortUtil.sortThreads(SortUtil.SORT_DATE_NEWEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_date_old:
            // User wants to push old threads to the top.
            prefManager.setThreadSort(SortUtil.SORT_DATE_OLDEST);
            SortUtil.sortThreads(SortUtil.SORT_DATE_OLDEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_score_high:
            // User wants threads with high relevance/score at the top.
            prefManager.setThreadSort(SortUtil.SORT_USER_SCORE_HIGHEST);
            SortUtil.setThreadSortGeo(new GeoLocation(locationListener));
            SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_HIGHEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_score_low:
            // User wants threads with low relevance/score at the top.
            prefManager.setThreadSort(SortUtil.SORT_USER_SCORE_LOWEST);
            SortUtil.setThreadSortGeo(new GeoLocation(locationListener));
            SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_LOWEST, ThreadList.getThreads());
            adapter.notifyDataSetChanged();
            return true;
        case R.id.thread_sort_location:
            // User wants threads close to a selected location at the top.
            locSortFlag = 1;
            this.getSortingLoc();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets the fragment's locSortFlag to 1 so sorting is done in onResume then
     * sends the user to a CustomLocationFragment to enter the location they
     * want to sort according to.
     */
    private void getSortingLoc() {
        Bundle args = new Bundle();
        args.putInt("postType", CustomLocationFragment.SORT_THREAD);
        CustomLocationFragment frag = new CustomLocationFragment();
        frag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, "customLocFrag").addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    /**
     * Initializes our fragment with various variables, displays the threads,
     * sets up a onItemClickListener so the user is sent to the appropriate
     * thread when they click on it, then sorts the threads according to the
     * method the user has chosen.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (locationListener == null) {
            locationListener = new LocationListenerService(getActivity());
        }
        if (prefManager == null) {
            prefManager = PreferencesManager.getInstance();
        }
        if (cacheManager == null) {
            cacheManager = CacheManager.getInstance();
        }
        threadListView = (PullToRefreshListView) getActivity().findViewById(R.id.thread_list);
        // On start, get the threadList from the cache
        ArrayList<ThreadComment> list = cacheManager.deserializeThreadList();
        ThreadList.setThreads(list);
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
                bundle.putParcelable("thread", ThreadList.getThreads().get((int) id));
                bundle.putLong("id", id);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, "thread_view_fragment")
                        .addToBackStack("thread_view_fragment").commit();
                // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
                getFragmentManager().executePendingTransactions();
            }
        });
        int sort = prefManager.getThreadSort();
        SortUtil.sortThreads(sort, ThreadList.getThreads());
        adapter.notifyDataSetChanged();
        
        threadListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!connectHelper.isConnected()) {
                    Toaster.toastShort("No network connection.");
                    threadListView.onRefreshComplete();
                    //onLoadFinished(loader, ThreadList.getThreads());
                } else {
                    reload();
                }
            }
        });

    }

    public void reload() {
        ThreadManager.startGetThreadComments(this, null);
    }
    
    public void finishReload() {
        cacheManager.serializeThreadList(ThreadList.getThreads());
        SortUtil.sortThreads(prefManager.getThreadSort(), ThreadList.getThreads());
        adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
        // Assign custom adapter to the thread listView.
        threadListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        threadListView.onRefreshComplete();
    }
}
