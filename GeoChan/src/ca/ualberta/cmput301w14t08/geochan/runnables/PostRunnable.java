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

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.tasks.PostTask;

/**
 * Runnable for posting a Comment or ThreadComment 
 * object in a separate thread of execution to
 * ElasticSearch. 
 * 
 * @author Artem Herasymchuk
 *
 */
public class PostRunnable implements Runnable {

	private PostTask task;
	private String id;
	private String type;
	public static final int STATE_POST_FAILED = -1;
	public static final int STATE_POST_RUNNING = 0;
	public static final int STATE_POST_COMPLETE = 1;

	public PostRunnable(PostTask task) {
		this.task = task;
	}

	/**
	 * Converts the object into a json
	 * string, forms a query and sends it in  PUT request to
	 * ElasticSearch.
	 */
	@Override
	public void run() {
		task.setPostThread(Thread.currentThread());
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		JestResult jestResult = null;
		try {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			task.handlePostState(STATE_POST_RUNNING);
			JestClient client = ElasticSearchClient.getInstance().getClient();
			String json;
			if (task.getTitle() == null) {
				type = ElasticSearchClient.TYPE_COMMENT;
				id = task.getComment().getId();
				json = GsonHelper.getOnlineGson().toJson(task.getComment());
			} else {
				type = ElasticSearchClient.TYPE_THREAD;
				ThreadComment thread = task.getComment().findThread();
				if (thread == null) {
					thread = new ThreadComment(task.getComment(),
							task.getTitle());
				}
				thread.setBodyComment(task.getComment());
				task.setThreadComment(thread);
				id = thread.getId();
				json = GsonHelper.getOnlineGson().toJson(thread);
			}
			Index index = new Index.Builder(json)
					.index(ElasticSearchClient.URL_INDEX).type(type).id(id)
					.build();
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			try {
				jestResult = client.execute(index);
				
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (jestResult == null || !jestResult.isSucceeded()) {
				task.handlePostState(STATE_POST_FAILED);
			} else {
				task.handlePostState(STATE_POST_COMPLETE);
				GeoLocationLog.getInstance().addLogEntry(task.getLocation());
			}
			// task.setPostThread(null);
			Thread.interrupted();
		}
	}

}
