package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestResult;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetImageTask;

public class ElasticSearchGetImageRunnable implements Runnable {

    private ElasticSearchGetImageTask task;
    private String type = ElasticSearchClient.TYPE_IMAGE;
    public static final int STATE_GET_IMAGE_FAILED = -1;
    public static final int STATE_GET_IMAGE_RUNNING = 0;
    public static final int STATE_GET_IMAGE_COMPLETE = 1;
    
    public ElasticSearchGetImageRunnable(ElasticSearchGetImageTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        task.setGetImageThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        Byte[] cache = task.getImageCache();
        JestResult result = null;
    }

}
