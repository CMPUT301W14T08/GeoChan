package ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks;

import android.graphics.Bitmap;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables.ElasticSearchGetImageRunnable;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetImageRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;

public class ElasticSearchGetImageTask implements GetImageRunnableInterface {
    
    private String id;
    private Runnable getImageRunnable;
    private ThreadManager manager;
    private Thread thread;
    private Bitmap cache;
    
    public ElasticSearchGetImageTask() {
        this.getImageRunnable = new ElasticSearchGetImageRunnable(this);
    }
    
    public void initGetImageTask(ThreadManager manager, String id) {
        this.manager = manager;
        this.id = id;
    }
    
    public void handleState(int state) {
        manager.handleGetImageState(this, state);
    }

    @Override
    public void setGetImageThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    @Override
    public void handleGetImageState(int state) {
        int outState;
        switch(state) {
        case ElasticSearchGetImageRunnable.STATE_GET_IMAGE_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case ElasticSearchGetImageRunnable.STATE_GET_IMAGE_FAILED:
            outState = ThreadManager.GET_IMAGE_FAILED;
            break;
        default:
            outState = ThreadManager.GET_IMAGE_RUNNING;
            break;
        }
        handleState(outState);
    }
    
    public String getId() {
        return id;
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
    
    public Runnable getGetImageRunnable() {
        return getImageRunnable;
    }
    
    
    @Override
    public void setImageCache(Bitmap cache) {
        this.cache = cache;
    }

    @Override
    public Bitmap getImageCache() {
        return cache;
    }
    
    public void recycle() {
        this.id = null;
        this.manager = null;
    }
}
