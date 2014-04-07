package ca.ualberta.cmput301w14t08.geochan.runnables;

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;

import java.lang.reflect.Type;

import android.graphics.Bitmap;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.ElasticSearchResponse;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetImageTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetImageRunnable implements Runnable {

    private GetImageTask task;
    private String type = ElasticSearchClient.TYPE_IMAGE;
    public static final int STATE_GET_IMAGE_FAILED = -1;
    public static final int STATE_GET_IMAGE_RUNNING = 0;
    public static final int STATE_GET_IMAGE_COMPLETE = 1;

    public GetImageRunnable(GetImageTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.setGetImageThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        task.handleGetImageState(STATE_GET_IMAGE_RUNNING);
        JestResult result = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, task.getId()).type(type)
                    .build();
            result = ElasticSearchClient.getInstance().getClient().execute(get);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            Type type = new TypeToken<ElasticSearchResponse<Bitmap>>() {
            }.getType();
            Gson gson = GsonHelper.getOnlineGson();
            ElasticSearchResponse<Bitmap> esResponse = gson.fromJson(result.getJsonString(), type);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.setImageCache(esResponse.getSource());
            task.handleGetImageState(STATE_GET_IMAGE_COMPLETE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result == null || !result.isSucceeded()) {
                task.handleGetImageState(STATE_GET_IMAGE_FAILED);
            }
            //task.setGetImageThread(null);
            Thread.interrupted();
        }

    }

}
