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


public class GeoLocationLog {    

    private static ArrayList<LogEntry> entries;

    private GeoLocationLog() {
        entries = new ArrayList<LogEntry>();
    }
   
    public static void addLogEntry(String threadTitle,GeoLocation geoLocation) {
        if(entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        LogEntry logEntry = new LogEntry(threadTitle,geoLocation);
        entries.add(logEntry);
    }
    
    public static ArrayList<LogEntry> getLogEntries() {
        if(entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries;
    }

    public static boolean isEmpty() {
        if(entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries.size() == 0;
    }

    public static void clearLog() {
        if(entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        entries.clear();
    }

    public static int size() {
        if(entries == null) {
            entries = new ArrayList<LogEntry>();
        }
        return entries.size();
    }
}