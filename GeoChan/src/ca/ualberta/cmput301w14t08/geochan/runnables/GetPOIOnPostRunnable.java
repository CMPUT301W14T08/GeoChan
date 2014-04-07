package ca.ualberta.cmput301w14t08.geochan.runnables;

import ca.ualberta.cmput301w14t08.geochan.tasks.GetCommentsTask;

public class GetPOIOnPostRunnable implements Runnable {

    private GetCommentsTask task;
    public static final int STATE_GET_POI_FAILED = -1;
    public static final int STATE_GET_POI_RUNNING = 0;
    public static final int STATE_GET_POI_COMPLETE = 1;

    public GetPOIOnPostRunnable(GetCommentsTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
    	
    }
}
