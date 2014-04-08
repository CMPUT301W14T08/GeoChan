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
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.tasks.PostTask;

import com.google.gson.Gson;

/**
 * Runnable for posting a CommentList object of a 
 * corresponding Comment object in a separate thread of execution to
 * ElasticSearch. 
 * 
 * @author Artem Herasymchuk
 *
 */
public class UpdateRunnable implements Runnable {

	private PostTask task;
	private String id;
	private String type = ElasticSearchClient.TYPE_INDEX;
	public static final int STATE_UPDATE_FAILED = -1;
	public static final int STATE_UPDATE_RUNNING = 0;
	public static final int STATE_UPDATE_COMPLETE = 1;

	public UpdateRunnable(PostTask task) {
		this.task = task;
	}

	/**
	 * Constructs a CommentList object from the PostTask's Comment
	 * object, forms a query and sends it to ElasticSearch in a PUT
	 * request.
	 */
	@Override
	public void run() {
		task.setUpdateThread(Thread.currentThread());
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
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
			id = currentComment.getId();
			Gson gson = GsonHelper.getExposeGson();
			CommentList list = makeCommentList(new CommentList(currentComment));
			String json = gson.toJson(list);
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
			task.handleUpdateState(STATE_UPDATE_COMPLETE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (jestResult == null || !jestResult.isSucceeded()) {
				task.handleUpdateState(STATE_UPDATE_FAILED);
			}
			// task.setUpdateThread(null);
			Thread.interrupted();
		}
	}

	public CommentList makeCommentList(CommentList list) {
		if (list.getComment().getChildren().size() == 0) {
			return list;
		} else {
			for (Comment c : list.getComment().getChildren()) {
				list.addCommentList(makeCommentList(new CommentList(c)));
			}
		}
		return list;
	}

}
