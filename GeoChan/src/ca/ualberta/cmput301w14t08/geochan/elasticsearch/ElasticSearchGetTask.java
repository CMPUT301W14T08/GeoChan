package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentHitsList;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchGetCommentListRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchGetCommentRunnable;

public class ElasticSearchGetTask {

    private Comment topComment;
    private ArrayList<Comment> cache;
    private CommentHitsList commentHits;
    private Runnable getCommentListRunnable;
    private Runnable getCommentRunnable;
    private ThreadManager manager;
    
    public ElasticSearchGetTask() {
        this.getCommentListRunnable = new ElasticSearchGetCommentListRunnable(this);
        this.getCommentRunnable = new ElasticSearchGetCommentRunnable(this);
    }
    
    public CommentHitsList getCommentHits() {
        return commentHits;
    }
    
    public void setCommentHits(CommentHitsList commentHits) {
        this.commentHits = commentHits;
    }
    
    public Comment getTopComment() {
        return topComment;
    }
    
    public Runnable getGetCommentRunnable() {
        return getCommentRunnable;
    }
    
    public Runnable getGetCommentListRunnable() {
        return getCommentListRunnable;
    }
    
    public void initGetTask(ThreadManager manager, Comment topComment) {
        this.topComment = topComment;
        this.manager = manager;
        this.commentHits = new CommentHitsList(topComment.getId());
    }
    
    public void setCache(ArrayList<Comment> cache) {
        this.cache = cache;
    }
    
    public void recycle() {
        this.topComment = null;
        this.cache = null;
        this.manager = null;
    }
}
