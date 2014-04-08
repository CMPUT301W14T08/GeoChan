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

package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.managers.GeoLocationLogIOManager;

/**
 * Handles all operations involved in logging used locations.
 * Follows singleton design pattern.
 * 
 * @author Artem Chikin
 * 
 */
public class GeoLocationLog {

    private static GeoLocationLog instance = null;
    private Context context;
    private ArrayList<GeoLocation> entries;

    /**
     * Protected constructor. Is called only if singleton has not been
     * constructed. Sets up context and deserializes the log.
     * @param context  The Context the GeoLocationLog is running in.
     */
    protected GeoLocationLog(Context context) {
        this.context = context;
        GeoLocationLogIOManager manager = GeoLocationLogIOManager.getInstance(context);
        this.entries = manager.deserializeLog();
    }

    /**
     * Singleton construction method. If instance already exists, return it.
     * Else, construct a new one
     * 
     * @param context  The context the GeoLocationLog is running in.
     * @return the instance of GeoLocationLog
     */
    public static GeoLocationLog generateInstance(Context context) {
        if (instance == null) {
            instance = new GeoLocationLog(context);
        }
        return instance;
    }
    
    /**
     * Returns the Singleton instance of this object.
     * @return  The instance of GeoLocationLog.
     */
    public static GeoLocationLog getInstance() {
    	return instance;
    }
    
    /**
     * Adds a new LogEntry to the GeoLocationLog.
     * 
     * @param geoLocation
     *            GeoLocation to be stored in the new LogEntry.
     */
    public void addLogEntry(GeoLocation newGeoLocation) {
    	for (GeoLocation location : entries) {
    		if (newGeoLocation.getLocationDescription().equals(location.getLocationDescription())) {
    			return;
    		}
    	}
        entries.add(newGeoLocation);
        GeoLocationLogIOManager manager = GeoLocationLogIOManager.getInstance(context);
        manager.serializeLog(entries);
    }

    /**
     * Return the log entries array, creates one if it does not exist.
     * 
     * @return the Log entries array
     */
    public ArrayList<GeoLocation> getLogEntries() {
        GeoLocationLogIOManager manager = GeoLocationLogIOManager.getInstance(context);
        entries = manager.deserializeLog();
        return entries;
    }

    /**
     * Checks whether the log contains any entries.
     * 
     * @return Return true if log is empty, else false. If null create one
     */
    public boolean isEmpty() {
        return entries.size() == 0;
    }

    /**
     * Removes all LogEntry objects from entries.
     */
    public void clearLog() {
        entries.clear();
    }

    /**
     * Returns the number of LogEntry objects stored in entries.
     * 
     * @return Number of LogEntry objects.
     */
    public int size() {
        return entries.size();
    }
}