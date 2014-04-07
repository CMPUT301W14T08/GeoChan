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

package ca.ualberta.cmput301w14t08.geochan.tasks;

import org.osmdroid.bonuspack.overlays.Marker;

import android.app.ProgressDialog;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetPOIRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.TaskInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetPOIRunnable;

/**
 * Responsible for the task of controlling the runnables
 * that obtain the closest point of interest to the location of the user.
 * @author Artem Chikin
 *
 */
public class GetPOITask implements TaskInterface, GetPOIRunnableInterface {
	private GeoLocation location;
	private Marker marker;
	private String cache;
	private ProgressDialog dialog;
	private Runnable getPOIRunnable;
	private ThreadManager manager;
	private Thread thread;

	/** 
	 * Creates an instance of the task and its runnables.
	 */
	public GetPOITask() {
		this.getPOIRunnable = new GetPOIRunnable(this);
	}

	/**
	 * Initializes the instance of the task with the information needed to run it.
	 * @param manager the ThreadManager
	 * @param location the GeoLocation
	 * @param dialog a ProgressDialog inside the fragment to display the task process
	 * @param marker OSMDroid Marker overlay
	 */
	public void initGetPOITask(ThreadManager manager, GeoLocation location, ProgressDialog dialog, 
			Marker marker) {
		this.manager = manager;
		this.dialog= dialog;
		this.location = location;
		this.marker = marker;
	}

	/**
	 * Handles the various possible states of the
	 * Runnable that gets the POI.
	 * @param state the state
	 */
	@Override
	public void handleGetPOIState(int state) {
		int outState;
		switch (state) {
		case GetPOIRunnable.STATE_GET_POI_COMPLETE:
			outState = ThreadManager.GET_POI_COMPLETE;
			break;
		case GetPOIRunnable.STATE_GET_POI_FAILED:
			outState = ThreadManager.GET_POI_FAILED;
			break;
		default:
            outState = ThreadManager.GET_POI_RUNNING;
            break;
        }
        handleState(outState);
    }
	
    /**
     * {@inheritDoc} 
     */
    @Override
	public void handleState(int state) {
		manager.handleGetPOIState(this, state);
	}

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setCurrentThread(Thread thread) {
        synchronized (manager) {
            this.thread = thread;
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Thread getCurrentThread() {
        synchronized (manager) {
            return thread;
        }
    }
   
    /**
     * {@inheritDoc} 
     */
    @Override
    public void recycle() {
        this.location = null;
        this.cache = null;
        this.manager = null;
    }
    
    /* Getters/setters for the interfaces this task implements below */

    @Override
    public void setPOICache(String cache) {   
        this.cache = cache;
    }
    
    @Override
    public String getPOICache() {
        return cache;
    }
    
	@Override
	public void setGetPOIThread(Thread thread) {
		setCurrentThread(thread);
	}
    
    /* Basic getters/setters below */
    
    public Runnable getGetPOIRunnable() {
        return getPOIRunnable;
    }
    
    
    public GeoLocation getLocation() {
        return location;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }
    
    public Marker getMarker() {
    	return marker;
    }
}
