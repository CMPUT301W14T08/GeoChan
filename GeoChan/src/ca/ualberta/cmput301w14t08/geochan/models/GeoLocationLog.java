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

/**
 * This model class handles all operations involved in logging used locations.
 * 
 */
public class GeoLocationLog {

    private static GeoLocationLog instance = null;
    private static ArrayList<LogEntry> entries;

    /**
     * Protected constructor. Is called only if singleton has not been constructed.
     */
    protected GeoLocationLog() {
        entries = new ArrayList<LogEntry>();
    }

    public static GeoLocationLog getInstance() {
        if (instance == null) {
            instance = new GeoLocationLog();
        }
        return instance;
    }
    
    /**
     * Adds a new LogEntry to the GeoLocationLog.
     * @param threadTitle
     *            Title to be stored in the new LogEntry.
     * @param geoLocation
     *            GeoLocation to be stored in the new LogEntry.
     */
    public static void addLogEntry(String threadTitle, GeoLocation geoLocation) {
        if (entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        LogEntry logEntry = new LogEntry(threadTitle, geoLocation);
        entries.add(logEntry);
    }

    /**
     * Return the log entries array, if null create one
     * @return entries
     */
    public static ArrayList<LogEntry> getLogEntries() {
        if (entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries;
    }

    /**
     * Return true if log is empty, else false. If null create one
     * @return entries
     */
    public static boolean isEmpty() {
        if (entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries.size() == 0;
    }

    /**
     * Removes all LogEntry objects from entries.
     */
    public static void clearLog() {
        if (entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        entries.clear();
    }

    /**
     * Returns the number of LogEntry objects stored in entries.
     * @return Number of LogEntry objects.
     */
    public static int size() {
        if (entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries.size();
    }
}