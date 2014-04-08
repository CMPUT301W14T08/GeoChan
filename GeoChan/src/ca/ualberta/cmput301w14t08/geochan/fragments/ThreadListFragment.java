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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityBroadcastReceiver;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.interfaces.UpdateDialogListenerInterface;
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
 * @author Henry Pabst
 * @author Artem Chikin
 * 
 */
public class ThreadListFragment extends Fragment implements
		UpdateDialogListenerInterface {
	private BroadcastReceiver updateReceiver;
	private PullToRefreshListView threadListView;
	private ThreadListAdapter adapter;
	private LocationListenerService locationListener = null;
	private CacheManager cacheManager = null;
	private ConnectivityHelper connectHelper = null;
	private PreferencesManager prefManager = null;
	private static boolean refresh = false;
	private static int locSortFlag = 0;

	/**
	 * Set up the fragment UI.
	 * 
	 * @param inflater
	 *            The LayoutInflater used to inflate the fragment's UI.
	 * @param container
	 *            The parent View that the fragment's UI is attached to.
	 * @param savedInstanceState
	 *            The previously saved state of the fragment.
	 * @return The View for the fragment's UI.
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater
				.inflate(R.layout.fragment_thread_list, container, false);
	}

	/**
	 * Sets the fragment's adapter and starts a thread of execution to retrieve
	 * ThreadComments from ElasticSearch.
	 * 
	 * @param savedInstanceState
	 *            The previously saved state of the fragment.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
		if(prefManager == null){
			prefManager = PreferencesManager.getInstance();
		}
		setHasOptionsMenu(true);
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
			SortUtil.sortThreads(SortUtil.SORT_LOCATION,
					ThreadList.getThreads());
			adapter.notifyDataSetChanged();
			locSortFlag = 0;
		}
		adapter.notifyDataSetChanged();
		super.onResume();
	}
	
	/**
	 *Checks the proper sort option in our options menu.
	 *@param menu The fragment's menu.
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu){
		int sortType = prefManager.getThreadSort();
		setSortCheck(sortType, menu);
		super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Checks if there is a network connection for pulling ThreadComments from
	 * ElasticSearch.
	 * 
	 * @param savedInstanceState
	 *            The previously saved state of the fragment.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		connectHelper = ConnectivityHelper.getInstance();
		if (!connectHelper.isConnected()) {
			Toaster.toastShort("No network connection.");
		}
	}

	/**
	 * Initialize's the ThreadList's menu.
	 * 
	 * @param menu
	 *            The Menu of the fragment.
	 * @param inflater
	 *            A MenuInflater to inflate the fragment's menu.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.thread_list, menu);
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setVisible(true);
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
				.replace(R.id.fragment_container, frag, "customLocFrag")
				.addToBackStack(null).commit();
		getFragmentManager().executePendingTransactions();
	}
	
	/**
	 * Sets the user selected sorting option in our options menu.
	 * @param sort Code for the type of sort being used.
	 * @param menu The fragment's menu.
	 */
	private void setSortCheck(int sort, Menu menu){
		MenuItem item;
		switch(sort){
		case SortUtil.SORT_DATE_NEWEST:
			item = menu.findItem(R.id.thread_sort_date_new);
			item.setChecked(true);
			return;
		case SortUtil.SORT_DATE_OLDEST:
			item = menu.findItem(R.id.thread_sort_date_new);
			item.setChecked(true);
			return;
		case SortUtil.SORT_LOCATION:
			item = menu.findItem(R.id.thread_sort_location);
			item.setChecked(true);
			return;
		case SortUtil.SORT_USER_SCORE_HIGHEST:
			item = menu.findItem(R.id.thread_sort_score_high);
			item.setChecked(true);
			return;
		case SortUtil.SORT_USER_SCORE_LOWEST:
			item = menu.findItem(R.id.thread_sort_score_low);
			item.setChecked(true);
			return;
		default:
			return;
		}
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
		threadListView = (PullToRefreshListView) getActivity().findViewById(
				R.id.thread_list);
		// On start, get the threadList from the cache
		ArrayList<ThreadComment> list = cacheManager.deserializeThreadList();
		ThreadList.setThreads(list);
		adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
		threadListView.setEmptyView(getActivity().findViewById(
				R.id.empty_list_view));
		threadListView.setAdapter(adapter);

		threadListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			/*
			 * On click, launch the fragment responsible for thread viewing
			 */
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Fragment fragment = new ThreadViewFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable("thread",
						ThreadList.getThreads().get((int) id));
				bundle.putLong("id", id);
				fragment.setArguments(bundle);
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_container, fragment,
								"thread_view_fragment")
						.addToBackStack("thread_view_fragment").commit();
				getFragmentManager().executePendingTransactions();
			}
		});
		
		int sort = prefManager.getThreadSort();
		SortUtil.sortThreads(sort, ThreadList.getThreads());
		//setSortCheck(sort);
		adapter.notifyDataSetChanged();

		threadListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				if (!connectHelper.isConnected()) {
					Toaster.toastShort("No network connection.");
					threadListView.onRefreshComplete();
				} else {
					reload();
				}
			}
		});

		if (!refresh && connectHelper.isConnected()) {
			threadListView.setRefreshing();
			ThreadManager.startGetThreadComments(this);
			refresh = true;
		}

		updateReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (isVisible() && connectHelper.getWasNotConnected() == true) {
					connectHelper.setWasNotConnected(false);
					UpdateDialogFragment fragment = new UpdateDialogFragment();
					fragment.show(getFragmentManager(), "updateDialogFrag");
				}
			}

		};

		getActivity()
				.getApplicationContext()
				.registerReceiver(
						updateReceiver,
						new IntentFilter(
								ConnectivityBroadcastReceiver.UPDATE_FROM_SERVER_INTENT));
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
			item.setChecked(true);
			prefManager.setThreadSort(SortUtil.SORT_DATE_NEWEST);
			SortUtil.sortThreads(SortUtil.SORT_DATE_NEWEST,
					ThreadList.getThreads());
			adapter.notifyDataSetChanged();
			return true;
		case R.id.thread_sort_date_old:
			// User wants to push old threads to the top.
			item.setChecked(true);
			prefManager.setThreadSort(SortUtil.SORT_DATE_OLDEST);
			SortUtil.sortThreads(SortUtil.SORT_DATE_OLDEST,
					ThreadList.getThreads());
			adapter.notifyDataSetChanged();
			return true;
		case R.id.thread_sort_score_high:
			// User wants threads with high relevance/score at the top.
			item.setChecked(true);
			prefManager.setThreadSort(SortUtil.SORT_USER_SCORE_HIGHEST);
			SortUtil.setThreadSortGeo(new GeoLocation(locationListener));
			SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_HIGHEST,
					ThreadList.getThreads());
			adapter.notifyDataSetChanged();
			return true;
		case R.id.thread_sort_score_low:
			// User wants threads with low relevance/score at the top.
			item.setChecked(true);
			prefManager.setThreadSort(SortUtil.SORT_USER_SCORE_LOWEST);
			SortUtil.setThreadSortGeo(new GeoLocation(locationListener));
			SortUtil.sortThreads(SortUtil.SORT_USER_SCORE_LOWEST,
					ThreadList.getThreads());
			adapter.notifyDataSetChanged();
			return true;
		case R.id.thread_sort_location:
			// User wants threads close to a selected location at the top.
			item.setChecked(true);
			locSortFlag = 1;
			this.getSortingLoc();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Starts a thread of execution to retrieve updated ThreadComments from
	 * ElasticSearch.
	 */
	@Override
	public void reload() {
		ThreadManager.startGetThreadComments(this);
	}

	/**
	 * Stores the retrieved ThreadComments in cache in case connection dies,
	 * applies the current sorting method to the newly retrieved ThreadComments,
	 * and refreshes the adapter so that they display properly.
	 */
	public void finishReload() {
		cacheManager.serializeThreadList(ThreadList.getThreads());
		SortUtil.sortThreads(prefManager.getThreadSort(),
				ThreadList.getThreads());
		adapter = new ThreadListAdapter(getActivity(), ThreadList.getThreads());
		// Assign custom adapter to the thread listView.
		threadListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		threadListView.onRefreshComplete();
	}

}
