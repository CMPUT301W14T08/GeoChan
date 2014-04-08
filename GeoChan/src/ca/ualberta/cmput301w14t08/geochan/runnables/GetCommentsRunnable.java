/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.runnables;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchQueries;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ElasticSearchDocs;
import ca.ualberta.cmput301w14t08.geochan.models.ElasticSearchResponse;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetCommentsTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Runnable for retrieving a comment objects in a separate thread of execution from
 * ElasticSearch. 
 * 
 * @author Artem Herasymchuk
 *
 */
public class GetCommentsRunnable implements Runnable {

	private GetCommentsTask task;
	public static final int STATE_GET_COMMENTS_FAILED = -1;
	public static final int STATE_GET_COMMENTS_RUNNING = 0;
	public static final int STATE_GET_COMMENTS_COMPLETE = 1;

	public GetCommentsRunnable(GetCommentsTask task) {
		this.task = task;
	}

	/**
	 * Forms a query and sends a multi-Get request to ES, then processes
	 * the retrieved data into a CommentList object and saves it into an array.
	 * Then, puts the comment object in the right place in the corresponding 
	 * commentList to be later reconstructed into a correct hierarchy of comments.
	 */
	@Override
	public void run() {
		task.setGetCommentsThread(Thread.currentThread());
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
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
			URL url = new URL(
					"http://cmput301.softwareprocess.es:8080/cmput301w14t08/geoComment/_mget");
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			
			DataOutputStream writeStream = new DataOutputStream(
					connection.getOutputStream());
			writeStream.writeBytes(json);
			writeStream.flush();
			writeStream.close();
			
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
			StringBuffer response = new StringBuffer();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			
			String responseJson = response.toString();
			Gson gson = GsonHelper.getOnlineGson();
			Type elasticSearchDocsType = new TypeToken<ElasticSearchDocs<Comment>>() {
			}.getType();
			
			ElasticSearchDocs<Comment> esResponse = gson.fromJson(responseJson,
					elasticSearchDocsType);
			ArrayList<Comment> list = new ArrayList<Comment>();
			
			for (ElasticSearchResponse<Comment> r : esResponse.getDocs()) {
				Comment object = r.getSource();
				list.add(object);
			}
			
			for (Comment comment : list) {
				commentList.findCommentListById(commentList, comment.getId())
						.setComment(comment);
			}
			
			ThreadComment threadComment = ThreadList.getThreads().get(
					task.getThreadIndex());
			Comment bodyComment = threadComment.getBodyComment();
			threadComment.setBodyComment(commentList.reconsructFromCommentList(
					commentList, bodyComment));
			CacheManager.getInstance()
					.serializeThreadCommentById(threadComment);
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
			// task.setGetCommentsThread(null);
			Thread.interrupted();
		}

	}

}
