package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import ca.ualberta.cmput301w14t08.geochan.interfaces.PostRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.UpdateRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchPostRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchUpdateRunnable;

public class ElasticSearchTask implements PostRunnableInterface, UpdateRunnableInterface {
    private Comment comment;
    private String title;
    private ThreadManager manager;
    private Thread thread;
    private Runnable postRunnable;
    private Runnable updateRunnable;
    
   public ElasticSearchTask() {
        postRunnable = new ElasticSearchPostRunnable(this);
        updateRunnable = new ElasticSearchUpdateRunnable(this);
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
    
    public Runnable getPostRunnable() {
        return postRunnable;
    }
    
    public Runnable getUpdateRunnable() {
        return updateRunnable;
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
            if (title != null) {
                outState = ThreadManager.TASK_COMPLETE;
            } else {
                outState = ThreadManager.POST_COMPLETE;
            }
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
    
    @Override
    public void setUpdateThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleUpdateState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchUpdateRunnable.STATE_UPDATE_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case ElasticSearchUpdateRunnable.STATE_UPDATE_FAILED:
            outState = ThreadManager.UPDATE_FAILED;
            break;
        default:
            outState = ThreadManager.UPDATE_RUNNING;
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
