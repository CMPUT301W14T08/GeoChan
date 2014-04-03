package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class CommentList {
    @Expose
    private ArrayList<CommentList> comments;
    @Expose
    private String id;
    private Comment comment;
    
    public CommentList(String id, Comment comment) {
        comments = new ArrayList<CommentList>();
        this.id = id;
        this.comment = comment;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setComment(Comment comment) {
        this.comment = comment;
    }
    
    public Comment getComment() {
        return comment;
    }
    
    public void setComments(ArrayList<CommentList> comments) {
        this.comments = comments;
    }
    
    public ArrayList<CommentList> getComments() {
        return comments;
    }
}
