package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchEditTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;

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
        task.setEditThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult jestResult = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleEditState(STATE_EDIT_RUNNING);
            JestClient client = ElasticSearchClient.getInstance().getClient();
            String json;
            Comment commentToUpdate = task.getComment();
            if (task.getIsThread() == false) {
                type = ElasticSearchClient.TYPE_COMMENT;
                id = commentToUpdate.getId();
                json = GsonHelper.getOnlineGson().toJson(commentToUpdate);
            } else {
                type = ElasticSearchClient.TYPE_THREAD;
                id = commentToUpdate.getId();
            }
            // Here build update, query and execute.
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jestResult == null || !jestResult.isSucceeded()) {
                task.handleEditState(STATE_EDIT_FAILED);
            }
            task.setEditThread(null);
            Thread.interrupted();
        }
    }
}
