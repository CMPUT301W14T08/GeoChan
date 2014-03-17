package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.helpers.FavouritesIOManager;

/**
 * This class handles the threads/comments saved by the user as favourite, to be
 * available for later viewing.
 */
public class Favourites {
    private ArrayList<ThreadComment> threads;
    private ArrayList<Comment> comments;

    public void addThreadComment(ThreadComment thread) {
        threads.add(thread);
        FavouritesIOManager.serializeThreads();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        FavouritesIOManager.serializeComments();
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
