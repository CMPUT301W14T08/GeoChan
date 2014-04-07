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

    public static FavouritesLog getInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesLog(context);
        }
        return instance;
    }

    public void addThreadComment(ThreadComment thread) {
        threads.add(thread);
        manager.serializeThreads();
    }
    
    public void addFavComment(ThreadComment comment) {
    	favComments.add(comment);
    	manager.serializeFavComments();
    }

    public void removeThreadComment(ThreadComment threadComment) {
        for (ThreadComment t : getThreads()) {
            if (t.getId().equals(threadComment.getId())) {
                getThreads().remove(t);
            }
        }
        manager.serializeThreads();
    }

    public void removeFavComment(Comment comment) {
        for (ThreadComment c : getFavComments()) {
            if (c.getId().equals(comment.getId())) {
                getFavComments().remove(c);
            }
        }
        manager.serializeFavComments();
    }

    /**
     * Iterate over the cached favourites and check for comment with given id
     * 
     * @param id
     * @return
     */
    public boolean hasFavComment(String id) {
        for (ThreadComment c : getFavComments()) {
            if (c.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate over the cached favourites and check for threadComment with given
     * id
     * 
     * @param id
     * @return
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
