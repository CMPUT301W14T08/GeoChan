package ca.ualberta.cmput301w14t08.geochan.runnables;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchQueries;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ElasticSearchDocs;
import ca.ualberta.cmput301w14t08.geochan.models.ElasticSearchResponse;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetCommentsTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetCommentsRunnable implements Runnable {

    private GetCommentsTask task;
    private String type = ElasticSearchClient.TYPE_COMMENT;
    public static final int STATE_GET_COMMENTS_FAILED = -1;
    public static final int STATE_GET_COMMENTS_RUNNING = 0;
    public static final int STATE_GET_COMMENTS_COMPLETE = 1;

    public GetCommentsRunnable(GetCommentsTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        task.setGetCommentsThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        HttpURLConnection connection = null;
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            task.handleGetCommentsState(STATE_GET_COMMENTS_RUNNING);
            CommentList commentList = task.getCommentListCache();
            ArrayList<String> idList = new ArrayList<String>();
            commentList.getIdsFromList(commentList, idList);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            String json = ElasticSearchQueries.commentsScript(idList);
            URL url = new URL("http://cmput301.softwareprocess.es:8080/cmput301w14t08/geoCommentTest/_mget");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            DataOutputStream writeStream = new DataOutputStream(connection.getOutputStream());
            writeStream.writeBytes(json);
            writeStream.flush();
            writeStream.close();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            StringBuffer response = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;         
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            String responseJson = response.toString();
            Gson gson = GsonHelper.getOnlineGson();
            Type elasticSearchDocsType = new TypeToken<ElasticSearchDocs<Comment>>() {}.getType();
            ElasticSearchDocs<Comment> esResponse = gson.fromJson(responseJson,
                    elasticSearchDocsType);
            ArrayList<Comment> list = new ArrayList<Comment>();
            for (ElasticSearchResponse<Comment> r : esResponse.getDocs()) {
                Comment object = r.getSource();
                list.add(object);
            }
            for (Comment comment : list) {
                Log.e("?", comment.getTextPost() + "  " + comment.getId());
                commentList.findCommentListById(commentList, comment.getId()).setComment(comment);
            }
            ThreadComment threadComment = ThreadList.getThreads().get(task.getThreadIndex());
            Comment bodyComment = threadComment.getBodyComment();
            Log.e("??", "size: " + bodyComment.getChildren().size());
            threadComment.setBodyComment(commentList.reconsructFromCommentList(commentList, bodyComment));
            /*for (CommentList cl : commentList.getChildren()) {
                Log.e("???", cl.getId());
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection == null || (connection.getResponseCode() != 200)) {
                    task.handleGetCommentsState(STATE_GET_COMMENTS_FAILED);
                } else {
                    task.handleGetCommentsState(STATE_GET_COMMENTS_COMPLETE);
                }
            } catch (IOException e) {
                task.handleGetCommentsState(STATE_GET_COMMENTS_FAILED);
            }
            connection.disconnect();
            //task.setGetCommentsThread(null);
            Thread.interrupted();
        }

    }

}
