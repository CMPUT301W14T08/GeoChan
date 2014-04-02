package ca.ualberta.cmput301w14t08.geochan.runnables;

import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchEditTask;

public class ElasticSearchEditRunnable implements Runnable {

    private ElasticSearchEditTask task;
    private String id;
    private String type;
    public static final int STATE_EDIT_FAILED = -1;
    public static final int STATE_EDIT_RUNNING = 0;
    public static final int STATE_EDIT_COMPLETE = 1;
    
    public ElasticSearchEditRunnable(ElasticSearchEditTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        
    }
}
