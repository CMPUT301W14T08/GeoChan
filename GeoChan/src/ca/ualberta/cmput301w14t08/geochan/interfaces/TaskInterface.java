package ca.ualberta.cmput301w14t08.geochan.interfaces;

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
