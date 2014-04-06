package ca.ualberta.cmput301w14t08.geochan.runnables;

import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetThreadCommentsTask;

public class ElasticSearchGetThreadCommentsRunnable implements Runnable {
    private ElasticSearchGetThreadCommentsTask task;
    private String type = ElasticSearchClient.TYPE_INDEX;
    public static final int STATE_GET_THREADS_FAILED = -1;
    public static final int STATE_GET_THREADS_RUNNING = 0;
    public static final int STATE_GET_THREADS_COMPLETE = 1;

    public ElasticSearchGetThreadCommentsRunnable(ElasticSearchGetThreadCommentsTask task) {
        this.task = task;
    }

    @Override
    public void run() {

    }
}
