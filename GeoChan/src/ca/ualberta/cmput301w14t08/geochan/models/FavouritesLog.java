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
import ca.ualberta.cmput301w14t08.geochan.managers.FavouritesIOManager;

/**
 * Handles the threads/comments saved by the user as favourite, to be available
 * for later viewing. Contains methods to add, remove and check presence of
 * favourite comments/threads. Is a singleton.
 * 
 * @autor Artem Chikin
 */
public class FavouritesLog {
    private static FavouritesLog instance = null;
    @SuppressWarnings("unused")
    private Context context;
    private FavouritesIOManager manager;
    private ArrayList<ThreadComment> threads;
    private ArrayList<ThreadComment> favComments;

    private FavouritesLog(Context context) {
        this.context = context;
        manager = FavouritesIOManager.getInstance(context);
        threads = manager.deSerializeThreads();
        favComments = manager.deSerializeFavComments();
    }

    /**
     * Returns the instance of the FavouritesLog.
     * @param context The Context in which the FavouritesLog is running.
     * @return The instance of FavouritesLog.
     */
    public static FavouritesLog getInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesLog(context);
        }
        return instance;
    }

    /**
     * Adds a ThreadComment to favourites.
     * @param thread The ThreadComment to be added.
     */
    public void addThreadComment(ThreadComment thread) {
        threads.add(thread);
        manager.serializeThreads();
    }
    
    /**
     * Adds a Comment to favourites.
     * @param comment The Comment to be added.
     */
    public void addFavComment(ThreadComment comment) {
    	favComments.add(comment);
    	manager.serializeFavComments();
    }

    /**
     * Removes a ThreadComment from favourites.
     * @param threadComment The ThreadComment to be removed.
     */
    public void removeThreadComment(ThreadComment threadComment) {
        for (ThreadComment t : getThreads()) {
            if (t.getId().equals(threadComment.getId())) {
                getThreads().remove(t);
            }
        }
        manager.serializeThreads();
    }

    /**
     * Removes a Comment from favourites.
     * @param id The ID of the Comment to be removed.
     */
    public void removeFavComment(String id) {
        for (ThreadComment c : getFavComments()) {
            if (c.getBodyComment().getId().equals(id)) {
                getFavComments().remove(c);
            }
        }
        manager.serializeFavComments();
    }

    /**
     * Iterate over the cached favourites and check for comment with given id
     * 
     * @param id The ID to be checked for.
     * @return True if the Comment was found, false otherwise.
     */
    public boolean hasFavComment(String id) {
        for (ThreadComment c : getFavComments()) {
            if (c.getBodyComment().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate over the cached favourites and check for threadComment with given
     * id.
     * 
     * @param id The ID to be checked for.
     * @return True if the ThreadComment was found, false otherwise.
     */
    public boolean hasThreadComment(String id) {
        for (ThreadComment t : getThreads()) {
            if (t.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Getters and Setters
    public ArrayList<ThreadComment> getThreads() {
        return threads;
    }

    public ArrayList<ThreadComment> getFavComments() {
		return favComments;
	}

	public void setFavComments(ArrayList<ThreadComment> favComments) {
		this.favComments = favComments;
	}

	public void setThreads(ArrayList<ThreadComment> threads) {
        this.threads = threads;
    }
}
