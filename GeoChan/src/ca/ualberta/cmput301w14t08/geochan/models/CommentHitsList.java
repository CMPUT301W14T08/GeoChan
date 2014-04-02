package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

public class CommentHitsList {
    private ArrayList<CommentHitsList> comments;
    private String id;
    
    public CommentHitsList(String id) {
        comments = new ArrayList<CommentHitsList>();
        this.id = id;
    }
    
    public CommentHitsList(String id, ArrayList<String> hits) {
        comments = new ArrayList<CommentHitsList>();
        this.id = id;
        for (String hit : hits) {
            comments.add(new CommentHitsList(hit));
        }
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setComments(ArrayList<CommentHitsList> comments) {
        this.comments = comments;
    }
    
    public ArrayList<CommentHitsList> getComments() {
        return comments;
    }
}
