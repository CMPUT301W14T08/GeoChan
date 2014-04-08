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

import io.searchbox.client.JestResult;
import io.searchbox.core.Get;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetCommentsTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Runnable for retrieving a CommentList from a separate thread of execution from
 * ElasticSearch.
 * @author Artem Hersymchuk, Artem Chikin 
 *
 */
public class GetCommentListRunnable implements Runnable {

	private GetCommentsTask task;
	private String type = ElasticSearchClient.TYPE_INDEX;
	public static final int STATE_GET_LIST_FAILED = -1;
	public static final int STATE_GET_LIST_RUNNING = 0;
	public static final int STATE_GET_LIST_COMPLETE = 1;

	public GetCommentListRunnable(GetCommentsTask task) {
		this.task = task;
	}

	@Override
	public void run() {
		task.setGetCommentListThread(Thread.currentThread());
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		task.handleGetCommentListState(STATE_GET_LIST_RUNNING);
		JestResult result = null;
		String id = ThreadList.getThreads().get(task.getThreadIndex()).getId();
		try {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			Get get = new Get.Builder(ElasticSearchClient.URL_INDEX, id).type(
					type).build();
			result = ElasticSearchClient.getInstance().getClient().execute(get);
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			JsonObject object = result.getJsonObject().get("_source")
					.getAsJsonObject();
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			Gson gson = GsonHelper.getExposeGson();
			CommentList list = gson.fromJson(object, CommentList.class);
			task.setCommentListCache(list);
			task.handleGetCommentListState(STATE_GET_LIST_COMPLETE);
		} catch (Exception e) {
			//
		} finally {
			if (result == null || !result.isSucceeded()) {
				task.handleGetCommentListState(STATE_GET_LIST_FAILED);
			}
			// task.setGetCommentListThread(null);
			Thread.interrupted();
		}
	}
}
