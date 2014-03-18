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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostThreadFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PreferencesFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;

/**
 * This is the main and, so far, only activity in the application. It inflates
 * the default fragment and handles some of the crucial controller methods
 */
public class MainActivity extends Activity implements OnBackStackChangedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }
        // DO NOT DELETE THE LINE BELOW OR THIS APP WILL EXPLODE
        PreferencesManager.generateInstance(this);
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
            PostThreadFragment frag = new PostThreadFragment();
            frag.setArguments(new Bundle());
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag, "postThreadFrag").addToBackStack(null)
                    .commit();

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
    public void onResume() {
        super.onResume();
        getFragmentManager().addOnBackStackChangedListener(this);
        checkActionBar();
    }

    /**
     * Checks the back stack for fragments and enables/disables the back button
     * in the action bar accordingly
     */
    @Override
    public void onBackStackChanged() {
        checkActionBar();
    }

    /**
     * Calls the respective post new thread method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void postNewThread(View v) {
        PostThreadFragment fragment = (PostThreadFragment) getFragmentManager().findFragmentByTag(
                "postThreadFrag");
        fragment.postNewThread(v);
    }

    /**
     * Calls the respective post reply method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void postReply(View v) {
        PostCommentFragment fragment = (PostCommentFragment) getFragmentManager()
                .findFragmentByTag("repFrag");
        fragment.postReply(v);
    }

    /**
     * Calls the respective change location method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void changeLocation(View v) {
        Bundle args = new Bundle();
        if (v.getId() == R.id.thread_location_button) {
            args.putInt("postType", CustomLocationFragment.THREAD);
        } else if (v.getId() == R.id.location_button) {
            args.putInt("postType", CustomLocationFragment.COMMENT);
        }
        CustomLocationFragment frag = new CustomLocationFragment();
        frag.setArguments(args);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, "customLocFrag").addToBackStack(null)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    /**
     * Calls the respective submit location method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void submitLocation(View v) {
        CustomLocationFragment fragment = (CustomLocationFragment) getFragmentManager()
                .findFragmentByTag("customLocFrag");
        fragment.submitNewLocationFromCoordinates(v);
    }

    /**
     * Calls the respective submit location method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void submitCurrentLocation(View v) {
        CustomLocationFragment fragment = (CustomLocationFragment) getFragmentManager()
                .findFragmentByTag("customLocFrag");
        fragment.submitCurrentLocation(v);
    }
    
    /**
     * Checks the back stack for fragments and enables/disables the back button
     * in the action bar accordingly
     */
    private void checkActionBar() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

}
