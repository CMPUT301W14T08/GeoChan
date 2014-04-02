package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchPostTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;

public class ElasticSearchImageRunnable implements Runnable {

    private ElasticSearchPostTask task;
    private String id;
    private String type = ElasticSearchClient.TYPE_IMAGE;
    public static final int STATE_IMAGE_FAILED = -1;
    public static final int STATE_IMAGE_RUNNING = 0;
    public static final int STATE_IMAGE_COMPLETE = 1;
    
    public ElasticSearchImageRunnable(ElasticSearchPostTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.setImageThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult jestResult = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleImageState(STATE_IMAGE_RUNNING);
            id = task.getComment().getId();
            JestClient client = ElasticSearchClient.getInstance().getClient();
            String json = GsonHelper.getOnlineGson().toJson(task.getComment().getImage());
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
            task.handleImageState(STATE_IMAGE_COMPLETE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jestResult == null || !jestResult.isSucceeded()) {
                task.handleImageState(STATE_IMAGE_FAILED);
            }
            task.setImageThread(null);
            Thread.interrupted();
        }
    }

}
