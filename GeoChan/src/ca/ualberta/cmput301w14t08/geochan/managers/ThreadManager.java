package ca.ualberta.cmput301w14t08.geochan.managers;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchEditTask;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetCommentListTask;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetCommentTask;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetImageTask;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchPostTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;

/**
 * Responsible for managing various threads that require to run
 * in the background and communicate with the network. 
 * Can set up, launch given tasks, monitor their state, and react
 * to state changes by communicating with the UI. Is a singleton.
 * 
 * Created using the tutorial at http://developer.android.com/training/multiple-threads/create-threadpool.html
 * 
 * @author Artem Herasymchuk, Artem Chikin
 *
 */
public class ThreadManager {
    // These are the states of all tasks this manager handles
    // Post a comment to elasticSearch
    public static final int POST_FAILED = 1;
    public static final int POST_RUNNING = 2;
    public static final int POST_COMPLETE = 3;
    // Update the commentList on elasticSearch
    public static final int UPDATE_FAILED = 4;
    public static final int UPDATE_RUNNING = 5;
    // Post an image to elasticSearch
    public static final int POST_IMAGE_FAILED = 6;
    public static final int POST_IMAGE_RUNNING = 7;
    public static final int POST_IMAGE_COMPLETE = 8;
    // Retrieve commentList from elasticSearch
    public static final int GET_COMMENT_LIST_FAILED = 9;
    public static final int GET_COMMENT_LIST_RUNNING = 10;
    public static final int GET_COMMENT_LIST_COMPLETE = 11;
    // Retrieve single comment from elasticSearch
    public static final int GET_COMMENT_FAILED = 12;
    public static final int GET_COMMENT_RUNNING = 13;
    // Edit a comment or thread on elasticSearch
    public static final int EDIT_FAILED = 14;
    public static final int EDIT_RUNNING = 15;
    public static final int EDIT_COMPLETE = 16;
    // Edit an image on elasticSearch
    public static final int EDIT_IMAGE_FAILED = 17;
    public static final int EDIT_IMAGE_RUNNING = 18;
    // Retrieve an bitmap image from elasticSearch
    public static final int GET_IMAGE_FAILED = 19;
    public static final int GET_IMAGE_RUNNING = 20;
    // Space for map task states
    
    public static final int TASK_COMPLETE = 9001;
    
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 5;
    private static final int MAXIMUM_CACHE_SIZE = 1024 * 1024 * 10; // Start at 10MB??
    
    // Caches for download tasks
    private final LruCache<String, CommentList> elasticSearchCommentListCache;
    private final LruCache<String, Comment> elasticSearchCommentCache;
    private final LruCache<String, Bitmap> elasticSearchGetImageCache;

    // Queues of runnables required by tasks
    // es GetCommentList task
    private final BlockingQueue<Runnable> elasticSearchCommentListRunnableQueue;
    // es GetComment task
    private final BlockingQueue<Runnable> elasticSearchCommentRunnableQueue;
    // es Post task
    private final BlockingQueue<Runnable> elasticSearchImageRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchPostRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchUpdateRunnableQueue;
    // es Edit task
    private final BlockingQueue<Runnable> elasticSearchEditRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchEditImageRunnableQueue;
    // es GetImage task
    private final BlockingQueue<Runnable> elasticSearchGetImageRunnableQueue;


    // Queues of tasks this manager is responsible for
    private final Queue<ElasticSearchGetCommentListTask> elasticSearchCommentListTaskQueue;
    private final Queue<ElasticSearchGetCommentTask> elasticSearchCommentTaskQueue;
    private final Queue<ElasticSearchPostTask> elasticSearchPostTaskQueue;
    private final Queue<ElasticSearchEditTask> elasticSearchEditTaskQueue;
    private final Queue<ElasticSearchGetImageTask> elasticSearchGetImageTaskQueue;

    // Thread pools for all the possible threads, one pool per each runnable
    private final ThreadPoolExecutor elasticSearchCommentListPool;
    private final ThreadPoolExecutor elasticSearchCommentPool;
    private final ThreadPoolExecutor elasticSearchImagePool;
    private final ThreadPoolExecutor elasticSearchPostPool;
    private final ThreadPoolExecutor elasticSearchUpdatePool;
    private final ThreadPoolExecutor elasticSearchEditPool;
    private final ThreadPoolExecutor elasticSearchEditImagePool;
    private final ThreadPoolExecutor elasticSearchGetImagePool;

    
    private Handler handler;
    private static ThreadManager instance = null;
    
    /**
     * Private constructor due to singleton pattern.
     */
    private ThreadManager() {
        elasticSearchCommentListCache = new LruCache<String, CommentList>(MAXIMUM_CACHE_SIZE);
        elasticSearchCommentCache = new LruCache<String, Comment>(MAXIMUM_CACHE_SIZE);
        elasticSearchGetImageCache = new LruCache<String, Bitmap>(MAXIMUM_CACHE_SIZE);
        
        elasticSearchCommentListRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchCommentRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchPostRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchUpdateRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchEditRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchEditImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchGetImageRunnableQueue = new LinkedBlockingQueue<Runnable>();


        elasticSearchCommentListTaskQueue = new LinkedBlockingQueue<ElasticSearchGetCommentListTask>();
        elasticSearchCommentTaskQueue = new LinkedBlockingQueue<ElasticSearchGetCommentTask>();
        elasticSearchPostTaskQueue = new LinkedBlockingQueue<ElasticSearchPostTask>();
        elasticSearchEditTaskQueue = new LinkedBlockingQueue<ElasticSearchEditTask>();
        elasticSearchGetImageTaskQueue = new LinkedBlockingQueue<ElasticSearchGetImageTask>();


        elasticSearchCommentListPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchCommentListRunnableQueue);
        elasticSearchCommentPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchCommentRunnableQueue);
        elasticSearchImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchImageRunnableQueue);
        elasticSearchPostPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchPostRunnableQueue);
        elasticSearchUpdatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchUpdateRunnableQueue);
        elasticSearchEditPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchEditRunnableQueue);
        elasticSearchEditImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchEditImageRunnableQueue);
        elasticSearchGetImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchGetImageRunnableQueue);

        
        handler = new Handler(Looper.getMainLooper()) {
            
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                case TASK_COMPLETE:
                    Toaster.toastShort("YAY!!! :D");
                    break;
                default:
                    super.handleMessage(inputMessage);
                }
            }
            
        };
    }
    
    public static void generateInstance() {
        instance = new ThreadManager();
    }
    
    /**
     * Start the get image from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * @param id
     *          the image id under which the bitmap is stored on es
     */
    public static ElasticSearchGetImageTask startGetImage(String id) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchGetImageTask task = instance.elasticSearchGetImageTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchGetImageTask();
        }
        task.initGetImageTask(ThreadManager.instance, id);
        task.setImageCache(instance.elasticSearchGetImageCache.get(id));
        instance.elasticSearchGetImagePool.execute(task.getGetImageRunnable());
        return task;
    }
    
    /**
     * Start the get commentList from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * @param loader
     *              commentLoader object required
     * @param id
     *          id of the commentList used on the server
     */
    public static ElasticSearchGetCommentListTask startGetCommentList(CommentLoader loader, String id) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchGetCommentListTask task = instance.elasticSearchCommentListTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchGetCommentListTask();
        }
        task.initCommentListTask(ThreadManager.instance, loader, id);
        task.setCommentListCache(instance.elasticSearchCommentListCache.get(id));
        instance.elasticSearchCommentListPool.execute(task.getGetCommentListRunnable());
        return task;
    }
    
    /**
     * Start the get comment from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * @param loader
     *              commentLoader object required
     * @param id
     *          id of the commentList used on the server
     */
    public static ElasticSearchGetCommentTask startGetComment(CommentLoader loader, String id) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchGetCommentTask task = instance.elasticSearchCommentTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchGetCommentTask();
        }
        task.initCommentTask(ThreadManager.instance, loader, id);
        task.setCommentCache(instance.elasticSearchCommentCache.get(id));
        instance.elasticSearchCommentListPool.execute(task.getGetCommentRunnable());
        return task;
    }
    
    /**
     * Start the post comment to elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * @param comment
     *              comment object to be posted
     * @param title
     *          title of the threadComment, if it is a threadComment. 
     *          if it is a Comment, not a threadComment, the title field is null 
     */
    public static ElasticSearchPostTask startPost(Comment comment, String title) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchPostTask task = instance.elasticSearchPostTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchPostTask();
        }
        task.initPostTask(ThreadManager.instance, comment, title);
        instance.elasticSearchPostPool.execute(task.getPostRunnable());
        return task;
    }
    
    /**
     * Start the edit comment on elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * @param comment
     *              comment object to be edited
     * @param bitmap
     *          edited image of the comment, left null if image has not been edited
     * @param isThread
     *          boolean used to determine if we are editing a comment or a threadComment bodyComment         
     */
    public static ElasticSearchEditTask startEdit(Comment comment, Bitmap bitmap, Boolean isThread) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchEditTask task = instance.elasticSearchEditTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchEditTask();
        }
        task.initEditTask(ThreadManager.instance, comment, bitmap, isThread);
        instance.elasticSearchEditPool.execute(task.getEditRunnable());
        return task;
    }
    
    /** 
     * Handle the possible states of the getCommentList task. 
     * As of now, just wait until task is complete.
     * @param task
     * @param state
     */
    public void handleGetCommentListState(ElasticSearchGetCommentListTask task, int state) {
        switch(state) {
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }
    
    /** 
     * Handle the possible states of the getComment task. 
     * As of now, just wait until task is complete.
     * @param task
     * @param state
     */
    public void handleGetCommentState(ElasticSearchGetCommentTask task, int state) {
        switch(state) {
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }
    
    /** 
     * Handle the possible states of the getImage task. 
     * As of now, just wait until task is complete.
     * @param task
     * @param state
     */
    public void handleGetImageState(ElasticSearchGetImageTask task, int state) {
        switch(state) {
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }
    
    /** 
     * Handle the possible states of the Post task. 
     * Once post is complete, if there is an image, start the Image runnable, which
     * posts the attached image to elasticSearch.
     * If not, or after the image is complete, start the update runnable, 
     * which updates the commentList on elasticSearch
     * @param task
     * @param state
     */
    public void handlePostState(ElasticSearchPostTask task, int state) {
        switch(state) {
        case POST_COMPLETE:
            if (task.getComment().hasImage()) {
                instance.elasticSearchImagePool.execute(task.getImageRunnable());
            } else if (task.getTitle() == null) {
                instance.elasticSearchUpdatePool.execute(task.getUpdateRunnable());
            } else {
                handler.obtainMessage(TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case POST_IMAGE_COMPLETE:
            if (task.getTitle() == null) {
                instance.elasticSearchUpdatePool.execute(task.getUpdateRunnable());
            } else {
                handler.obtainMessage(TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }
    
    /** 
     * Handle the possible states of the Edit task. 
     * Once Edit is complete, if there is an edited image, start the 
     * EditImage runnable. Otherwise, the task is complete.
     * @param task
     * @param state
     */
    public void handleEditState(ElasticSearchEditTask task, int state) {
        switch(state) {
        case EDIT_COMPLETE:
            if (task.getBitmap() != null) {
                instance.elasticSearchEditImagePool.execute(task.getEditImageRunnable());
            } else {
                handler.obtainMessage(TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;            
        }
    }
    
    
    void recycleCommentTask(ElasticSearchGetCommentTask task) {
        task.recycle();
        elasticSearchCommentTaskQueue.offer(task);
    }
    
    void recycleGetImageTask(ElasticSearchGetImageTask task) {
        task.recycle();
        elasticSearchGetImageTaskQueue.offer(task);
    }
    
    void recyclePostTask(ElasticSearchPostTask task) {
        task.recycle();
        elasticSearchPostTaskQueue.offer(task);
    }
    
    void recycleEditTask(ElasticSearchEditTask task) {
        task.recycle();
        elasticSearchEditTaskQueue.offer(task);
    }
}
