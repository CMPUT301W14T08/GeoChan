package ca.ualberta.cmput301w14t08.geochan.tasks;

import android.app.ProgressDialog;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetThreadCommentsRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetThreadCommentsRunnable;

public class GetThreadCommentsTask implements GetThreadCommentsRunnableInterface {

    private ProgressDialog dialog;
    private Runnable getThreadCommentsRunnable;
    private ThreadListFragment fragment;
    private ThreadManager manager;
    private Thread thread;
    
    public GetThreadCommentsTask() {
        this.getThreadCommentsRunnable = new GetThreadCommentsRunnable(this);
    }
    
    public void initGetThreadCommentsTask(ThreadManager manager, ThreadListFragment fragment, ProgressDialog dialog) {
        this.manager = manager;
        this.fragment = fragment;
        this.dialog = dialog;
    }
    
    public void handleState(int state) {
        manager.handleGetThreadCommentsState(this, state);
    }
    
    @Override
    public void setGetThreadCommentsThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleGetThreadCommentsState(int state) {
        int outState;
        switch(state) {
        case GetThreadCommentsRunnable.STATE_GET_THREADS_COMPLETE:
            outState = ThreadManager.GET_THREADS_COMPLETE;
            break;
        case GetThreadCommentsRunnable.STATE_GET_THREADS_FAILED:
            outState = ThreadManager.GET_THREADS_FAILED;
            break;
        default:
            outState = ThreadManager.GET_THREADS_RUNNING;
            break;
        }
        handleState(outState);
    }

    public ThreadListFragment getFragment() {
        return fragment;
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
    
    public Runnable getGetThreadCommentsRunnable() {
        return getThreadCommentsRunnable;
    }
    
    public void recycle() {
        this.manager = null;
        this.fragment = null;
        this.dialog = null;
    }
}
