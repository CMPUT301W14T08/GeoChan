package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ElasticSearchGetThreadCommentsRunnable {
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
