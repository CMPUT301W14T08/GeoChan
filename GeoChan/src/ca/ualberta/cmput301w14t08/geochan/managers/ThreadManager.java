package ca.ualberta.cmput301w14t08.geochan.managers;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;

public class ThreadManager {

    public static final int POST_FAILED = 1;
    public static final int POST_RUNNING = 2;
    public static final int POST_COMPLETE = 3;
    public static final int UPDATE_FAILED = 4;
    public static final int UPDATE_RUNNING = 5;
    public static final int POST_IMAGE_FAILED = 6;
    public static final int POST_IMAGE_RUNNING = 7;
    public static final int POST_IMAGE_COMPLETE = 8;
    // Space for map task states
    public static final int TASK_COMPLETE = 9001;
    
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 5;
    private static final int MAXIMUM_CACHE_SIZE = 1024 * 1024 * 10; // Start at 10MB??
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    
    private final LruCache<Long, byte[]> ElasticSearchCache;
    
    private final BlockingQueue<Runnable> ElasticSearchPostRunnableQueue;
    private final BlockingQueue<Runnable> ElasticSearchUpdateRunnableQueue;
    private final Queue<ElasticSearchTask> ElasticSearchTaskQueue;
    private final ThreadPoolExecutor ElasticSearchPostPool;
    private final ThreadPoolExecutor ElasticSearchUpdatePool;
    
    private Handler handler;
    private static ThreadManager instance = null;    
    
    /**
     * Private constructor due to singleton pattern.
     */
    private ThreadManager() {
        ElasticSearchCache = new LruCache<Long, byte[]>(MAXIMUM_CACHE_SIZE);
        ElasticSearchPostRunnableQueue = new LinkedBlockingQueue<Runnable>();
        ElasticSearchUpdateRunnableQueue = new LinkedBlockingQueue<Runnable>();
        ElasticSearchTaskQueue = new LinkedBlockingQueue<ElasticSearchTask>();
        ElasticSearchPostPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, ElasticSearchPostRunnableQueue);
        ElasticSearchUpdatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, ElasticSearchUpdateRunnableQueue);

        
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
    
    public static ElasticSearchTask startPost(Comment comment, String title) {
        if (instance == null) {
            generateInstance();
        }
        ElasticSearchTask task = instance.ElasticSearchTaskQueue.poll();
        if (task == null) {
            task = new ElasticSearchTask();
        }
        task.initPostTask(ThreadManager.instance, comment, title);
        instance.ElasticSearchPostPool.execute(task.getPostRunnable());
        return task;
    }
    
    public void handleState(ElasticSearchTask task, int state) {
        switch(state) {
        case POST_COMPLETE:
            instance.ElasticSearchUpdatePool.execute(task.getUpdateRunnable());
            break;
        case TASK_COMPLETE:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }
}
