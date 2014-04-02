package ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks;

import android.graphics.Bitmap;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables.ElasticSearchEditImageRunnable;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables.ElasticSearchEditRunnable;
import ca.ualberta.cmput301w14t08.geochan.interfaces.EditImageRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.EditRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;

public class ElasticSearchEditTask implements EditRunnableInterface, EditImageRunnableInterface {
    private Comment comment;
    private Bitmap bitmap;
    private Boolean isThread;
    private ThreadManager manager;
    private Thread thread;
    private Runnable editImageRunnable;
    private Runnable editRunnable;
    
   public ElasticSearchEditTask() {
        editImageRunnable = new ElasticSearchEditImageRunnable(this);
        editRunnable = new ElasticSearchEditRunnable(this);
    }
    
    public void initEditTask(ThreadManager manager, Comment comment, Bitmap bitmap, Boolean isThread) {
        this.manager = manager;
        this.comment = comment;
        this.bitmap = bitmap;
        this.isThread = isThread;
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
    
    public Runnable getEditImageRunnable() {
        return editImageRunnable;
    }
    
    public Runnable getEditRunnable() {
        return editRunnable;
    }
    
    public void handleState(int state) {
        manager.handleEditState(this, state);
    }
    
    @Override
    public void setEditThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void setEditImageThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleEditState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchEditRunnable.STATE_EDIT_COMPLETE:
            outState = ThreadManager.EDIT_COMPLETE;
            break;
        case ElasticSearchEditRunnable.STATE_EDIT_FAILED:
            outState = ThreadManager.EDIT_FAILED;
            break;
        default:
            outState = ThreadManager.EDIT_RUNNING;
            break;
        }
        handleState(outState);
    }

    @Override
    public void handleEditImageState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchEditImageRunnable.STATE_EDIT_IMAGE_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case ElasticSearchEditImageRunnable.STATE_EDIT_IMAGE_FAILED:
            outState = ThreadManager.EDIT_IMAGE_FAILED;
            break;
        default:
            outState = ThreadManager.EDIT_IMAGE_RUNNING;
            break;
        }
        handleState(outState);
    }
    
    public Comment getComment() {
        return comment;
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }
    
    public Boolean getIsThread() {
        return isThread;
    }
    
    public void recycle() {
        comment = null;
        manager = null;
        bitmap = null;
        isThread = null;
    }
}

