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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.ThreadViewAdapter;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Fragment which displays the contents of a ThreadComment.
 */
public class ThreadViewFragment extends Fragment implements LoaderCallbacks<ArrayList<Comment>> {
    private ListView threadView;
    private ThreadViewAdapter adapter;
    private int threadIndex;
    private ThreadComment thread = null;

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
        threadIndex = (int) bundle.getLong("id");
        thread = bundle.getParcelable("thread");
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
        threadView = (ListView) getView().findViewById(R.id.thread_view_list);
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
        // Assign custom adapter to the thread listView.
        threadView.setAdapter(adapter);   
        adapter.notifyDataSetChanged();
        threadView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                final Comment comment = (Comment) parent.getAdapter().getItem(position);
                RelativeLayout relativeInflater = (RelativeLayout)view.findViewById(R.id.relative_inflater);
                LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View child = inflater.inflate(R.layout.comment_buttons, null);
                relativeInflater.addView(child);
                
                final ImageButton replyButton = (ImageButton) view
                        .findViewById(R.id.comment_reply_button);
                
                final ImageButton starButton = (ImageButton) view
                        .findViewById(R.id.comment_star_button);
                
                if (starButton != null) {
                    starButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Saved to Favourites.", Toast.LENGTH_SHORT).show();
                            FavouritesLog log = FavouritesLog.getInstance(getActivity());
                            log.addComment(comment);
                        }
                    });
                }
                
                if (replyButton != null) {
                    replyButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Perform action on click
                            Log.e("ButtonClick", "click");
                            Log.e("Comment being replied:", comment.getTextPost());
                            Fragment fragment = new PostCommentFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("cmt", comment);
                            bundle.putLong("id", threadIndex);
                            fragment.setArguments(bundle);

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment, "repFrag")
                                    .addToBackStack(null).commit();
                            getFragmentManager().executePendingTransactions();
                        }
                    });
                }
            }
        });
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
         * Have to reset adapter: workaround for a strange issue, for
         * description, see :
         * http://stackoverflow.com/questions/20512068/listview
         * -not-updating-properly-cursoradapter-after-swapcursor
         */
        adapter = new ThreadViewAdapter(getActivity(), thread, getFragmentManager(), threadIndex);
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

    public GeoLocation getThreadLocation() {
        return thread.getSortLoc();
    }
}
