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

package ca.ualberta.cmput301w14t08.geochan.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.core.Count;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.serializers.CommentDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.CommentSerializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class ElasticSearchClient {
	private static ElasticSearchClient instance = null;
	private static Gson gson;
	private static JestClient client;
	private static final String TYPE_COMMENT = "geoComment";
	private static final String TYPE_THREAD = "geoThread";
	private static final String URL = "http://cmput301.softwareprocess.es:8080";
	private static final String URL_INDEX = "testing";

	private ElasticSearchClient() {
		ClientConfig config = new ClientConfig.Builder(URL).multiThreaded(true)
				.build();
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Comment.class, new CommentSerializer());
		builder.registerTypeAdapter(Comment.class, new CommentDeserializer());
		builder.registerTypeAdapter(ThreadComment.class,
				new ThreadCommentSerializer());
		builder.registerTypeAdapter(ThreadComment.class,
				new ThreadCommentDeserializer());
		gson = builder.create();
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(config);
		client = factory.getObject();
	}

	public static ElasticSearchClient getInstance() {
		return instance;
	}

	public static void generateInstance() {
		if (instance == null) {
			instance = new ElasticSearchClient();
		}
	}

	public void postThread(final ThreadComment thread) {
		Thread t = new Thread() {
			@Override
			public void run() {
				String json = gson.toJson(thread);
				Index index = new Index.Builder(json).index(URL_INDEX)
						.type(TYPE_THREAD).id(thread.getId()).build();
				try {
					client.execute(index);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

	public void postComment(final ThreadComment thread,
			final Comment commentToReplyTo, final Comment comment) {
		Thread t = new Thread() {
			@Override
			public void run() {
				String json = gson.toJson(comment);
				Index index = new Index.Builder(json).index(URL_INDEX)
						.type(TYPE_COMMENT).id(comment.getId()).build();
				try {
					client.execute(index);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		t.start();
	}

	public int getThreadCount() {
		Count count = new Count.Builder().addIndex(URL_INDEX)
				.addType(TYPE_THREAD).build();
		JestResult result = null;
		try {
			result = client.execute(count);
			String json = result.getJsonString();
			Type elasticSearchCountResponseType = new TypeToken<ElasticSearchCountResponse>() {
			}.getType();
			ElasticSearchCountResponse esResponse = gson.fromJson(json,
					elasticSearchCountResponseType);
			return esResponse.getCount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public int getCommentCount() {
		Count count = new Count.Builder().addIndex(URL_INDEX)
				.addType(TYPE_COMMENT).build();
		JestResult result = null;
		try {
			result = client.execute(count);
			String json = result.getJsonString();
			Type elasticSearchCountResponseType = new TypeToken<ElasticSearchCountResponse>() {
			}.getType();
			ElasticSearchCountResponse esResponse = gson.fromJson(json,
					elasticSearchCountResponseType);
			return esResponse.getCount();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	public ArrayList<ThreadComment> getThreads() {
		String query = "{\n" + "   \"query\": {\n"
				+ "       \"match_all\" : { } \n" + "   }\n" + "}";
		Search search = new Search.Builder(query).addIndex(URL_INDEX)
				.addType(TYPE_THREAD).build();
		JestResult result = null;
		try {
			result = client.execute(search);
			String json = result.getJsonString();
			Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<ThreadComment>>() {
			}.getType();
			ElasticSearchSearchResponse<ThreadComment> esResponse = gson
					.fromJson(json, elasticSearchSearchResponseType);
			ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
			for (ElasticSearchResponse<ThreadComment> r : esResponse.getHits()) {
				ThreadComment comment = r.getSource();
				list.add(comment);
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<ThreadComment>();
		}

	}

	public ArrayList<Comment> getComments(String id) {
		Search search = new Search.Builder(
				ElasticSearchQueries.getMatchParent(id)).addIndex(URL_INDEX)
				.addType(TYPE_COMMENT).build();
		JestResult result = null;
		try {
			result = client.execute(search);
			String json = result.getJsonString();
			Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<Comment>>() {
			}.getType();
			ElasticSearchSearchResponse<Comment> esResponse = gson.fromJson(
					json, elasticSearchSearchResponseType);
			ArrayList<Comment> list = new ArrayList<Comment>();
			for (ElasticSearchResponse<Comment> r : esResponse.getHits()) {
				Comment comment = r.getSource();
				list.add(comment);
			}
			for (Comment c : list) {
				getChildComments(c);
			}
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Comment>();
		}

	}

	public void getChildComments(Comment comment) {
		ArrayList<Comment> children = getComments(comment.getId());
		for (Comment child : children) {
			getChildComments(child);
		}
		for (Comment child : children) {
			child.setParent(comment);
		}
		comment.setChildren(children);
	}
}
