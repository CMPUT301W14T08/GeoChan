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

package ca.ualberta.cmput301w14t08.geochan.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostReplyFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostThreadFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PreferencesFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.helpers.UserHashManager;

public class MainActivity extends Activity implements OnBackStackChangedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }
        // DO NOT DELETE THE LINES BELOW OR THIS APP WILL EXPLODE
        ElasticSearchClient.generateInstance();
        UserHashManager.generateInstance(this);
        Fragment fragment = new ThreadListFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        getFragmentManager().addOnBackStackChangedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PreferencesFragment(), "prefFrag")
                    .addToBackStack(null).commit();

            // This next line is necessary for JUnit to see fragments
            getFragmentManager().executePendingTransactions();
            return true;
        case R.id.action_add_thread:
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PostThreadFragment(), "postThreadFrag")
                    .addToBackStack(null).commit();

            // This next line is necessary for JUnit to see fragments
            getFragmentManager().executePendingTransactions();
            return true;
        case android.R.id.home:
            getFragmentManager().popBackStack();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackStackChanged() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void postNewThread(View v) {
        PostThreadFragment fragment = (PostThreadFragment) getFragmentManager().findFragmentByTag(
                "postThreadFrag");
        fragment.postNewThread(v);
    }

    public void postComment(View v) {
        PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                .findFragmentByTag("comFrag");
        fragment.postComment(v);
    }

    public void submitLocation(View v) {
        CustomLocationFragment fragment = (CustomLocationFragment) getFragmentManager()
                .findFragmentByTag("customLocFrag");
        fragment.submitLocation(v);
    }

    public void postReply(View v) {
        PostReplyFragment fragment = (PostReplyFragment) getFragmentManager().findFragmentByTag(
                "repFrag");
        fragment.postReply(v);
    }

    public void postReplyToOp(View v) {
        ThreadViewFragment fragment = (ThreadViewFragment) getFragmentManager().findFragmentByTag(
                "thread_view_fragment");
        Bundle bundle = fragment.getArguments();
        Fragment f = new PostCommentFragment();
        f.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, f, "comFrag")
                .addToBackStack(null).commit();
        getFragmentManager().executePendingTransactions();
    }

    public void changeLocation(View v) {
        Log.e("clicked", "changeLocationButton");
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CustomLocationFragment(), "customLocFrag")
                .addToBackStack(null).commit();
    }
}
