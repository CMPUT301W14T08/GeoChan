package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.managers.FavouritesIOManager;

/**
 * The class that handles the threads/comments saved by the user as favourite,
 * to be available for later viewing.
 */
public class FavouritesLog {
    private static FavouritesLog instance = null;
    @SuppressWarnings("unused")
    private Context context;
    private FavouritesIOManager manager;
    private ArrayList<ThreadComment> threads;
    private ArrayList<Comment> comments;

    private FavouritesLog(Context context) {
        this.context = context;
        manager = FavouritesIOManager.getInstance(context);
        threads = manager.deSerializeThreads();
        comments = manager.deSerializeComments();
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

    public void addComment(Comment comment) {
        comments.add(comment);
        manager.serializeComments();
    }

    public void removeThreadComment(ThreadComment threadComment) {
        for (ThreadComment t : getThreads()) {
            if (t.getId().equals(threadComment.getId())) {
                getThreads().remove(t);
            }
        }
        manager.serializeThreads();
    }

    public void removeComment(Comment comment) {
        for (Comment c : getComments()) {
            if (c.getId().equals(comment.getId())) {
                getComments().remove(c);
            }
        }
        manager.serializeComments();
    }

    /**
     * Iterate over the cached favourites and check for comment with given id
     * 
     * @param id
     * @return
     */
    public boolean hasComment(String id) {
        for (Comment c : getComments()) {
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

    public void setThreads(ArrayList<ThreadComment> threads) {
        this.threads = threads;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
