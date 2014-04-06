package ca.ualberta.cmput301w14t08.geochan.managers;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetCommentsTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetImageTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchPostTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetPOITask;

/**
 * Responsible for managing various threads that require to run in the
 * background and communicate with the network. Can set up, launch given tasks,
 * monitor their state, and react to state changes by communicating with the UI.
 * Is a singleton.
 * 
 * Created using the tutorial at
 * http://developer.android.com/training/multiple-threads/create-threadpool.html
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
    public static final int GET_COMMENTS_FAILED = 12;
    public static final int GET_COMMENTS_RUNNING = 13;
    public static final int GET_COMMENTS_COMPLETE = 14;
    // Retrieve an bitmap image from elasticSearch
    public static final int GET_IMAGE_FAILED = 15;
    public static final int GET_IMAGE_RUNNING = 16;
    public static final int GET_IMAGE_COMPLETE = 17;
    // Space for map task states
    public static final int GET_POI_FAILED = 18;
    public static final int GET_POI_RUNNING = 19;
    public static final int GET_POI_COMPLETE = 20;

    public static final int TASK_COMPLETE = 9001;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int MAXIMUM_CACHE_SIZE = 1024 * 1024 * 10; // Start at
                                                                    // 10MB??

    // Caches for download tasks
    private final LruCache<String, CommentList> elasticSearchCommentListCache;
    private final LruCache<String, Bitmap> elasticSearchGetImageCache;
    private final LruCache<String, String> getPOICache;

    // Queues of runnables required by tasks
    // es GetCommentList task
    private final BlockingQueue<Runnable> elasticSearchGetCommentListRunnableQueue;
    // es GetComment task
    private final BlockingQueue<Runnable> elasticSearchGetCommentsRunnableQueue;
    // es Post task
    private final BlockingQueue<Runnable> elasticSearchImageRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchPostRunnableQueue;
    private final BlockingQueue<Runnable> elasticSearchUpdateRunnableQueue;
    // es GetImage task
    private final BlockingQueue<Runnable> elasticSearchGetImageRunnableQueue;
    // get Point of Interest Task
    private final BlockingQueue<Runnable> GetPOIRunnableQueue;

    // Queues of tasks this manager is responsible for
    private final Queue<ElasticSearchGetCommentsTask> elasticSearchGetCommentsTaskQueue;
    private final Queue<ElasticSearchPostTask> elasticSearchPostTaskQueue;
    private final Queue<ElasticSearchGetImageTask> elasticSearchGetImageTaskQueue;
    private final Queue<GetPOITask> getPOITaskQueue;

    // Thread pools for all the possible threads, one pool per each runnable
    private final ThreadPoolExecutor elasticSearchGetCommentListPool;
    private final ThreadPoolExecutor elasticSearchGetCommentsPool;
    private final ThreadPoolExecutor elasticSearchImagePool;
    private final ThreadPoolExecutor elasticSearchPostPool;
    private final ThreadPoolExecutor elasticSearchUpdatePool;
    private final ThreadPoolExecutor elasticSearchGetImagePool;
    private final ThreadPoolExecutor getPOIPool;

    private Handler handler;
    private static ThreadManager instance = null;

    /**
     * Private constructor due to singleton pattern.
     */
    private ThreadManager() {
        elasticSearchCommentListCache = new LruCache<String, CommentList>(MAXIMUM_CACHE_SIZE);
        elasticSearchGetImageCache = new LruCache<String, Bitmap>(MAXIMUM_CACHE_SIZE);
        getPOICache = new LruCache<String, String>(MAXIMUM_CACHE_SIZE);

        elasticSearchGetCommentListRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchGetCommentsRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchPostRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchUpdateRunnableQueue = new LinkedBlockingQueue<Runnable>();
        elasticSearchGetImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        GetPOIRunnableQueue = new LinkedBlockingQueue<Runnable>();

        elasticSearchGetCommentsTaskQueue = new LinkedBlockingQueue<ElasticSearchGetCommentsTask>();
        elasticSearchPostTaskQueue = new LinkedBlockingQueue<ElasticSearchPostTask>();
        elasticSearchGetImageTaskQueue = new LinkedBlockingQueue<ElasticSearchGetImageTask>();
        getPOITaskQueue = new LinkedBlockingQueue<GetPOITask>();

        elasticSearchGetCommentListPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchGetCommentListRunnableQueue);
        elasticSearchGetCommentsPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchGetCommentsRunnableQueue);
        elasticSearchImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchImageRunnableQueue);
        elasticSearchPostPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchPostRunnableQueue);
        elasticSearchUpdatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchUpdateRunnableQueue);
        elasticSearchGetImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, elasticSearchGetImageRunnableQueue);
        getPOIPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, GetPOIRunnableQueue);

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                case TASK_COMPLETE:
                    Toaster.toastShort("YAY!!! :D");
                    break;

                case GET_COMMENTS_COMPLETE:
                    ElasticSearchGetCommentsTask task = (ElasticSearchGetCommentsTask) inputMessage.obj;
                    task.getFragment().finishReload();
                    recycleCommentsTask(task);
                    break;
                    
                case GET_COMMENTS_FAILED:
                    ElasticSearchGetCommentsTask taskFail = (ElasticSearchGetCommentsTask) inputMessage.obj;
                    taskFail.getFragment().finishReload();
                    recycleCommentsTask(taskFail);
                    break;    
                    
                case GET_COMMENT_LIST_FAILED:
                    ElasticSearchGetCommentsTask taskListFail = (ElasticSearchGetCommentsTask) inputMessage.obj;
                    taskListFail.getFragment().finishReload();
                    recycleCommentsTask(taskListFail);
                    break;

                case GET_IMAGE_RUNNING:
                    ElasticSearchGetImageTask imageTask = (ElasticSearchGetImageTask) inputMessage.obj;
                    imageTask.getDialog().show();
                    break;
                    
                case GET_IMAGE_FAILED:
                    ElasticSearchGetImageTask imageTaskFail = (ElasticSearchGetImageTask) inputMessage.obj;
                    imageTaskFail.getDialog().dismiss();
                    recycleGetImageTask(imageTaskFail);
                    break;
                    
                case GET_IMAGE_COMPLETE:
                    ElasticSearchGetImageTask imageTaskComplete = (ElasticSearchGetImageTask) inputMessage.obj;
                    imageTaskComplete.getDialog().dismiss();
                    Bitmap bitmap = imageTaskComplete.getImageCache();
                    String id = imageTaskComplete.getId();
                    imageTaskComplete.getmImageWeakRef().get().setImageBitmap(bitmap);
                    CacheManager.getInstance().serializeImage(bitmap, id);
                    recycleGetImageTask(imageTaskComplete);
                    break;

                case GET_POI_RUNNING:
                    GetPOITask poiTaskRunning = (GetPOITask) inputMessage.obj;
                    if (poiTaskRunning.getDialog() != null) {
                        poiTaskRunning.getDialog().show();
                    }
                    break;
                    
                case GET_POI_COMPLETE:
                    GetPOITask poiTaskComplete = (GetPOITask) inputMessage.obj;
                    if (poiTaskComplete.getDialog() != null) {
                        poiTaskComplete.getDialog().dismiss();
                    }
                    poiTaskComplete.getLocation().setLocationDescription(
                            poiTaskComplete.getPOICache());
                    recycleGetPOITask(poiTaskComplete);
                    break;
                    
                case GET_POI_FAILED:
                    GetPOITask poiTaskFailed = (GetPOITask) inputMessage.obj;
                    if (poiTaskFailed.getDialog() != null) {
                        poiTaskFailed.getDialog().dismiss();
                    }
                    poiTaskFailed.getLocation().setLocationDescription(poiTaskFailed.getPOICache());
                    recycleGetPOITask(poiTaskFailed);
                    break;
                    
                default:
                    super.handleMessage(inputMessage);
                    break;
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
     * 
     * @param id
     *            the image id under which the bitmap is stored on es
     */
    public static ElasticSearchGetImageTask startGetImage(String id, ImageView imageView,
            ProgressDialog dialog) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchGetImageTask task = instance.elasticSearchGetImageTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchGetImageTask();
        }
        task.initGetImageTask(instance, id, imageView, dialog);
        task.setImageCache(instance.elasticSearchGetImageCache.get(id));
        instance.elasticSearchGetImagePool.execute(task.getGetImageRunnable());
        return task;
    }

    /**
     * Start the get commentList from elasticSearch task, initialize a task
     * instance and add the appropriate runnable to the thread pool. After the list
     * is obtained, gets the comments.
     * 
     * @param loader
     *            commentLoader object required
     * @param id
     *            id of the commentList used on the server
     */
    public static ElasticSearchGetCommentsTask startGetComments(ThreadViewFragment fragment,
            int threadIndex, ProgressDialog dialog) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchGetCommentsTask task = instance.elasticSearchGetCommentsTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchGetCommentsTask();
        }
        task.initCommentsTask(instance, fragment, threadIndex, dialog);
        task.setCommentListCache(instance.elasticSearchCommentListCache.get(ThreadList.getThreads().get(threadIndex).getId()));
        instance.elasticSearchGetCommentListPool.execute(task.getGetCommentListRunnable());
        return task;
    }

    /**
     * Start the post comment to elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * 
     * @param comment
     *            comment object to be posted
     * @param title
     *            title of the threadComment, if it is a threadComment. if it is
     *            a Comment, not a threadComment, the title field is null
     */
    public static ElasticSearchPostTask startPost(Comment comment, String title) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchPostTask task = instance.elasticSearchPostTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchPostTask();
        }
        task.initPostTask(instance, comment, title);
        instance.elasticSearchPostPool.execute(task.getPostRunnable());
        return task;
    }

    public static GetPOITask startGetPOI(GeoLocation location, ProgressDialog dialog) {
        if (instance == null) {
            generateInstance();
        }
        GetPOITask task = instance.getPOITaskQueue.poll();
        if (task == null) {
            task = new GetPOITask();
        }
        task.initGetPOITask(instance, location, dialog);
        task.setPOICache(instance.getPOICache.get(location.getLocation().toString()));
        instance.getPOIPool.execute(task.getGetPOIRunnable());
        return task;
    }

    public void handleGetPOIState(GetPOITask task, int state) {
        switch (state) {
        case GET_POI_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_POI_RUNNING:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_POI_FAILED:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }

    /**
     * Handle the possible states of the getComment task. As of now, just wait
     * until task is complete.
     * 
     * @param task
     * @param state
     */
    public void handleGetCommentsState(ElasticSearchGetCommentsTask task, int state) {
        switch (state) {
        case GET_COMMENT_LIST_COMPLETE:
            instance.elasticSearchGetCommentsPool.execute(task.getGetCommentsRunnable());
            break;
        case GET_COMMENTS_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_COMMENT_LIST_RUNNING:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_COMMENT_LIST_FAILED:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_COMMENTS_FAILED:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }

    /**
     * Handle the possible states of the getImage task. As of now, just wait
     * until task is complete.
     * 
     * @param task
     * @param state
     */
    public void handleGetImageState(ElasticSearchGetImageTask task, int state) {
        switch (state) {
        case GET_IMAGE_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_IMAGE_RUNNING:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_IMAGE_FAILED:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }

    /**
     * Handle the possible states of the Post task. Once post is complete, if
     * there is an image, start the Image runnable, which posts the attached
     * image to elasticSearch. If not, or after the image is complete, start the
     * update runnable, which updates the commentList on elasticSearch
     * 
     * @param task
     * @param state
     */
    public void handlePostState(ElasticSearchPostTask task, int state) {
        switch (state) {
        case POST_COMPLETE:
            if (task.getComment().hasImage()) {
                instance.elasticSearchImagePool.execute(task.getImageRunnable());
            } else if (task.getTitle() == null) {
                instance.elasticSearchUpdatePool.execute(task.getUpdateRunnable());
            } else {
                instance.handler.obtainMessage(TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case POST_IMAGE_COMPLETE:
            if (task.getTitle() == null) {
                instance.elasticSearchUpdatePool.execute(task.getUpdateRunnable());
            } else {
                instance.handler.obtainMessage(TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case TASK_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }

    void recycleCommentsTask(ElasticSearchGetCommentsTask task) {
        task.recycle();
        instance.elasticSearchGetCommentsTaskQueue.offer(task);
    }

    void recycleGetImageTask(ElasticSearchGetImageTask task) {
        task.recycle();
        instance.elasticSearchGetImageTaskQueue.offer(task);
    }

    void recyclePostTask(ElasticSearchPostTask task) {
        task.recycle();
        instance.elasticSearchPostTaskQueue.offer(task);
    }

    void recycleGetPOITask(GetPOITask task) {
        task.recycle();
        instance.getPOITaskQueue.offer(task);
    }
}
