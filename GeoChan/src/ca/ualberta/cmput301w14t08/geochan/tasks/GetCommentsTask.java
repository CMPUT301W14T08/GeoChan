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

package ca.ualberta.cmput301w14t08.geochan.tasks;

import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadViewFragment;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentListRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetCommentsRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.TaskInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetCommentListRunnable;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetCommentsRunnable;

/**
 * Responsible for the task of controlling the runnables
 * that are responsible for the various parts of getting
 * all the Comments in a ThreadComment.
 * @author Artem Herasymchuk
 *
 */
public class GetCommentsTask implements TaskInterface, GetCommentListRunnableInterface, GetCommentsRunnableInterface {

    private int threadIndex;
    private CommentList cache;
    private ThreadViewFragment fragment;
    private Runnable getCommentListRunnable;
    private Runnable getCommentsRunnable;
    private ThreadManager manager;
    private Thread thread;

    /**
     * Constructs an instance of the task and its runnables.
     */
    public GetCommentsTask() {
        this.getCommentListRunnable = new GetCommentListRunnable(this);
        this.getCommentsRunnable = new GetCommentsRunnable(this);
    }

    /**
     * Initializes the instance of the task with the information needed to run it.
     * @param manager the ThreadManager
     * @param fragment the ThreadViewFragment
     * @param threadIndex the index of the ThreadComment
     */
    public void initCommentsTask(ThreadManager manager, ThreadViewFragment fragment, int threadIndex) {
        this.manager = manager;
        this.fragment = fragment;
        this.threadIndex = threadIndex;
    }
    
    /**
     * {@inheritDoc} 
     */
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

    /**
     * {@inheritDoc} 
     */
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
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void handleState(int state) {
        manager.handleGetCommentsState(this, state);
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
        this.threadIndex = -1;
        this.cache = null;
        this.manager = null;
        this.fragment = null;
    }
    
    /* Getters/setters for the interfaces this task implements below */
    
    @Override
    public void setGetCommentsThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    @Override
    public void setGetCommentListThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    @Override
    public void setCommentListCache(CommentList cache) {
        this.cache = cache;
    }

    @Override
    public CommentList getCommentListCache() {
        return cache;
    }
    
    /* Basic getters/setters below */
    
    public Runnable getGetCommentListRunnable() {
        return getCommentListRunnable;
    }
    
    public Runnable getGetCommentsRunnable() {
        return getCommentsRunnable;
    }
    
    public int getThreadIndex() {
        return threadIndex;
    }

    public ThreadViewFragment getFragment() {
        return fragment;
    }
}