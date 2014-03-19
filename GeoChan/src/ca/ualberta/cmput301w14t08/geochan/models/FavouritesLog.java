package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.managers.FavouritesIOManager;

/**
 * This class handles the threads/comments saved by the user as favourite, to be
 * available for later viewing.
 */
public class FavouritesLog {
    private static FavouritesLog instance = null;
    private FavouritesIOManager manager;
    private ArrayList<ThreadComment> threads;
    private ArrayList<Comment> comments;

    private FavouritesLog() {
        threads = new ArrayList<ThreadComment>();
        comments = new ArrayList<Comment>();
        manager = FavouritesIOManager.getInstance();
    }

    public static FavouritesLog getInstance() {
        if (instance == null) {
            instance = new FavouritesLog();
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
