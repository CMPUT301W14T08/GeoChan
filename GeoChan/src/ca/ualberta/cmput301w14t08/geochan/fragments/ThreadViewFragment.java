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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadViewAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

/**
 * Fragment which displays the contents of a ThreadComment.
 * 
 * @author Henry Pabst, Artem Chikin
 */
public class ThreadViewFragment extends Fragment {
    private PullToRefreshListView threadView;
    private ThreadViewAdapter adapter;
    private int threadIndex;
    private CacheManager cache = null;
    private ThreadComment thread = null;
    private ConnectivityHelper connectHelper = null;
    private LocationListenerService locationListener = null;
    private PreferencesManager prefManager = null;
    private int container;
    private int isFavCom;
    private static int locSortFlag = 0;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        threadIndex = (int) bundle.getLong("id");
        int isFavCom = bundle.getInt("favCom");
        thread = bundle.getParcelable("thread");
        // Assign custom adapter to the thread listView.
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
        connectHelper = ConnectivityHelper.getInstance();
        cache = CacheManager.getInstance();
        if (!connectHelper.isConnected()) {
            Toaster.toastShort("No network connection.");
            ArrayList<Comment> comments = cache.deserializeThreadCommentById(thread.getId());
            if (comments != null) {
                thread.getBodyComment().setChildren(comments);
            }
        } else if (isFavCom != -1) {
            // Load comments with dialog
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading comments.");
            ThreadManager.startGetComments(this, threadIndex, dialog);
        }
    }

    /**
     * Initializes several of the variables used in displaying the contents of a
     * thread. If the user just returned from selecting a custom location to
     * sort by, it sorts the comments accordingly and resets locSortFlag.
     */
    @Override
    public void onResume() {
        setHasOptionsMenu(true);
        if (locationListener == null) {
            locationListener = new LocationListenerService(getActivity());
        }
        if (prefManager == null) {
            prefManager = PreferencesManager.getInstance();
        }
        if (locSortFlag == 1) {
            prefManager.setCommentSort(SortUtil.SORT_LOCATION);
            SortUtil.sortComments(SortUtil.SORT_LOCATION, thread.getBodyComment().getChildren());
            adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(),
                    threadIndex);
            threadView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            locSortFlag = 0;
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.thread_view, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Set up the ListView, adapter, listeners.
     */
    @Override
    public void onStart() {
        super.onStart();
        threadView = (PullToRefreshListView) getView().findViewById(R.id.thread_view_list);
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
        threadView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        threadView.setOnItemClickListener(commentButtonListener);
        threadView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	if (!connectHelper.isConnected()) {
                    Toaster.toastShort("No network connection.");
                    threadView.onRefreshComplete();
                    //onLoadFinished(loader, ThreadList.getThreads());
                } else if (isFavCom == -1) {
                	threadView.onRefreshComplete();
                } else {
                    reload();
                }
            }
        });
        Fragment fav = getFragmentManager().findFragmentByTag("favThrFragment");
        if (fav != null) {
            container = R.id.container;
        } else {
            container = R.id.fragment_container;
        }
    }

    /**
     * When comment is selected, additional information is displayed in the form
     * of location coordinates. This method sets that location field TextView in
     * the view.
     * 
     * @param view
     * @param comment
     */
    public void setLocationField(View view, Comment comment) {
        // Comment location
        TextView replyLocationText = (TextView) view
                .findViewById(R.id.thread_view_comment_location);
        GeoLocation repLocCom = comment.getLocation();

        if (repLocCom != null) {
            if (repLocCom.getLocationDescription() != null) {
                replyLocationText.setText("near: " + repLocCom.getLocationDescription());
            } else {
                DecimalFormat format = new DecimalFormat();
                format.setRoundingMode(RoundingMode.HALF_EVEN);
                format.setMinimumFractionDigits(0);
                format.setMaximumFractionDigits(4);

                replyLocationText.setText("Latitude: " + format.format(repLocCom.getLatitude())
                        + " Longitude: " + format.format(repLocCom.getLongitude()));
            }
        } else {
            replyLocationText.setText("Error: No location found");
        }
    }

    /**
     * Called when the star button is pressed in the selected comment. Save the
     * comment as favourite.
     * 
     * @param comment
     */
    public void favouriteAComment(Comment comment) {
        Toast.makeText(getActivity(), "Comment saved to Favourites.", Toast.LENGTH_SHORT)
        		.show();
        FavouritesLog log = FavouritesLog.getInstance(getActivity());
        ThreadComment thread = new ThreadComment(comment,"");
        thread.setId(Long.parseLong(comment.getId()));
        log.addFavComment(thread);
    }

    /**
     * Called when the star button is pressed in the selected comment when
     * comment is already starred. Remove the comment as favourite.
     * 
     * @param comment
     */
    public void unfavouriteAComment(String id) {
        Toast.makeText(getActivity(), "Comment removed from Favourites.", Toast.LENGTH_SHORT)
                .show();
        FavouritesLog log = FavouritesLog.getInstance(getActivity());
        log.removeFavComment(id);
    }

    /**
     * Set up and launch the postCommentFragment when the user wishes to reply
     * to a comment. The fragment takes as input the index of the therad and the
     * comment object.
     * 
     * @param comment
     * @param threadIndex
     */
    public void replyToComment(Comment comment, int threadIndex) {
        Fragment fragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("cmt", comment);
        bundle.putLong("id", threadIndex);
        fragment.setArguments(bundle);
        boolean fromFavs = false;
        Fragment fav = getFragmentManager().findFragmentByTag("favThrFragment");
        if(fav != null){
            fromFavs = true;
        }
        bundle.putBoolean("fromFavs", fromFavs);
        getFragmentManager().beginTransaction()
                .replace(container, fragment, "postFrag").addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    private OnItemClickListener commentButtonListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < 2) {
                return;
            }
            if (getArguments().getInt("favCom") == -1) {
            	return;
            }
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            // "+1" is necessary because of PullToRefresh
            final Comment comment = (Comment) threadView.getItemAtPosition(position + 1);
            RelativeLayout relativeInflater = (RelativeLayout) view
                    .findViewById(R.id.relative_inflater);
            View child = inflater.inflate(R.layout.comment_buttons, null);

            /*
             * If the child layout with buttons is already inflated, remove it.
             * If not, inflate it.
             */
            if (relativeInflater != null && relativeInflater.getChildCount() > 0) {
                relativeInflater.removeAllViews();
                return;
            } else {
                resetOtherCommentLayouts(position);
                relativeInflater.addView(child);
                setLocationField(view, comment);
            }

            if (comment.hasImage()) {
                ImageButton thumbnail = (ImageButton) view
                        .findViewById(R.id.thread_view_comment_thumbnail);
                thumbnail.setVisibility(View.VISIBLE);
                thumbnail.setFocusable(false);
            }

            final ImageButton replyButton = (ImageButton) view
                    .findViewById(R.id.comment_reply_button);
            replyButton.setFocusable(false);

            final ImageButton starButton = (ImageButton) view
                    .findViewById(R.id.comment_star_button);
            starButton.setFocusable(false);

            // Check if the comment is by the user to decide
            // wether or not to display the edit button.
            if (HashHelper.getHash(comment.getUser()).equals(comment.getHash())) {
                final ImageButton editButton = (ImageButton) view
                        .findViewById(R.id.comment_edit_button);
                editButton.setVisibility(View.VISIBLE);
                editButton.setFocusable(false);

                editButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // SETUP FOR COMMENT EDITING GOES HERE
                        editComment(comment);
                    }
                });
            }

            // Check if the favourites log already has a copy.
            if (FavouritesLog.getInstance(getActivity()).hasFavComment(comment.getId())) {
                starButton.setImageResource(R.drawable.ic_rating_marked);
            }

            starButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Favourite or unfavourite depending on current state
                    if (!FavouritesLog.getInstance(getActivity()).hasFavComment(comment.getId())) {
                        starButton.setImageResource(R.drawable.ic_rating_marked);
                        favouriteAComment(comment);
                    } else {
                        starButton.setImageResource(R.drawable.ic_rating_important);
                        unfavouriteAComment(comment.getId());
                    }
                }
            });

            replyButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    replyToComment(comment, threadIndex);
                }
            });

        }
    };
    
    private void editComment(Comment comment){
        Fragment fragment = new EditFragment();
        Bundle bundle = new Bundle();
        boolean fromFavs = false;
        bundle.putInt("threadIndex", threadIndex);
        bundle.putString("commentId", comment.getId());
        Fragment fav = getFragmentManager().findFragmentByTag("favThrFragment");
        if(fav != null){
            fromFavs = true;
        }
        bundle.putBoolean("fromFavs", fromFavs);
        Log.e("EDIT:", "Id of comment being passed." + comment.getId());
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(container, fragment, "editFrag").addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    private void resetOtherCommentLayouts(int position) {
        for (int i = 2; i < threadView.getCount() + 1; ++i) {

            if (i == position + 1) {
                continue;
            }
            View v = threadView.getChildAt(i);
            if (v == null) {
                continue;
            }
            RelativeLayout relativeInflater = (RelativeLayout) v
                    .findViewById(R.id.relative_inflater);
            if (relativeInflater != null && relativeInflater.getChildCount() > 0) {
                relativeInflater.removeAllViews();
            }
        }

    }

    /**
     * Determines which sorting option the user selected and sorts the comments
     * accordingly.
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case (R.id.comment_sort_date_new):
            // User wants to push newer comments to the top.
            sortByTag(SortUtil.SORT_DATE_NEWEST);
            return true;
        case (R.id.comment_sort_date_old):
            // User wants to push older comments to the top.
            sortByTag(SortUtil.SORT_DATE_OLDEST);
            return true;
        case (R.id.comment_sort_image):
            // User wants to push comments with images to the top.
            sortByTag(SortUtil.SORT_IMAGE);
            return true;
        case (R.id.comment_sort_score_high):
            // User wants to push comments with a high score/relevance to the
            // top.
            sortByTag(SortUtil.SORT_USER_SCORE_HIGHEST);
            return true;
        case (R.id.comment_sort_score_low):
            // User wants to push comments with a low score/relevance to the
            // top.
            sortByTag(SortUtil.SORT_USER_SCORE_LOWEST);
            return true;
        case (R.id.comment_sort_location):
            // User wants to push comments near a selected location to the top.
            locSortFlag = 1;
            this.getSortingLoc();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Given a sorting tag, perform sort, remember chosen sorting method and
     * reset the adapter to reflect changes.
     * 
     * @param tag
     *            tag to sort by. Tags are defined in SortUtil.java
     */
    private void sortByTag(int tag) {
        prefManager.setCommentSort(tag);
        SortUtil.sortComments(tag, thread.getBodyComment().getChildren());
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
        threadView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Sends the user into a CustomLocationFragment so they can choose a custom
     * location to sort comments by.
     * 
     */
    private void getSortingLoc() {
        Bundle args = new Bundle();
        args.putInt("postType", CustomLocationFragment.SORT_COMMENT);
        CustomLocationFragment frag = new CustomLocationFragment();
        frag.setArguments(args);
        getFragmentManager().beginTransaction().replace(container, frag, "customLocFrag")
                .addToBackStack(null).commit();
        getFragmentManager().executePendingTransactions();
    }
    
    public void reload() {
        ThreadManager.startGetComments(this, threadIndex, null);
    }
    
    public void finishReload() {
        SortUtil.sortComments(prefManager.getCommentSort(), thread.getBodyComment().getChildren());
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
        // Assign custom adapter to the thread listView.
        threadView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        threadView.onRefreshComplete();
    }
}
