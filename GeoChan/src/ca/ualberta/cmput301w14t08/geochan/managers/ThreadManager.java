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
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.loaders.CommentLoader;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchEditTask;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetCommentListTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetCommentTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchPostTask;

public class ThreadManager {

    public static final int POST_FAILED = 1;
    public static final int POST_RUNNING = 2;
    public static final int POST_COMPLETE = 3;
    public static final int UPDATE_FAILED = 4;
    public static final int UPDATE_RUNNING = 5;
    public static final int POST_IMAGE_FAILED = 6;
    public static final int POST_IMAGE_RUNNING = 7;
    public static final int POST_IMAGE_COMPLETE = 8;
    public static final int GET_COMMENT_LIST_FAILED = 9;
    public static final int GET_COMMENT_LIST_RUNNING = 10;
    public static final int GET_COMMENT_LIST_COMPLETE = 11;
    public static final int GET_COMMENT_FAILED = 12;
    public static final int GET_COMMENT_RUNNING = 13;
    public static final int EDIT_FAILED = 14;
    public static final int EDIT_RUNNING = 14;
    public static final int EDIT_COMPLETE = 14;

    // Space for map task states
    public static final int TASK_COMPLETE = 9001;
    
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 5;
    private static final int MAXIMUM_CACHE_SIZE = 1024 * 1024 * 10; // Start at 10MB??
    
    private final LruCache<String, CommentList> elasticSearchCommentListCache;
    private final LruCache<String, Comment> elasticSearchCommentCache;
    
    private final BlockingQueue<Runnable> elasticSearchCommentListRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchCommentRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchImageRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchPostRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchUpdateRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchEditRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchEditImageRunnableQueue;


    private final Queue<ElasticSearchGetCommentListTask> elasticSearchCommentListTaskQueue;
    private final Queue<ElasticSearchGetCommentTask> elasticSearchCommentTaskQueue;
    private final Queue<ElasticSearchPostTask> elasticSearchPostTaskQueue;
    private final Queue<ElasticSearchEditTask> elasticSearchEditTaskQueue;

    private final ThreadPoolExecutor elasticSearchCommentListPool;
    private final ThreadPoolExecutor elasticSearchCommentPool;
    private final ThreadPoolExecutor elasticSearchImagePool;
    private final ThreadPoolExecutor elasticSearchPostPool;
    private final ThreadPoolExecutor elasticSearchUpdatePool;
    private final ThreadPoolExecutor elasticSearchEditPool;
    private final ThreadPoolExecutor elasticSearchEditImagePool;
    
    private Handler handler;
    private static ThreadManager instance = null;
    
    /**
     * Private constructor due to singleton pattern.
     */
    private ThreadManager() {
        elasticSearchCommentListCache = new LruCache<String, CommentList>(MAXIMUM_CACHE_SIZE);
        elasticSearchCommentCache = new LruCache<String, Comment>(MAXIMUM_CACHE_SIZE);
        
        elasticSearchCommentListRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchCommentRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchPostRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchUpdateRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchEditRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchEditImageRunnableQueue = new LinkedBlockingQueue<Runnable>();

        
        elasticSearchCommentListTaskQueue = new LinkedBlockingQueue<ElasticSearchGetCommentListTask>();
        elasticSearchCommentTaskQueue = new LinkedBlockingQueue<ElasticSearchGetCommentTask>();
        elasticSearchPostTaskQueue = new LinkedBlockingQueue<ElasticSearchPostTask>();
        elasticSearchEditTaskQueue = new LinkedBlockingQueue<ElasticSearchEditTask>();

        elasticSearchCommentListPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchCommentListRunnableQueue);
        elasticSearchCommentPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchCommentRunnableQueue);
        elasticSearchImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchImageRunnableQueue);
        elasticSearchPostPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchPostRunnableQueue);
        elasticSearchUpdatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchUpdateRunnableQueue);
        elasticSearchEditPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchEditRunnableQueue);
        elasticSearchEditImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchEditImageRunnableQueue);
        
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
    
    void recycleCommentTask(ElasticSearchGetCommentTask task) {
        task.recycle();
        elasticSearchCommentTaskQueue.offer(task);
    }
    
    void recyclePostTask(ElasticSearchPostTask task) {
        task.recycle();
        elasticSearchPostTaskQueue.offer(task);
    }
}
