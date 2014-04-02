package ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables.ElasticSearchGetCommentListRunnable;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentListRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;

public class ElasticSearchGetCommentListTask implements GetCommentListRunnableInterface {

    private String id;
    private CommentList cache;
    private CommentLoader loader;
    private Runnable getCommentListRunnable;
    private ThreadManager manager;
    private Thread thread;
    
    public ElasticSearchGetCommentListTask() {
        this.getCommentListRunnable = new ElasticSearchGetCommentListRunnable(this);
    }
    
    public void initCommentListTask(ThreadManager manager, CommentLoader loader, String id) {
        this.manager = manager;
        this.loader = loader;
        this.id = id;
    }
    
    public void handleState(int state) {
        manager.handleGetCommentListState(this, state);
    }
    
    @Override
    public void setGetCommentListThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleGetCommentListState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchGetCommentListRunnable.STATE_GET_LIST_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case ElasticSearchGetCommentListRunnable.STATE_GET_LIST_FAILED:
            outState = ThreadManager.GET_COMMENT_LIST_FAILED;
            break;
        default:
            outState = ThreadManager.GET_COMMENT_LIST_RUNNING;
            break;
        }
        handleState(outState);
    }

    @Override
    public void setCommentListCache(CommentList cache) {
        this.cache = cache;
    }

    @Override
    public CommentList getCommentListCache() {
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
    
    public Runnable getGetCommentListRunnable() {
        return getCommentListRunnable;
    }
    
    public void recycle() {
        this.id = null;
        this.cache = null;
        this.manager = null;
        this.loader = null;
    }
}
