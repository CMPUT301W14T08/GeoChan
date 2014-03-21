package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.managers.FavouritesIOManager;

/**
 * This class handles the threads/comments saved by the user as favourite, to be
 * available for later viewing.
 */
public class FavouritesLog {
    private static FavouritesLog instance = null;
    @SuppressWarnings("unused")
    private Context context;
    private FavouritesIOManager manager;
    private ArrayList<ThreadComment> threads;
    private ArrayList<Comment> comments;

    private FavouritesLog(Context context) {
        threads = new ArrayList<ThreadComment>();
        comments = new ArrayList<Comment>();
        this.context = context;
        manager = FavouritesIOManager.getInstance(context);
    }

    public static FavouritesLog getInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesLog(context);
        }
        return instance;
    }

    public void addThreadComment(ThreadComment thread) {
        threads.add(thread);
        // manager.serializeThreads();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        // manager.serializeComments();
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
