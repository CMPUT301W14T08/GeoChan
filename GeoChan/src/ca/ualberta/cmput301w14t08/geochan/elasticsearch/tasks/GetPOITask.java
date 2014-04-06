package ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks;

import android.app.ProgressDialog;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables.GetPOIRunnable;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetPOIRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

public class GetPOITask implements GetPOIRunnableInterface {
    private GeoLocation location;
    private String cache;
    private ProgressDialog dialog;
    private Runnable getPOIRunnable;
    private ThreadManager manager;
    private Thread thread;
    
    public GetPOITask() {
        this.getPOIRunnable = new GetPOIRunnable(this);
    }

    public void initGetPOITask(ThreadManager manager, GeoLocation location, ProgressDialog dialog) {
        this.manager = manager;
        this.dialog= dialog;
        this.location = location;
    }
    
    public void handleState(int state) {
        manager.handleGetPOIState(this, state);
    }
    
    @Override
    public void setGetPOIThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    @Override
    public void handleGetPOIState(int state) {
        int outState;
        switch (state) {
        case GetPOIRunnable.STATE_GET_POI_COMPLETE:
            outState = ThreadManager.GET_POI_COMPLETE;
            break;
        case GetPOIRunnable.STATE_GET_POI_FAILED:
            outState = ThreadManager.GET_POI_FAILED;
            break;
        default:
            outState = ThreadManager.GET_POI_RUNNING;
            break;
        }
        handleState(outState);
    }
    
    @Override
    public void setPOICache(String cache) {   
        this.cache = cache;
    }
    
    @Override
    public String getPOICache() {
        return cache;
    }
    
    public GeoLocation getLocation() {
        return location;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setCurrentThread(Thread thread) {
        synchronized (manager) {
            this.thread = thread;
        }
    }

    public Thread getCurrentThread() {
        synchronized (manager) {
            return thread;
        }
    }
    
    public Runnable getGetPOIRunnable() {
        return getPOIRunnable;
    }
    
    public void recycle() {
        this.location = null;
        this.cache = null;
        this.manager = null;
        setCurrentThread(null);
    }
}
