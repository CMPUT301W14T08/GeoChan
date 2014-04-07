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

import ca.ualberta.cmput301w14t08.geochan.fragments.ThreadListFragment;
import ca.ualberta.cmput301w14t08.geochan.interfaces.GetThreadCommentsRunnableInterface;
import ca.ualberta.cmput301w14t08.geochan.interfaces.TaskInterface;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.runnables.GetThreadCommentsRunnable;

/**
 * Responsible for the task that downloads all the Comments for a particular
 * ThreadComment from ElasticSearch.
 * @author Artem Herasymchuk
 *
 */
public class GetThreadCommentsTask implements TaskInterface, GetThreadCommentsRunnableInterface {

    private Runnable getThreadCommentsRunnable;
    private ThreadListFragment fragment;
    private ThreadManager manager;
    private Thread thread;
    
    /**
     * Constructs an instance of the task and its runnables.
     */
    public GetThreadCommentsTask() {
        this.getThreadCommentsRunnable = new GetThreadCommentsRunnable(this);
    }
    
    /**
     * Initializes the instance of the task with the parameters needed to run it.
     * @param manager the ThreadManager
     * @param fragment the ThreadListFragment
     */
    public void initGetThreadCommentsTask(ThreadManager manager, ThreadListFragment fragment) {
        this.manager = manager;
        this.fragment = fragment;
    }

    /** 
     * Handles the various possible states of the 
     * Runnable the obtains the ThreadComments.
     * @param state the state
     */
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
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public void handleState(int state) {
        manager.handleGetThreadCommentsState(this, state);
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
        this.manager = null;
        this.fragment = null;
    }
    
    /* Getters/setters for the interfaces this task impements */
    
    @Override
    public void setGetThreadCommentsThread(Thread thread) {
        setCurrentThread(thread);
    }
    
    /* Basic getters/setters below */

    public ThreadListFragment getFragment() {
        return fragment;
    }
    
    public Runnable getGetThreadCommentsRunnable() {
        return getThreadCommentsRunnable;
    }
}
