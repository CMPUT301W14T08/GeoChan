package ca.ualberta.cmput301w14t08.geochan.elasticsearch.runnables;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Update;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchQueries;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.tasks.ElasticSearchPostTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;

import com.google.gson.Gson;

public class ElasticSearchUpdateRunnable implements Runnable {

    private ElasticSearchPostTask task;
    private String id;
    private String type = ElasticSearchClient.TYPE_INDEX;
    public static final int STATE_UPDATE_FAILED = -1;
    public static final int STATE_UPDATE_RUNNING = 0;
    public static final int STATE_UPDATE_COMPLETE = 1;
    
    public ElasticSearchUpdateRunnable(ElasticSearchPostTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        task.setUpdateThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        JestResult jestResult = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleUpdateState(STATE_UPDATE_RUNNING);
            JestClient client = ElasticSearchClient.getInstance().getClient();
            Comment currentComment = task.getComment();
            while (currentComment.getParent() != null) {
                currentComment = currentComment.getParent();
            }
            Gson gson = GsonHelper.getExposeGson();
            CommentList list = currentComment.makeCommentList(new CommentList(currentComment));
            String json = gson.toJson(list);
            String query = ElasticSearchQueries.commentListScript(json);
            Update update = new Update.Builder(query).index(ElasticSearchClient.URL_INDEX).type(type).id(task.getComment().getParent().getId()).build();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            try {
                jestResult = client.execute(update);
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            task.handleUpdateState(STATE_UPDATE_COMPLETE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (jestResult == null || !jestResult.isSucceeded()) {
                task.handleUpdateState(STATE_UPDATE_FAILED);
            }
            task.setUpdateThread(null);
            Thread.interrupted();
        }
    }

}
