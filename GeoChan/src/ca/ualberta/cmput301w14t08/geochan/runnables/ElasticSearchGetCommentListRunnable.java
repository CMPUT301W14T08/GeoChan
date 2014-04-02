package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchGetTask;
import ca.ualberta.cmput301w14t08.geochan.models.CommentHitsList;

import com.google.gson.JsonArray;

public class ElasticSearchGetCommentListRunnable implements Runnable {

    private ElasticSearchGetTask task;
    private String type = ElasticSearchClient.TYPE_INDEX;
    public static final int STATE_GET_LIST_FAILED = -1;
    public static final int STATE_GET_LIST_RUNNING = 0;
    public static final int STATE_GET_LIST_COMPLETE = 1;

    
    public ElasticSearchGetCommentListRunnable(ElasticSearchGetTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        CommentHitsList list = task.getCommentHits();
        Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, task.getTopComment().getId()).type(type).build();
        JestResult result = null;
        try {
            result = ElasticSearchClient.getInstance().getClient().execute(get);
            JsonArray array = result.getJsonObject().get("_source").getAsJsonObject()
                    .get("comments").getAsJsonArray();
            ArrayList<String> hits = new ArrayList<String>();
            for (int i = 0; i < array.size(); ++i) {
                hits.add(array.get(i).getAsString());
            }
            list = new CommentHitsList(task.getTopComment().getId(), hits);
        } catch (Exception e) {
            //
        } finally {
            task.setCommentHits(list);
        }
    }

}
