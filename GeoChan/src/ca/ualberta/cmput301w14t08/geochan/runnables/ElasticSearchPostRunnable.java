package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;


public class ElasticSearchPostRunnable implements Runnable {

    private ElasticSearchTask task;
    private String id;
    private String type;
    public static final int STATE_POST_FAILED = -1;
    public static final int STATE_POST_RUNNING = 0;
    public static final int STATE_POST_COMPLETE = 1;
    
    public ElasticSearchPostRunnable(ElasticSearchTask task) {
        this.task = task;
    }
    
    public void run() {
        task.setPostThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult jestResult = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handlePostState(STATE_POST_RUNNING);
            JestClient client = ElasticSearchClient.getInstance().getClient();
            String json;
            if (task.getTitle() == null) {
                type = ElasticSearchClient.TYPE_COMMENT;
                id = task.getComment().getId();
                json = GsonHelper.getOnlineGson().toJson(task.getComment());
            } else {
                type = ElasticSearchClient.TYPE_THREAD;
                ThreadComment thread = new ThreadComment(task.getComment(), task.getTitle());
                id = thread.getId();
                json = GsonHelper.getOnlineGson().toJson(thread);
            }
            Index index = new Index.Builder(json).index(ElasticSearchClient.URL_INDEX).type(type).id(id).build();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                jestResult = client.execute(index);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.handlePostState(STATE_POST_COMPLETE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jestResult == null || !jestResult.isSucceeded()) {
                task.handlePostState(STATE_POST_FAILED);
            }
            task.setPostThread(null);
            Thread.interrupted();
        }
    }

}
