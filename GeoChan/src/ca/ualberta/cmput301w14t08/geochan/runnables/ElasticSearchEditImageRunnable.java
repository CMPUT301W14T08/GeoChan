package ca.ualberta.cmput301w14t08.geochan.runnables;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchEditTask;

public class ElasticSearchEditImageRunnable implements Runnable {
    
    private ElasticSearchEditTask task;
    private String id;
    private String type = ElasticSearchClient.TYPE_IMAGE;
    public static final int STATE_EDIT_IMAGE_FAILED = -1;
    public static final int STATE_EDIT_IMAGE_RUNNING = 0;
    public static final int STATE_EDIT_IMAGE_COMPLETE = 1;
    
    
    public ElasticSearchEditImageRunnable(ElasticSearchEditTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {        
    }
}
