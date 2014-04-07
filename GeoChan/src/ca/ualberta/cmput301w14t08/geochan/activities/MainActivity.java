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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.CustomLocationFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.EditFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.FavouritesFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.MapViewFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityHelper;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;

/**
 * Inflates the default fragment and handles some of the crucial controller methods.
 * Initializes most of our singleton classes so attempting to fetch an instance of 
 * one does not return null.
 * 
 * @author Artem Chikin
 * @author Henry Pabst
 * @author Artem Herasymchuk
 */
public class MainActivity extends FragmentActivity implements OnBackStackChangedListener {
	
	/**
	 * Sets up the initial state of the activity. Initializes singleton classes
	 * and a ThreadListFragment to view the app's thread list.
	 * @param savedInstanceState the saved instance state bundle
	 */
	
	// TODO Fix logic in fragments
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            return;
        }
        // DO NOT DELETE THE LINES BELOW OR THIS APP WILL EXPLODE, THESE SINGLETON
        // CLASSES HAVE TO BE INITIALIZED BEFORE ANYTHING ELSE.
        ConnectivityHelper.generateInstance(this);
        Toaster.generateInstance(this);
        PreferencesManager.generateInstance(this);
        CacheManager.generateInstance(this);
        GeoLocationLog.generateInstance(this);
        ThreadManager.generateInstance(this);
        ThreadListFragment fragment = new ThreadListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, "threadListFrag")
                .commit();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

	/**
	 * Inflates this activity's action bar options.
	 * @param menu the menu
	 * @return the result
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles the selection of specific action bar items according
     * to which item was selected.
     * @param item the item selected
     * @return the result
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            Intent intent = new Intent(this.getBaseContext(), PreferencesActivity.class);
            startActivity(intent);
            return true;

        case R.id.action_favourites:
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new FavouritesFragment(), "favouritesFrag")
                    .addToBackStack(null).commit();

            // This next line is necessary for JUnit to see fragments
            getSupportFragmentManager().executePendingTransactions();
            return true;

        case R.id.action_add_thread:
            PostFragment frag = new PostFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("id", -1);
            frag.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag, "postFrag").addToBackStack(null)
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
        }
    }

    /**
     * Re-registers the OnBackStackChangedListener as it does not survive
     * the destruction of the Activity. 
     */
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

    /**
     * Overrides the back button to work with nested fragments
     */
    @Override
    public void onBackPressed() {
        if (!returnBackStackImmediate(getSupportFragmentManager())) {
            super.onBackPressed();
        }
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
    
    /**
     * Propagates a back button press down to our nested fragment manager
     * and its fragments.
     * @param fm the Fragment Manager
     * @return true if the back stack was returned from successfully,
     * false if not
     */
    private boolean returnBackStackImmediate(FragmentManager fm) {
        // HACK: propagate back button press to child fragments.
        // This might not work properly when you have multiple fragments adding
        // multiple children to the back stack.
        // (in our case, only one child fragments adds fragments to the back stack,
        // so we're fine with this)
        //
        // This code was taken from the web site:
        // http://android.joao.jp/2013/09/back-stack-with-nested-fragments-back.html
        // Accessed on March 21, 2014
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
     * @param view
     *            View passed to the activity to check which button was pressed
     */
    public void post(View view) {
        getSupportFragmentManager();
        PostFragment fragment = (PostFragment) getSupportFragmentManager()
                .findFragmentByTag("postFrag");
        if(fragment == null){
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (PostFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("postFrag");
        }
        fragment.post(view);
    }

    /**
     * Calls the respective attach image method in the running PostFragment.
     * @param view View passed to the activity to check which button was pressed.
     */
    public void attachImage(View view) {
        PostFragment fragment = (PostFragment) getSupportFragmentManager()
                .findFragmentByTag("postFrag");
        if(fragment == null){
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (PostFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("postFrag");
        }
        fragment.attachImage(view);
    }

    /**
     * Method called when the Edit Image button is pressed in EditFragment. Finds the
     * appropriate fragment and calls editImage on it.
     * @param view The View passed to the activity to check which button was pressed.
     */
    public void editImage(View view) {
        EditFragment fragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag("editFrag");
        if(fragment == null){
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (EditFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("editFrag");
        }
        fragment.editImage(view);
    }

    /**
     * Method called when the Post Edit button is pressed in EditFragment.
     * Finds the appropriate fragment and calls makeEdit on it.
     * @param view
     */
    public void makeEdit(View view) {
        EditFragment fragment = (EditFragment) getSupportFragmentManager()
                .findFragmentByTag("editFrag");
        if(fragment == null){
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (EditFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("editFrag");
        }
        fragment.makeEdit(view);
    }

    /**
     * Calls the respective change location method in the fragment.
     * 
     * @param view
     *            View passed to the activity to check which button was pressed
     */
    public void changeLocation(View view) {
        Bundle args = new Bundle();
        if (view.getId() == R.id.location_button) {
            args.putInt("postType", CustomLocationFragment.POST);
        } else if (view.getId() == R.id.edit_location_button) {
            args.putInt("postType", CustomLocationFragment.EDIT);
        }
        CustomLocationFragment frag = new CustomLocationFragment();
        frag.setArguments(args);
        FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                .findFragmentByTag("favouritesFrag");
        if(favFrag != null){
            //This bit here solves the issue of a crash when changing location
            //in a reply to a comment in a favourited thread.
            FragmentManager childMan = favFrag.getChildFragmentManager();
            childMan.beginTransaction()
            .replace(R.id.container, frag, "customLocFrag").addToBackStack(null)
            .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag, "customLocFrag").addToBackStack(null)
                .commit();
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    /**
     * Calls the respective submit location method in the fragment.
     * 
     * @param view
     *            View passed to the activity to check which button was pressed
     */
    public void submitLocation(View view) {
        CustomLocationFragment fragment = (CustomLocationFragment) getSupportFragmentManager()
                .findFragmentByTag("customLocFrag");
        if(fragment == null){
            Log.e("DEBUG","submitLocation called for favourites fragment.");
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (CustomLocationFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("customLocFrag");
        }
        fragment.submitNewLocation(view);
     }

    /**
     * Calls the respective submit location method in the fragment.
     * 
     * @param view
     *            View passed to the activity to check which button was pressed
     */
    public void submitCurrentLocation(View view) {
        CustomLocationFragment fragment = (CustomLocationFragment) getSupportFragmentManager()
                .findFragmentByTag("customLocFrag");
        if(fragment == null){
            Log.e("DEBUG","submitCurrentLocation called for favourites fragment.");
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (CustomLocationFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("customLocFrag");
        }
        fragment.submitCurrentLocation(view);
    }

    /**
     * Called when the get_directions_button is clicked in MapViewFragment. Finds the
     * fragment where the button was clicked and calls getDirections on it.
     * @param view View passed to the activity to determine which button was pressed.
     */
    public void getDirections(View view) {
        MapViewFragment fragment = (MapViewFragment) getSupportFragmentManager().findFragmentByTag(
                "mapFrag");
        if(fragment == null){
            FavouritesFragment favFrag = (FavouritesFragment) getSupportFragmentManager()
                    .findFragmentByTag("favouritesFrag");
            fragment = (MapViewFragment) favFrag.getChildFragmentManager()
                    .findFragmentByTag("mapFrag");
        }
        fragment.getDirections();
    }
}
