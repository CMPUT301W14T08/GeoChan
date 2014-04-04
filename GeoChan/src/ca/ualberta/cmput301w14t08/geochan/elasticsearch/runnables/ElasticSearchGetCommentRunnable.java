package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

import java.lang.reflect.Type;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchResponse;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchGetCommentTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ElasticSearchGetCommentRunnable implements Runnable {

    private ElasticSearchGetCommentTask task;
    private String type = ElasticSearchClient.TYPE_COMMENT;
    public static final int STATE_GET_COMMENT_FAILED = -1;
    public static final int STATE_GET_COMMENT_RUNNING = 0;
    public static final int STATE_GET_COMMENT_COMPLETE = 1;

    public ElasticSearchGetCommentRunnable(ElasticSearchGetCommentTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.setGetCommentThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult result = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleGetCommentState(STATE_GET_COMMENT_RUNNING);
            Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, task.getId()).type(type)
                    .build();
            result = ElasticSearchClient.getInstance().getClient().execute(get);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Type type = new TypeToken<ElasticSearchResponse<Comment>>() {
            }.getType();
            Gson gson = GsonHelper.getOnlineGson();
            ElasticSearchResponse<Comment> esResponse = gson.fromJson(result.getJsonString(), type);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            CommentList list = task.getLoader().getCommentList();
            list.findCommentListById(list, task.getId()).setComment(esResponse.getSource());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result == null || !result.isSucceeded()) {
                task.handleGetCommentState(STATE_GET_COMMENT_FAILED);
            } else {
                task.handleGetCommentState(STATE_GET_COMMENT_COMPLETE);
            }
            task.setGetCommentThread(null);
            Thread.interrupted();
        }

    }

}
