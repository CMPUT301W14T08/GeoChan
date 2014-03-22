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

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadViewAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

/**
 * Fragment which displays the contents of a ThreadComment.
 */
public class ThreadViewFragment extends Fragment implements LoaderCallbacks<ArrayList<Comment>> {
    private ListView threadView;
    private ThreadViewAdapter adapter;
    private ThreadComment thread = null;
    private LocationListenerService locationListener = null;
    private PreferencesManager prefManager = null;
    private static int locSortFlag = 0;
    
    @Override
    public void onResume(){
        setHasOptionsMenu(true);
        if(locationListener == null){
            locationListener = new LocationListenerService(getActivity());
        }
        if(prefManager == null){
            prefManager = PreferencesManager.getInstance();
        }
        if(locSortFlag == 1){
            prefManager.setCommentSort(SortUtil.SORT_LOCATION);
            SortUtil.sortComments(SortUtil.SORT_LOCATION,
                                  thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            locSortFlag = 0;
        }
        locationListener.startListening();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        final int id = (int) bundle.getLong("id");
        thread = ThreadList.getThreads().get(id);
        getLoaderManager().restartLoader(CommentLoader.LOADER_ID, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.thread_view, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        final int id = (int) bundle.getLong("id");
        ThreadComment thread = ThreadList.getThreads().get(id);
        threadView = (ListView) getView().findViewById(R.id.thread_view_list);
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
        // Assign custom adapter to the thread listView.
        threadView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
        case(R.id.comment_sort_date_new):
            prefManager.setCommentSort(SortUtil.SORT_DATE_NEWEST);
            SortUtil.sortComments(SortUtil.SORT_DATE_NEWEST, 
                                thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        case(R.id.comment_sort_date_old):
            prefManager.setCommentSort(SortUtil.SORT_DATE_OLDEST);
            SortUtil.sortComments(SortUtil.SORT_DATE_OLDEST, 
                                thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        case(R.id.comment_sort_image):
            prefManager.setCommentSort(SortUtil.SORT_IMAGE);
            SortUtil.sortComments(SortUtil.SORT_IMAGE, 
                                  thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        case(R.id.comment_sort_location_current):
            prefManager.setCommentSort(SortUtil.SORT_LOCATION);
            SortUtil.setCommentSortGeo(new GeoLocation(locationListener.getCurrentLocation()));
            SortUtil.sortComments(SortUtil.SORT_LOCATION,
                                  thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return true;
        case(R.id.comment_sort_location_other):
            locSortFlag = 1;
            this.getSortingLoc();
            return true;
        case(R.id.comment_sort_score_high):
             prefManager.setCommentSort(SortUtil.SORT_USER_SCORE_HIGHEST);
             SortUtil.setCommentSortGeo(new GeoLocation(locationListener));
             SortUtil.sortComments(SortUtil.SORT_USER_SCORE_HIGHEST,
                                   thread.getBodyComment().getChildren());
             adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
             threadView.setAdapter(adapter);
             adapter.notifyDataSetChanged();
             return true;
        case(R.id.comment_sort_score_low):
             prefManager.setCommentSort(SortUtil.SORT_USER_SCORE_LOWEST);
             SortUtil.setCommentSortGeo(new GeoLocation(locationListener));
             SortUtil.sortComments(SortUtil.SORT_USER_SCORE_LOWEST,
                                   thread.getBodyComment().getChildren());
             adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
             threadView.setAdapter(adapter);
             adapter.notifyDataSetChanged();
             return true;
         default:
             return super.onOptionsItemSelected(item);
        }     
    }
    
    private void getSortingLoc(){
        Bundle args = new Bundle();
        args.putInt("postType", CustomLocationFragment.SORT_COMMENT);
        CustomLocationFragment frag = new CustomLocationFragment();
        frag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, "customLocFrag").addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     */
    @Override
    public Loader<ArrayList<Comment>> onCreateLoader(int id, Bundle args) {
        return new CommentLoader(getActivity(), thread);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.content
     * .Loader, java.lang.Object)
     */
    @Override
    public void onLoadFinished(Loader<ArrayList<Comment>> loader, ArrayList<Comment> list) {
        thread.getBodyComment().setChildren(list);
        
        /*
         *  Have to reset adapter: workaround for a strange issue, for description,
         *  see : http://stackoverflow.com/questions/20512068/listview-not-updating-properly-cursoradapter-after-swapcursor
         */
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager());
        threadView.setAdapter(adapter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.content
     * .Loader)
     */
    @Override
    public void onLoaderReset(Loader<ArrayList<Comment>> loader) {
        //
    }
}
