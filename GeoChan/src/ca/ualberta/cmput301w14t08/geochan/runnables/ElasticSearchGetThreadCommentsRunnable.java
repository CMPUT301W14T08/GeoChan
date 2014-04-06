package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.ElasticSearchGetThreadCommentsTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
        task.setGetThreadCommentsThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        task.handleGetThreadCommentsState(STATE_GET_THREADS_RUNNING);
        JestResult result = null;
        String id = ThreadList.getThreads().get(task.getThreadIndex()).getId();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, id).type(type)
                    .build();
            result = ElasticSearchClient.getInstance().getClient().execute(get);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            JsonObject object = result.getJsonObject().get("_source").getAsJsonObject();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Gson gson = GsonHelper.getExposeGson();
            CommentList list = gson.fromJson(object, CommentList.class);
            task.handleGetThreadCommentsState(STATE_GET_THREADS_COMPLETE);
        } catch (Exception e) {
            //
        } finally {
            if (result == null || !result.isSucceeded()) {
                task.handleGetThreadCommentsState(STATE_GET_THREADS_FAILED);
            }
            //task.setGetCommentListThread(null);
            Thread.interrupted();
        }
    }
}
