package ca.ualberta.cmput301w14t08.geochan.tasks;

import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentListRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentsRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetCommentListRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetCommentsRunnable;

public class GetCommentsTask implements GetCommentListRunnableInterface, GetCommentsRunnableInterface {

    private int threadIndex;
    private CommentList cache;
    private ThreadViewFragment fragment;
    private Runnable getCommentListRunnable;
    private Runnable getCommentsRunnable;
    private ThreadManager manager;
    private Thread thread;

    public GetCommentsTask() {
        this.getCommentListRunnable = new GetCommentListRunnable(this);
        this.getCommentsRunnable = new GetCommentsRunnable(this);
    }

    public void initCommentsTask(ThreadManager manager, ThreadViewFragment fragment, int threadIndex) {
        this.manager = manager;
        this.fragment = fragment;
        this.threadIndex = threadIndex;
    }

    public void handleState(int state) {
        manager.handleGetCommentsState(this, state);
    }

    @Override
    public void setGetCommentListThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleGetCommentListState(int state) {
        int outState;
        switch (state) {
        case GetCommentListRunnable.STATE_GET_LIST_COMPLETE:
            outState = ThreadManager.GET_COMMENT_LIST_COMPLETE;
            break;
        case GetCommentListRunnable.STATE_GET_LIST_FAILED:
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

    public int getThreadIndex() {
        return threadIndex;
    }

    public ThreadViewFragment getFragment() {
        return fragment;
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

    public Runnable getGetCommentListRunnable() {
        return getCommentListRunnable;
    }
    
    public Runnable getGetCommentsRunnable() {
        return getCommentsRunnable;
    }

    public void recycle() {
        this.threadIndex = -1;
        this.cache = null;
        this.manager = null;
        this.fragment = null;
    }

    @Override
    public void setGetCommentsThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleGetCommentsState(int state) {
        int outState;
        switch (state) {
        case GetCommentsRunnable.STATE_GET_COMMENTS_COMPLETE:
            outState = ThreadManager.GET_COMMENTS_COMPLETE;
            break;
        case GetCommentsRunnable.STATE_GET_COMMENTS_FAILED:
            outState = ThreadManager.GET_COMMENTS_FAILED;
            break;
        default:
            outState = ThreadManager.GET_COMMENTS_RUNNING;
            break;
        }
        handleState(outState);
    }
}