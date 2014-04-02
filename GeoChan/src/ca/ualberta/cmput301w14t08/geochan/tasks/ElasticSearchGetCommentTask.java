package ca.ualberta.cmput301w14t08.geochan.tasks;

import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchGetCommentRunnable;

public class ElasticSearchGetCommentTask implements GetCommentRunnableInterface {

    private String id;
    private Comment cache;
    private CommentLoader loader;
    private Runnable getCommentRunnable;
    private ThreadManager manager;
    private Thread thread;
    
    public ElasticSearchGetCommentTask() {
        this.getCommentRunnable = new ElasticSearchGetCommentRunnable(this);
    }
    
    public void initCommentTask(ThreadManager manager, CommentLoader loader, String id) {
        this.manager = manager;
        this.loader = loader;
        this.id = id;
    }
    
    public void handleState(int state) {
        manager.handleGetCommentState(this, state);
    }
    
    @Override
    public void setGetCommentThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleGetCommentState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchGetCommentRunnable.STATE_GET_COMMENT_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case ElasticSearchGetCommentRunnable.STATE_GET_COMMENT_FAILED:
            outState = ThreadManager.GET_COMMENT_FAILED;
            break;
        default:
            outState = ThreadManager.GET_COMMENT_RUNNING;
            break;
        }
        handleState(outState);
    }

    @Override
    public void setCommentCache(Comment cache) {
        this.cache = cache;
    }

    @Override
    public Comment getCommentCache() {
        return cache;
    }
    
    public String getId() {
        return id;
    }
    
    public CommentLoader getLoader() {
        return loader;
    }
    
    public void setCurrentThread(Thread thread) {
        synchronized(manager) {
            this.thread = thread;
        }
    }
    
    public Thread getCurrentThread() {
        synchronized(manager) {
            return thread;
        }
    }
    
    public Runnable getGetCommentRunnable() {
        return getCommentRunnable;
    }
    
    public void recycle() {
        this.id = null;
        this.cache = null;
        this.manager = null;
        this.loader = null;
    }
}
