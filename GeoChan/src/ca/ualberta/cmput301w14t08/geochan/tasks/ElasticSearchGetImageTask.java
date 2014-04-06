package ca.ualberta.cmput301w14t08.geochan.tasks;

import java.lang.ref.WeakReference;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetImageRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.runnables.ElasticSearchGetImageRunnable;

public class ElasticSearchGetImageTask implements GetImageRunnableInterface {

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

    public ElasticSearchGetImageTask() {
        this.getImageRunnable = new ElasticSearchGetImageRunnable(this);
    }

    public void initGetImageTask(ThreadManager manager, String id, ImageView imageView, ProgressDialog dialog) {
        this.manager = manager;
        this.id = id;
        this.dialog = dialog;
        // Instantiates the weak reference to the incoming view
        setmImageWeakRef(new WeakReference<ImageView>(imageView));
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
        switch (state) {
        case ElasticSearchGetImageRunnable.STATE_GET_IMAGE_COMPLETE:
            outState = ThreadManager.GET_IMAGE_COMPLETE;
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

    public ProgressDialog getDialog() {
        return dialog;
    }

    public WeakReference<ImageView> getmImageWeakRef() {
        return mImageWeakRef;
    }

    public void setmImageWeakRef(WeakReference<ImageView> mImageWeakRef) {
        this.mImageWeakRef = mImageWeakRef;
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
