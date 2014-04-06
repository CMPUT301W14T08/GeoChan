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
        CommentList c = null;
        if (commentList.getId().equals(id)) {
            c = commentList;
        }
        for (CommentList child : commentList.getChildren()) {
            CommentList c2 = findCommentListById(child, id);
            if (c2 != null) {
                c = c2;
            }
        }
        return c;
    }
    
    public void getIdsFromList(CommentList list, ArrayList<String> idList) {
        idList.add(list.getId());
        for (CommentList childList : list.getChildren()) {
            getIdsFromList(childList, idList);
        }
    }
    
    // Should only be called on bodyComment, returns the bodyComment with the children all set.
    // depth first traverasl
    public Comment reconsructFromCommentList(CommentList list, Comment comment) {
        comment.setChildren(new ArrayList<Comment>());
        if (list.getChildren().size() == 0) {
            return comment;
        } else {
            for(CommentList cl : list.getChildren()) {
                comment.addChild(reconsructFromCommentList(cl, cl.getComment()));
            }
        }
        return comment;
    }
}
