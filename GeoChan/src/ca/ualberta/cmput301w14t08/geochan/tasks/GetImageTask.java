package ca.ualberta.cmput301w14t08.geochan.tasks;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetImageRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.TaskInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetImageRunnable;

public class GetImageTask implements TaskInterface, GetImageRunnableInterface {

    /*
     * Id of the image as stored on elasticSearch
     */
    private String id;
    private ProgressDialog dialog;
    
    /*
     * Creates a weak reference to the ImageView that this Task will populate.
     * The weak reference prevents memory leaks and crashes, because it
     * automatically tracks the "state" of the variable it backs. If the
     * reference becomes invalid, the weak reference is garbage- collected. This
     * technique is important for referring to objects that are part of a
     * component lifecycle. Using a hard reference may cause memory leaks as the
     * value continues to change; even worse, it can cause crashes if the
     * underlying component is destroyed. Using a weak reference to a View
     * ensures that the reference is more transitory in nature.
     */
    private WeakReference<ImageView> mImageWeakRef;
    private Runnable getImageRunnable;
    private ThreadManager manager;
    private Thread thread;
    private Bitmap cache;

    public GetImageTask() {
        this.getImageRunnable = new GetImageRunnable(this);
    }

    public void initGetImageTask(ThreadManager manager, String id, ImageView imageView, ProgressDialog dialog) {
        this.manager = manager;
        this.id = id;
        this.dialog = dialog;
        // Instantiates the weak reference to the incoming view
        setmImageWeakRef(new WeakReference<ImageView>(imageView));
    }
    
    /**
     * Handles the various possible states of the
     * Runnable that obtains the image.
     * @param state the state
     */
    @Override
    public void handleGetImageState(int state) {
        int outState;
        switch (state) {
        case GetImageRunnable.STATE_GET_IMAGE_COMPLETE:
            outState = ThreadManager.GET_IMAGE_COMPLETE;
            break;
        case GetImageRunnable.STATE_GET_IMAGE_FAILED:
            outState = ThreadManager.GET_IMAGE_FAILED;
            break;
        default:
            outState = ThreadManager.GET_IMAGE_RUNNING;
            break;
        }
        handleState(outState);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void handleState(int state) {
        manager.handleGetImageState(this, state);
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void setCurrentThread(Thread thread) {
        synchronized (manager) {
            this.thread = thread;
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Thread getCurrentThread() {
        synchronized (manager) {
            return thread;
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void recycle() {
        this.id = null;
        this.manager = null;
    }
    
    /* Getters/setters for the interfaces this task implements below */
    
    @Override
    public void setImageCache(Bitmap cache) {
        this.cache = cache;
    }

    @Override
    public Bitmap getImageCache() {
        return cache;
    }
    
    @Override
    public void setGetImageThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    /* Basic getters/setters below */
    
    public Runnable getGetImageRunnable() {
        return getImageRunnable;
    }
    
    public String getId() {
        return id;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public WeakReference<ImageView> getmImageWeakRef() {
        return mImageWeakRef;
    }

    public void setmImageWeakRef(WeakReference<ImageView> mImageWeakRef) {
        this.mImageWeakRef = mImageWeakRef;
    }
}
