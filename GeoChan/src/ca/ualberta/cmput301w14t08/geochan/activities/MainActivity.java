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

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.FavouritesFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.MapViewFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostThreadFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;

/**
 * This is the main and, so far, only activity in the application. It inflates
 * the default fragment and handles some of the crucial controller methods
 * @author henrypabst
 */
 
public class MainActivity extends FragmentActivity implements OnBackStackChangedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }
        // DO NOT DELETE THE LINE BELOW OR THIS APP WILL EXPLODE
        PreferencesManager.generateInstance(this);
        ThreadListFragment fragment = new ThreadListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment)
                .commit();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
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
            /*
             * getSupportFragmentManager().beginTransaction()
             * .replace(R.id.fragment_container, new PreferencesFragment(),
             * "prefFrag") .addToBackStack(null).commit();
             * 
             * // This next line is necessary for JUnit to see fragments
             * getSupportFragmentManager().executePendingTransactions();
             */
            Intent intent = new Intent(this.getBaseContext(), PreferencesActivity.class);
            startActivity(intent);
            return true;

        case R.id.action_favourites:
            /*
             * Intent intent = new
             * Intent(this.getBaseContext(),FavouritesActivity.class);
             * startActivity(intent);
             */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavouritesFragment(), "favouritesFrag")
                    .addToBackStack(null).commit();

            // This next line is necessary for JUnit to see fragments
            getSupportFragmentManager().executePendingTransactions();
            return true;

        case R.id.action_add_thread:
            PostThreadFragment frag = new PostThreadFragment();
            frag.setArguments(new Bundle());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag, "postThreadFrag").addToBackStack(null)
                    .commit();

            // This next line is necessary for JUnit to see fragments
            getSupportFragmentManager().executePendingTransactions();
            return true;
        case android.R.id.home:
            if (!returnBackStackImmediate(getSupportFragmentManager())) {
                getSupportFragmentManager().popBackStack();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
            //return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
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

    @Override
    public void onBackPressed() {
        if (!returnBackStackImmediate(getSupportFragmentManager())) {
            super.onBackPressed();
        }
    }

    // HACK: propagate back button press to child fragments.
    // This might not work properly when you have multiple fragments adding
    // multiple children to the backstack.
    // (in our case, only one child fragments adds fragments to the backstack,
    // so we're fine with this)
    //
    // This code was taken from the website:
    // http://android.joao.jp/2013/09/back-stack-with-nested-fragments-back.html
    // Accessed on March 21, 2014

    private boolean returnBackStackImmediate(FragmentManager fm) {
        List<Fragment> fragments = fm.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.getChildFragmentManager() != null) {
                    if (fragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                        if (fragment.getChildFragmentManager().popBackStackImmediate()) {
                            return true;
                        } else {
                            return returnBackStackImmediate(fragment.getChildFragmentManager());
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Calls the respective post new thread method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void postNewThread(View v) {
        PostThreadFragment fragment = (PostThreadFragment) getSupportFragmentManager()
                .findFragmentByTag("postThreadFrag");
        fragment.postNewThread(v);
    }

    /**
     * Calls the respective post reply method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void postReply(View v) {
        PostCommentFragment fragment = (PostCommentFragment) getSupportFragmentManager()
                .findFragmentByTag("repFrag");
        fragment.postReply(v);
    }
    
    public void attachImageReply(View v) {
        PostCommentFragment fragment = (PostCommentFragment) getSupportFragmentManager()
                .findFragmentByTag("repFrag");
        fragment.attachImageReply(v);
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, "customLocFrag").addToBackStack(null)
                .commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Calls the respective submit location method in the fragment.
     * 
     * @param v
     *            View passed to the activity to check which button was pressed
     */
    public void submitLocation(View v) {
        CustomLocationFragment fragment = (CustomLocationFragment) getSupportFragmentManager()
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
        CustomLocationFragment fragment = (CustomLocationFragment) getSupportFragmentManager()
                .findFragmentByTag("customLocFrag");
        fragment.submitCurrentLocation(v);
    }

    /**
     * Checks the back stack for fragments and enables/disables the back button
     * in the action bar accordingly
     */
    private void checkActionBar() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    public void getDirections(View v) {
        MapViewFragment fragment = (MapViewFragment) getSupportFragmentManager().findFragmentByTag(
                "mapFrag");
        fragment.getDirections();
    }

}
