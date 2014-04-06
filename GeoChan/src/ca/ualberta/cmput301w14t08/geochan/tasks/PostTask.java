package ca.ualberta.cmput301w14t08.geochan.tasks;

import ca.ualberta.cmput301w14t08.geochan.interfaces.PostImageRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.PostRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.UpdateRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.runnables.PostImageRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.PostRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.UpdateRunnable;

public class PostTask implements PostImageRunnableInterface, PostRunnableInterface,
        UpdateRunnableInterface {
    private Comment comment;
    private String title;
    private ThreadManager manager;
    private Thread thread;
    private Runnable imageRunnable;
    private Runnable postRunnable;
    private Runnable updateRunnable;

    public PostTask() {
        imageRunnable = new PostImageRunnable(this);
        postRunnable = new PostRunnable(this);
        updateRunnable = new UpdateRunnable(this);
    }

    public void initPostTask(ThreadManager manager, Comment comment, String title) {
        this.manager = manager;
        this.comment = comment;
        this.title = title;
    }

    public Thread getCurrentThread() {
        synchronized (manager) {
            return thread;
        }
    }

    public void setCurrentThread(Thread thread) {
        synchronized (manager) {
            this.thread = thread;
        }
    }

    public Runnable getImageRunnable() {
        return imageRunnable;
    }

    public Runnable getPostRunnable() {
        return postRunnable;
    }

    public Runnable getUpdateRunnable() {
        return updateRunnable;
    }

    public void handleState(int state) {
        manager.handlePostState(this, state);
    }

    @Override
    public void setImageThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handleImageState(int state) {
        int outState;
        switch (state) {
        case PostImageRunnable.STATE_IMAGE_COMPLETE:
            outState = ThreadManager.POST_IMAGE_COMPLETE;
            break;
        case PostImageRunnable.STATE_IMAGE_FAILED:
            outState = ThreadManager.POST_IMAGE_FAILED;
            break;
        default:
            outState = ThreadManager.POST_IMAGE_RUNNING;
            break;
        }
        handleState(outState);
    }

    @Override
    public void setPostThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public void handlePostState(int state) {
        int outState;
        switch (state) {
        case PostRunnable.STATE_POST_COMPLETE:
            outState = ThreadManager.POST_COMPLETE;
            break;
        case PostRunnable.STATE_POST_FAILED:
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
        switch (state) {
        case UpdateRunnable.STATE_UPDATE_COMPLETE:
            outState = ThreadManager.TASK_COMPLETE;
            break;
        case UpdateRunnable.STATE_UPDATE_FAILED:
            outState = ThreadManager.UPDATE_FAILED;
            break;
        default:
            outState = ThreadManager.UPDATE_RUNNING;
            break;
        }
        handleState(outState);
    }

    public void recycle() {
        comment = null;
        manager = null;
        title = null;
    }

    public Comment getComment() {
        return comment;
    }

    public String getTitle() {
        return title;
    }
}
