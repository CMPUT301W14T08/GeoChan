package ca.ualberta.cmput301w14t08.geochan.interfaces;

import ca.ualberta.cmput301w14t08.geochan.models.CommentHitsList;

public interface GetCommentListRunnableInterface {
    void setGetCommentListThread(Thread thread);
    
    void handleGetCommentListState(int state);
    
    void setCommentListCache(CommentHitsList cache);
    
    CommentHitsList getCommentListCache();
}
