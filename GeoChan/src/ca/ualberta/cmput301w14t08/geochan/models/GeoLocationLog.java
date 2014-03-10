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

import ca.ualberta.cmput301w14t08.geochan.helpers.LogEntry;

public class GeoLocationLog {

    private static GeoLocationLog instance = null;
    private static ArrayList<LogEntry> entries;

    private GeoLocationLog() {
        entries = new ArrayList<LogEntry>();
    }

    public static GeoLocationLog getInstance() {
        if (instance == null) {
            instance = new GeoLocationLog();
        }
        return instance;
    }

    public void addLogEntry(String threadTitle, GeoLocation geoLocation) {
        LogEntry logEntry = new LogEntry(threadTitle, geoLocation);
        entries.add(logEntry);
    }

    public ArrayList<LogEntry> getLogEntries() {
        return entries;
    }

    public boolean isEmpty() {
        return entries.size() == 0;
    }

    public void clearLog() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }
}