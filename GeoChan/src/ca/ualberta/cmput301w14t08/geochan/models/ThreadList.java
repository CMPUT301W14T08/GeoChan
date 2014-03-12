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
import java.util.Collections;

import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;

public class ThreadList {
    private static ArrayList<ThreadComment> threads = null;
    
    /**
     * A GeoLocation used for our thread sorting methods, should be set
     * by the fragment whenever the user decides to sort Threads by relevance
     * or location.
     */
    private static GeoLocation sortLoc;

    /**
     * Getters and setters
     */
    public static ArrayList<ThreadComment> getThreads() {
        if (threads == null) {
            threads = new ArrayList<ThreadComment>();
        }
        return threads;
    }

    public static void setThreads(ArrayList<ThreadComment> listOfThreads) {
        threads = listOfThreads;
    }
    
    public static void setSortLoc(GeoLocation g){
        sortLoc = g;
    }
    
    public static GeoLocation getSortLoc(){
        return sortLoc;
    }

    public static void addThread(Comment comment, String title) {
        if (threads == null) {
            threads = new ArrayList<ThreadComment>();
        }
        threads.add(new ThreadComment(comment, title));
    }
    
    /**
     * Adds a new ThreadComment to the ThreadList.
     * @param t The ThreadComment to be added.
     */
    public static void addThread(ThreadComment t) {
        if (threads == null) {
            threads = new ArrayList<ThreadComment>();
        }
        threads.add(t);
    }
    
    public static void clearThreads(){
        threads.clear();
    }

    /**
     * Sorts threads according to the tag passed. "DATE_NEWEST" pushes the most
     * recent threads to the top. "DATE_OLDEST" pushes the oldest threads to the
     * top.
     * 
     * @param tag
     *            Sets the sorting method to use
     */
    public static void sortThreads(int tag) {
        switch (tag) {
        case SortTypes.SORT_DATE_NEWEST:
            Collections.sort(threads, SortTypes.sortThreadsByDateNewest());
            break;
        case SortTypes.SORT_DATE_OLDEST:
            Collections.sort(threads, SortTypes.sortThreadsByDateOldest());
            break;
        case SortTypes.SORT_USER_SCORE_HIGHEST:
            Collections.sort(threads, SortTypes.sortThreadsByUserScoreHighest(ThreadList.getSortLoc()));
            break;
        case SortTypes.SORT_USER_SCORE_LOWEST:
            Collections.sort(threads, SortTypes.sortThreadsByUserScoreLowest(ThreadList.getSortLoc()));
            break;
        case SortTypes.SORT_LOCATION_MISC:
            Collections.sort(threads, SortTypes.sortThreadsByLocation(sortLoc));
            break;
        }
    }
}
