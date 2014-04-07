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

package ca.ualberta.cmput301w14t08.geochan.interfaces;

/**
 * Provides an interface for a Task that runs one or several runnables,
 * for use with the ThreadManager for asynchronous tasks.
 * @author Artem Herasymchuk
 *
 */
public interface TaskInterface {
	
    /** 
     * Passes the state of the task to the ThreadManager
     * so that it can be handled by the manager.
     * @param state the state
     */
	public void handleState(int state);
	
	/**
     * Returns the currently running thread.
     * @return the thread
     */
	public Thread getCurrentThread();
	
    /**
     * Sets the currently running thread of the task.
     * @param thread the thread
     */
	public void setCurrentThread(Thread thread);
	
	/**
	 * Resets the task.
	 */
	public void recycle();
}
