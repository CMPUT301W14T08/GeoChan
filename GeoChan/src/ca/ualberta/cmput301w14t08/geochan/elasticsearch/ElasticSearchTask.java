package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import ca.ualberta.cmput301w14t08.geochan.interfaces.PostRunnableMethodsInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;

public class ElasticSearchTask implements PostRunnableMethodsInterface {
    private Comment comment;
    private String title;
    private ThreadManager manager;
    private Thread thread;
    private Runnable runnable;
    
   public ElasticSearchTask() {
        runnable = new ElasticSearchPostRunnable(this);
    }
    
    public void initPostTask(ThreadManager manager, Comment comment, String title) {
        this.manager = manager;
        this.comment = comment;
        this.title = title;
    }
    
    public Thread getCurrentThread() {
        synchronized(manager) {
            return thread;
        }
    }

    public void setCurrentThread(Thread thread) {
        synchronized(manager) {
            this.thread = thread;
        }
    }
    
    public Runnable getRunnable() {
        return runnable;
    }
    
    public void handleState(int state) {
        manager.handleState(this, state);
    }
    
    @Override
    public void setPostThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handlePostState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchPostRunnable.STATE_POST_COMPLETE:
            outState = ThreadManager.POST_COMPLETE;
            break;
        case ElasticSearchPostRunnable.STATE_POST_FAILED:
            outState = ThreadManager.POST_FAILED;
            break;
        default:
            outState = ThreadManager.POST_RUNNING;
            break;
        }
        handleState(outState);
    }
    
    public Comment getComment() {
        return comment;
    }
    
    public String getTitle() {
        return title;
    }
}
