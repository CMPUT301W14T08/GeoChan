/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.managers;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osmdroid.bonuspack.overlays.Marker;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.helpers.Toaster;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetCommentsTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetImageTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetPOITask;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetThreadCommentsTask;
import ca.ualberta.cmput301w14t08.geochan.tasks.PostTask;

/**
 * Responsible for managing various threads that require to run in the
 * background and communicate with the network. Can set up, launch given tasks,
 * monitor their state, and react to state changes by communicating with the UI.
 * Is a singleton.
 * 
 * Created using the tutorial at
 * http://developer.android.com/training/multiple-threads/create-threadpool.html
 * 
 * @author Artem Herasymchuk
 * @author Artem Chikin
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
    // Get a point of interest
    public static final int GET_POI_FAILED = 18;
    public static final int GET_POI_RUNNING = 19;
    public static final int GET_POI_COMPLETE = 20;
    // Retrieve the threadList from elasticSearch
    public static final int GET_THREADS_FAILED = 21;
    public static final int GET_THREADS_RUNNING = 22;
    public static final int GET_THREADS_COMPLETE = 23;
    // Get a point of interest during a post
    public static final int POST_GET_POI_FAILED = 24;
    public static final int POST_GET_POI_RUNNING = 25;
    public static final int POST_GET_POI_COMPLETE = 26;
    public static final int POST_TASK_COMPLETE = 27;

    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int MAXIMUM_CACHE_SIZE = 1024 * 1024 * 10; // Start at
                                                                    // 10MB??

    // Caches for download tasks
    private final LruCache<String, CommentList> commentListCache;
    private final LruCache<String, Bitmap> getImageCache;
    private final LruCache<String, String> getPOICache;

    // Queues of runnables required by tasks
    // es GetCommentList task
    private final BlockingQueue<Runnable> getCommentListRunnableQueue;
    // es GetComment task
    private final BlockingQueue<Runnable> getCommentsRunnableQueue;
    // es Post task
    private final BlockingQueue<Runnable> postImageRunnableQueue;
    private final BlockingQueue<Runnable> postRunnableQueue;
    private final BlockingQueue<Runnable> updateRunnableQueue;
    // es GetImage task
    private final BlockingQueue<Runnable> getImageRunnableQueue;
    private final BlockingQueue<Runnable> getThreadCommentsRunnableQueue;

    // get Point of Interest Task
    private final BlockingQueue<Runnable> getPOIRunnableQueue;

    // Queues of tasks this manager is responsible for
    private final Queue<GetCommentsTask> getCommentsTaskQueue;
    private final Queue<PostTask> postTaskQueue;
    private final Queue<GetImageTask> getImageTaskQueue;
    private final Queue<GetPOITask> getPOITaskQueue;
    private final Queue<GetThreadCommentsTask> getThreadCommentsTaskQueue;

    // Thread pools for all the possible threads, one pool per each runnable
    private final ThreadPoolExecutor getCommentListPool;
    private final ThreadPoolExecutor getCommentsPool;
    private final ThreadPoolExecutor postImagePool;
    private final ThreadPoolExecutor postPool;
    private final ThreadPoolExecutor updatePool;
    private final ThreadPoolExecutor getImagePool;
    private final ThreadPoolExecutor getThreadCommentsPool;
    private final ThreadPoolExecutor getPOIPool;

    private Context context;
    private Handler handler;
    private static ThreadManager instance = null;

    /**
     * Private constructor due to singleton pattern.
     */
    private ThreadManager() {
        commentListCache = new LruCache<String, CommentList>(MAXIMUM_CACHE_SIZE);
        getImageCache = new LruCache<String, Bitmap>(MAXIMUM_CACHE_SIZE);
        getPOICache = new LruCache<String, String>(MAXIMUM_CACHE_SIZE);

        getCommentListRunnableQueue = new LinkedBlockingQueue<Runnable>();
        getCommentsRunnableQueue = new LinkedBlockingQueue<Runnable>();
        postImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        postRunnableQueue = new LinkedBlockingQueue<Runnable>();
        updateRunnableQueue = new LinkedBlockingQueue<Runnable>();
        getImageRunnableQueue = new LinkedBlockingQueue<Runnable>();
        getThreadCommentsRunnableQueue = new LinkedBlockingQueue<Runnable>();
        getPOIRunnableQueue = new LinkedBlockingQueue<Runnable>();

        getCommentsTaskQueue = new LinkedBlockingQueue<GetCommentsTask>();
        postTaskQueue = new LinkedBlockingQueue<PostTask>();
        getImageTaskQueue = new LinkedBlockingQueue<GetImageTask>();
        getThreadCommentsTaskQueue = new LinkedBlockingQueue<GetThreadCommentsTask>();
        getPOITaskQueue = new LinkedBlockingQueue<GetPOITask>();

        getCommentListPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, getCommentListRunnableQueue);
        getCommentsPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, getCommentsRunnableQueue);
        postImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, postImageRunnableQueue);
        postPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, postRunnableQueue);
        updatePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, updateRunnableQueue);
        getImagePool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, getImageRunnableQueue);
        getThreadCommentsPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, getThreadCommentsRunnableQueue);
        getPOIPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, getPOIRunnableQueue);

        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.what) {
                case POST_TASK_COMPLETE:
                	PostTask postTaskComplete = (PostTask) inputMessage.obj;
					if (postTaskComplete.getDialog() != null) {
						postTaskComplete.getDialog().dismiss();
					}
					ThreadComment threadComment = postTaskComplete.getThreadComment();
					if (threadComment != null) {
						ThreadList.addThread(threadComment);
						FragmentActivity activity = (FragmentActivity) context;
						ThreadListFragment fragment = (ThreadListFragment) activity.getSupportFragmentManager().findFragmentByTag("threadListFrag");
						if (fragment != null) {
							fragment.finishReload();
						}
					}
                    break;
                    
                case GET_THREADS_COMPLETE:
                    GetThreadCommentsTask threadTask = (GetThreadCommentsTask) inputMessage.obj;
                    threadTask.getFragment().finishReload();
                    recycleGetThreadCommentsTask(threadTask);
                    break;
                    
                case GET_THREADS_FAILED:
                    GetThreadCommentsTask threadTaskFail = (GetThreadCommentsTask) inputMessage.obj;
                    threadTaskFail.getFragment().finishReload();
                    recycleGetThreadCommentsTask(threadTaskFail);
                    break;   

                case GET_COMMENTS_COMPLETE:
                    GetCommentsTask task = (GetCommentsTask) inputMessage.obj;
                    task.getFragment().finishReload();
                    recycleCommentsTask(task);
                    break;
                    
                case GET_COMMENTS_FAILED:
                    GetCommentsTask taskFail = (GetCommentsTask) inputMessage.obj;
                    taskFail.getFragment().finishReload();
                    recycleCommentsTask(taskFail);
                    break;    

                case GET_COMMENT_LIST_RUNNING:
                	break;
                    
                case GET_COMMENT_LIST_FAILED:
                    GetCommentsTask taskListFail = (GetCommentsTask) inputMessage.obj;
                    taskListFail.getFragment().finishReload();
                    recycleCommentsTask(taskListFail);
                    break;

                case GET_IMAGE_RUNNING:
                    GetImageTask imageTask = (GetImageTask) inputMessage.obj;
                    if (imageTask.getDialog() != null) {
                    	imageTask.getDialog().show();
                    }
                    break;
                    
                case GET_IMAGE_FAILED:
                    GetImageTask imageTaskFail = (GetImageTask) inputMessage.obj;
                    if (imageTaskFail.getDialog() != null) {
                    	imageTaskFail.getDialog().dismiss();
                    }
                    recycleGetImageTask(imageTaskFail);
                    break;
                    
                case GET_IMAGE_COMPLETE:
                    GetImageTask imageTaskComplete = (GetImageTask) inputMessage.obj;
                    if (imageTaskComplete.getDialog() != null) {
                    	imageTaskComplete.getDialog().dismiss();
                    }
                    Bitmap bitmap = imageTaskComplete.getImageCache();
                    String id = imageTaskComplete.getId();
                    ImageView view = imageTaskComplete.getmImageWeakRef().get();
                    if (view != null) {
                    	Toaster.toastShort("Image updated");
                        view.setImageBitmap(bitmap);
                    }
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
					if (poiTaskComplete.getMarker() != null) {
						poiTaskComplete.getMarker().setSubDescription(
								(poiTaskComplete.getPOICache()));
						poiTaskComplete.getMarker().showInfoWindow();
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
					if (poiTaskFailed.getMarker() != null) {
						poiTaskFailed.getMarker().setSubDescription(
								("Unknown Location"));
						poiTaskFailed.getMarker().showInfoWindow();
					}
					poiTaskFailed.getLocation().setLocationDescription(
							poiTaskFailed.getPOICache());
					recycleGetPOITask(poiTaskFailed);
					break;
					
                case POST_GET_POI_RUNNING:
                    PostTask postPoiTaskRunning = (PostTask) inputMessage.obj;
                    if (postPoiTaskRunning.getDialog() != null) {
                    	postPoiTaskRunning.getDialog().show();
                    }
                    break;
                    
                case POST_GET_POI_COMPLETE:
                	PostTask postPoiTaskComplete = (PostTask) inputMessage.obj;
					if (postPoiTaskComplete.getDialog() != null) {
						postPoiTaskComplete.getDialog().setMessage("Posting to Server");
					}
					break;

				case POST_GET_POI_FAILED:
					PostTask postPoiTaskFailed = (PostTask) inputMessage.obj;
					if (postPoiTaskFailed.getDialog() != null) {
						postPoiTaskFailed.getDialog().dismiss();
					}
					break;
					
				case UPDATE_FAILED:
					PostTask postTaskUpdateFailed = (PostTask) inputMessage.obj;
					if (postTaskUpdateFailed.getDialog() != null) {
						postTaskUpdateFailed.getDialog().dismiss();
					}
					break;
										
				case POST_FAILED:
					PostTask postTaskFailed = (PostTask) inputMessage.obj;
					if (postTaskFailed.getDialog() != null) {
						postTaskFailed.getDialog().dismiss();
					}
					break;
					
				case POST_RUNNING:
					PostTask postTaskRun = (PostTask) inputMessage.obj;
					if (postTaskRun.getDialog() != null && !postTaskRun.getDialog().isShowing()) {
						postTaskRun.getDialog().show();
                    }
					break;
					
				case POST_IMAGE_FAILED:
					PostTask postTaskImageFailed = (PostTask) inputMessage.obj;
					if (postTaskImageFailed.getDialog() != null) {
						postTaskImageFailed.getDialog().dismiss();
					}
					break;
					

                default:
                	super.handleMessage(inputMessage);
                    break;
                }
            }
        };
    }

    /**
     * Generates an instance of the ThreadManager if it does not exist.
     * @param context  the context
     */
    public static void generateInstance(Context context) {
        instance = new ThreadManager();
        instance.context = context;
    }

    /**
     * Start the get image from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * 
     * @param id
     *            the image id under which the bitmap is stored on es
     */
    public static GetImageTask startGetImage(String id, ImageView imageView,
            ProgressDialog dialog) {
        GetImageTask task = instance.getImageTaskQueue.poll();
        if (task == null) {
            task = new GetImageTask();
        }
        task.initGetImageTask(instance, id, imageView, dialog);
        task.setImageCache(instance.getImageCache.get(id));
        instance.getImagePool.execute(task.getGetImageRunnable());
        return task;
    }
    
    /**
     * Start the get ThreadComments from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * 
     * @param fragment
     *            the ThreadListFragment that will be displaying the list
     */
    public static GetThreadCommentsTask startGetThreadComments(ThreadListFragment fragment) {
        GetThreadCommentsTask task = instance.getThreadCommentsTaskQueue.poll();
        if (task == null) {
            task = new GetThreadCommentsTask();
        }
        task.initGetThreadCommentsTask(instance, fragment);
        instance.getThreadCommentsPool.execute(task.getGetThreadCommentsRunnable());
        return task;
    }

    /**
     * Start the get comments from elasticSearch task, initialize a task
     * instance and add the appropriate runnable to the thread pool.
     * 
     * @param fragment
     *            the ThreadViewFragment displaying the ThreadComment
     * @param threadIndex
     *            id of the ThreadComment for which to get Comments
     */
    public static GetCommentsTask startGetComments(ThreadViewFragment fragment,
            int threadIndex) {
    	GetCommentsTask task = instance.getCommentsTaskQueue.poll();
        if (task == null) {
            task = new GetCommentsTask();
        }
        task.initCommentsTask(instance, fragment, threadIndex);
        task.setCommentListCache(instance.commentListCache.get(ThreadList.getThreads().get(threadIndex).getId()));
        instance.getCommentListPool.execute(task.getGetCommentListRunnable());
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
     * @param location
     * 			  the GeoLocation of the post
     * @param dialog
     *   		  a ProgressDialog in the fragment to display task progress
     *       
     */
    public static PostTask startPost(Comment comment, String title, GeoLocation location, ProgressDialog dialog) {
        PostTask task = instance.postTaskQueue.poll();
        if (task == null) {
            task = new PostTask();
        }
        Log.e("EEE", "INSIDE START POST");
        task.initPostTask(instance, comment, title, location, dialog);
        if (location.getLocationDescription() == null) {
            task.setPOICache(instance.getPOICache.get(location.getLocation().toString()));
            instance.getPOIPool.execute(task.getGetPOIRunnable());
        } else {
        	instance.postPool.execute(task.getPostRunnable());
        }
        return task;
    }

    /**
     * Start the get POI from elasticSearch task, initialize a task instance
     * and add the appropriate runnable to the thread pool
     * 
     * @param location
     *            the GeoLocation to find the POI from
     * @param dialog
     * 			  a ProgressDialog in a fragment to display task progress
     * @param marker
     * 			  an OSMDroid marker to display POI information 
     */
    public static GetPOITask startGetPOI(GeoLocation location, ProgressDialog dialog, Marker marker) {
        GetPOITask task = instance.getPOITaskQueue.poll();
        if (task == null) {
            task = new GetPOITask();
        }
        task.initGetPOITask(instance, location, dialog, marker);
        task.setPOICache(instance.getPOICache.get(location.getLocation().toString()));
        instance.getPOIPool.execute(task.getGetPOIRunnable());
        return task;
    }

    /**
     * Handles the possible states of the get POI task. Passes the state
     * to the Handler that runs on the UI thread.
     * 
     * @param task  the get POI task
     * @param state  the state
     */
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
     * Handle the possible states of the get comment task. When complete,
     * passes the state to the Handler that runs on the UI thread.
     * 
     * @param task  the get comments task
     * @param state  the state
     */
    public void handleGetCommentsState(GetCommentsTask task, int state) {
        switch (state) {
        case GET_COMMENT_LIST_COMPLETE:
            instance.getCommentsPool.execute(task.getGetCommentsRunnable());
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
     * Handle the possible states of the get thread comments task.
     * Passes the state to the Handler that runs on the UI thread.
     *
     * @param task  the get thread comments task
     * @param state  the state
     */
    public void handleGetThreadCommentsState(GetThreadCommentsTask task, int state) {
        switch(state) {
        case GET_THREADS_RUNNING:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_THREADS_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case GET_THREADS_FAILED:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        default:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        }
    }

    /**
     * Handle the possible states of the getImage task.
     * Passes the state to the Handler that runs on the UI thread.
     * 
     * @param task  the get image task
     * @param state  the state
     */
    public void handleGetImageState(GetImageTask task, int state) {
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
     * Handle the possible states of the Post task. Once post is complete,
     * obtains the point of interest of the post. If the post contains
     * an image, start the Image runnable, which posts the attached
     * image to elasticSearch. If not, or after the image is complete, start the
     * update runnable, which updates the commentList on elasticSearch.
     * Passes needed task states to the handler running on the UI thread to do UI updates.
     * 
     * @param task  the post task
     * @param state  the state
     */
    public void handlePostState(PostTask task, int state) {
        switch (state) {
        case POST_COMPLETE:
            if (task.getComment().hasImage()) {
                instance.postImagePool.execute(task.getImageRunnable());
            } else if (task.getTitle() == null) {
                instance.updatePool.execute(task.getUpdateRunnable());
            } else {
                instance.handler.obtainMessage(POST_TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case POST_IMAGE_COMPLETE:
            if (task.getTitle() == null) {
                instance.updatePool.execute(task.getUpdateRunnable());
            } else {
                instance.handler.obtainMessage(POST_TASK_COMPLETE, task).sendToTarget();
            }
            break;
        case POST_RUNNING:
        	instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case POST_TASK_COMPLETE:
            instance.handler.obtainMessage(state, task).sendToTarget();
            break;
        case POST_GET_POI_COMPLETE:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	instance.postPool.execute(task.getPostRunnable());
        	break;
        case POST_GET_POI_FAILED:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	instance.postPool.execute(task.getPostRunnable());
        	break;
        case UPDATE_FAILED:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	break;
        case POST_FAILED:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	break;
        case POST_IMAGE_FAILED:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	break;
        default:
        	instance.handler.obtainMessage(state, task).sendToTarget();
        	break;
        }
    }

    /**
     * Recycles a get comments task for reuse.
     * @param task  the task
     */
    void recycleCommentsTask(GetCommentsTask task) {
        task.recycle();
        instance.getCommentsTaskQueue.offer(task);
    }

    /**
     * Recycles a get image task for reuse.
     * @param task  the task
     */ 
    void recycleGetImageTask(GetImageTask task) {
        task.recycle();
        instance.getImageTaskQueue.offer(task);
    }

    /**
     * Recycles a post task for reuse.
     * @param task  the task
     */
    void recyclePostTask(PostTask task) {
        task.recycle();
        instance.postTaskQueue.offer(task);
    }

    /**
     * Recycles a get POI task for reuse.
     * @param task  the task
     */
    void recycleGetPOITask(GetPOITask task) {
        task.recycle();
        instance.getPOITaskQueue.offer(task);
    }
    
    /**
     * Recycles a get thread comments task for reuse.
     * @param task  the task
     */
    void recycleGetThreadCommentsTask(GetThreadCommentsTask task) {
        task.recycle();
        instance.getThreadCommentsTaskQueue.offer(task);
    }
}
