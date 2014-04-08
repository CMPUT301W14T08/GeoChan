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

package ca.ualberta.cmput301w14t08.geochan.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Class responsible for managing local saving of threadsList, threads, and comments.
 * As well as managing and saving the queues of comments and threadComments which are
 * to be posted when internet connection is acquired.
 * 
 * @author Artem Chikin
 *
 */
public class CacheManager {
	private static CacheManager instance = null;
	private Context context;
	private Gson offlineGson;
	private Gson onlineGson;
	private static final String EXTENSION = ".sav";
	// File for saving ThreadList
	private static final String FILENAME = "threads.sav";
	private static final String IMAGE = "IMG";
	// File for the Queue of Comments to post
	private static final String FILENAME2 = "commentq.sav";
	// File for the Queue of ChreadComments to post
	private static final String FILENAME3 = "threadq.sav";
	private ArrayList<Comment> commentQueue;
	private ArrayList<ThreadComment> threadCommentQueue;

	/**
	 * Initializes the CacheManager fields, private because of the Singleton
	 * pattern.
	 * 
	 * @param context The Context the CacheManager is running in.
	 */
	private CacheManager(Context context) {
		this.context = context;
		this.offlineGson = GsonHelper.getOfflineGson();
		// thread Gson is same as online - all the data except for the comments.
		this.onlineGson = GsonHelper.getOnlineGson();
		commentQueue = deserializeCommentQueue();
		threadCommentQueue = deserializeThreadCommentQueue();
	}

	/**
	 * Adds a comment to the Queue of comments to post when Internet connection
	 * is acquired. Serializes the queue.
	 * 
	 * @param comment The Comment to be added to the queue.
	 */
	public void addCommentToQueue(Comment comment) {
		commentQueue.add(comment);
		serializeCommentQueue();
	}

	/**
	 * Adds a threadComment to the Queue of threadComments to post when Internet
	 * connection is acquired. Serializes the queue.
	 * 
	 * @param thread The ThreadComment to be added to the queue.
	 */
	public void addThreadCommentToQueue(ThreadComment thread) {
		threadCommentQueue.add(thread);
		serializeThreadCommentQueue();
	}

	/**
	 * Using the ThreadManager, posts all the comments and threadComments in the
	 * queue, called once internet connection is acquired.
	 */
	public void postAll() {
		for (Comment comment : commentQueue) {
			ThreadManager.startPost(comment, null, comment.getLocation(), null);
			commentQueue.remove(comment);
		}
		for (ThreadComment threadComment : threadCommentQueue) {
			Comment bodyComment = threadComment.getBodyComment();
			ThreadManager.startPost(bodyComment, threadComment.getTitle(),
					bodyComment.getLocation(), null);
			threadCommentQueue.remove(threadComment);
		}
		serializeCommentQueue();
		serializeThreadCommentQueue();
	}

	public static CacheManager getInstance() {
		return instance;
	}

	public static void generateInstance(Context context) {
		instance = new CacheManager(context);
	}

	public ArrayList<Comment> getCommentQueue() {
		return commentQueue;
	}

	public void setCommentQueue(ArrayList<Comment> commentQueue) {
		this.commentQueue = commentQueue;
	}

	public ArrayList<ThreadComment> getThreadCommentQueue() {
		return threadCommentQueue;
	}

	public void setThreadCommentQueue(
			ArrayList<ThreadComment> threadCommentQueue) {
		this.threadCommentQueue = threadCommentQueue;
	}

	/**
	 * Serializes the comment queue to JSON.
	 */
	public void serializeCommentQueue() {
		try {
			String json = offlineGson.toJson(getCommentQueue());
			FileOutputStream f = context.openFileOutput(FILENAME2,
					Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserializes the comment queue from JSON.
	 * @return An ArrayList of the deserialized Comments.
	 */
	public ArrayList<Comment> deserializeCommentQueue() {
		ArrayList<Comment> list = new ArrayList<Comment>();
		try {
			FileInputStream f = context.openFileInput(FILENAME2);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			Type type = new TypeToken<ArrayList<Comment>>() {
			}.getType();
			list = onlineGson.fromJson(json, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Serializes the ThreadComment queue to JSON.
	 */
	public void serializeThreadCommentQueue() {
		try {
			String json = offlineGson.toJson(getThreadCommentQueue());
			FileOutputStream f = context.openFileOutput(FILENAME3,
					Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserializes the ThreadComment queue from JSON.
	 * @return An ArrayList of the deserialized ThreadComments.
	 */
	public ArrayList<ThreadComment> deserializeThreadCommentQueue() {
		ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
		try {
			FileInputStream f = context.openFileInput(FILENAME3);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			Type type = new TypeToken<ArrayList<ThreadComment>>() {
			}.getType();
			list = onlineGson.fromJson(json, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Serializes a Bitmap into JSON and stores it in the cache.
	 * @param image The Bitmap to be serialized.
	 * @param id The ID of the image being serialized.
	 */
	public void serializeImage(Bitmap image, String id) {
		try {
			String json = onlineGson.toJson(image);
			FileOutputStream f = context.openFileOutput(IMAGE + id + EXTENSION,
					Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserializes an image from the cache and returns it.
	 * @param id The ID of the comment to retrieve.
	 * @return The deserialized Bitmap.
	 */
	public Bitmap deserializeImage(String id) {
		Bitmap image = null;
		try {
			FileInputStream f = context.openFileInput(IMAGE + id + EXTENSION);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			image = onlineGson.fromJson(json, Bitmap.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Serialize the list of threads with all the data with the exception of all
	 * the comment children of the Thread body comment.
	 * 
	 * @param list The ArrayList of ThreadComments to serialize.
	 */
	public void serializeThreadList(ArrayList<ThreadComment> list) {
		try {
			String json = onlineGson.toJson(list);
			FileOutputStream f = context.openFileOutput(FILENAME,
					Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deserialize a list of ThreadComment objects without comments.
	 * 
	 * @return The ArrayList of ThreadComments.
	 */
	public ArrayList<ThreadComment> deserializeThreadList() {
		ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
		try {
			FileInputStream f = context.openFileInput(FILENAME);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			Type type = new TypeToken<ArrayList<ThreadComment>>() {
			}.getType();
			list = onlineGson.fromJson(json, type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Serialize ThreadComment object into a file with the file's name being
	 * ThreadComment's id.
	 * 
	 * @param thread The ThreadComment to serialize.
	 */
	public void serializeThreadCommentById(ThreadComment thread) {
		try {
			String json = offlineGson.toJson(thread);
			FileOutputStream f = context.openFileOutput(thread.getId()
					+ EXTENSION, Context.MODE_PRIVATE);
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
			w.write(json);
			w.close();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve ThreadComment object by id and return the children of its body
	 * comment. This is done because we already have ThreadComment object and
	 * its BodyComment from the ThreadComment Deserializers. This deserializer
	 * runs when a thread is opened and we need to retrieve comments only from
	 * the cache.
	 * 
	 * @param id The id of the ThreadComment to deserialize.
	 * @return An ArrayList of the ThreadComment's body comment's children.
	 */
	public ArrayList<Comment> deserializeThreadCommentById(String id) {
		ThreadComment thread = null;
		try {
			FileInputStream f = context.openFileInput(id + EXTENSION);
			BufferedReader r = new BufferedReader(new InputStreamReader(f));
			String json = "";
			String temp = "";
			temp = r.readLine();
			while (temp != null) {
				json = json + temp;
				temp = r.readLine();
			}
			r.close();
			f.close();
			thread = offlineGson.fromJson(json, ThreadComment.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (thread != null) {
			Log.e("Thread", "Deserialized");
			return thread.getBodyComment().getChildren();
		} else {
			return null;
		}
	}
}
