package ca.ualberta.cmput301w14t08.geochan.interfaces;

import ca.ualberta.cmput301w14t08.geochan.models.Comment;

public interface GetCommentRunnableInterface {
    void setGetCommentThread(Thread thread);
    
    void handleGetCommentState(int state);
    
    void setCommentCache(Comment cache);
    
    Comment getCommentCache();
}
