package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class CommentList {
    @Expose
    private ArrayList<CommentList> comments;
    @Expose
    private String id;
    private Comment comment;
    
    public CommentList(Comment comment) {
        comments = new ArrayList<CommentList>();
        this.id = comment.getId();
        this.comment = comment;
    }
    
    public CommentList(String id) {
        comments = new ArrayList<CommentList>();
        this.id = id;
        this.comment = null;
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
    
    public void setChildren(ArrayList<CommentList> comments) {
        this.comments = comments;
    }
    
    public void addCommentList(CommentList commentList) {
        comments.add(commentList);
    }
    
    public ArrayList<CommentList> getChildren() {
        return comments;
    }
    
    public CommentList findCommentListById(CommentList commentList, String id) {
        for (CommentList child : commentList.getChildren()) {
            if (child.getId() == id) {
                return child;
            }
            findCommentListById(child, id);
        }
        return null;
    }

}
