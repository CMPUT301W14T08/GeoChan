package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

/**
 * This class handles the threads/comments saved by the user as favourite, to be available for later viewing.
 */
public class Favourites {
    private ArrayList<ThreadComment> threads;
    private ArrayList<Comment> comments;
    
    
    
    
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
