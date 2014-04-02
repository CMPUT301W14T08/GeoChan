package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetCommentListTask;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;

import com.google.gson.JsonArray;

public class ElasticSearchGetCommentListRunnable implements Runnable {

    private ElasticSearchGetCommentListTask task;
    private String type = ElasticSearchClient.TYPE_INDEX;
    public static final int STATE_GET_LIST_FAILED = -1;
    public static final int STATE_GET_LIST_RUNNING = 0;
    public static final int STATE_GET_LIST_COMPLETE = 1;

    
    public ElasticSearchGetCommentListRunnable(ElasticSearchGetCommentListTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        task.setGetCommentListThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        task.handleGetCommentListState(STATE_GET_LIST_RUNNING);
        CommentList cache = task.getCommentListCache();
        JestResult result = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, task.getId()).type(type).build();
            result = ElasticSearchClient.getInstance().getClient().execute(get);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            JsonArray array = result.getJsonObject().get("_source").getAsJsonObject()
                    .get("comments").getAsJsonArray();
            ArrayList<String> hits = new ArrayList<String>();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            for (int i = 0; i < array.size(); ++i) {
                hits.add(array.get(i).getAsString());
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            cache = new CommentList(task.getId(), hits);
            task.handleGetCommentListState(STATE_GET_LIST_COMPLETE);
        } catch (Exception e) {
            //
        } finally {
            if (result == null || !result.isSucceeded()) {
                task.handleGetCommentListState(STATE_GET_LIST_FAILED);
            }
            task.setGetCommentListThread(null);
            Thread.interrupted();
        }
    }

}
