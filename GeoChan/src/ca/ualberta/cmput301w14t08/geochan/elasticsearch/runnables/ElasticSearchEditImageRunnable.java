package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import android.graphics.Bitmap;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchEditTask;

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
        task.setEditImageThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult jestResult = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleState(STATE_EDIT_IMAGE_RUNNING);
            JestClient client = ElasticSearchClient.getInstance().getClient();
            Bitmap bitmapToUpdate = task.getBitmap();
            // Here build update, query and execute.
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jestResult == null || !jestResult.isSucceeded()) {
                task.handleEditState(STATE_EDIT_IMAGE_FAILED);
            }
            task.setEditImageThread(null);
            Thread.interrupted();
        }
        
    }
}
