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
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Update;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

/**
 * This class is responsible for all ElasticSearch operations.
 * 
 */
public class ElasticSearchClient {
    private static ElasticSearchClient instance = null;
    private static Gson gson;
    private static JestClient client;
    private static final String TYPE_COMMENT = "geoComment";
    private static final String TYPE_THREAD = "geoThread";
    private static final String TYPE_INDEX = "geoCommentList";
    private static final String URL = "http://cmput301.softwareprocess.es:8080";
    private static final String URL_INDEX = "testing";

    private ElasticSearchClient() {
        ClientConfig config = new ClientConfig.Builder(URL).multiThreaded(true).build();
        gson = GsonHelper.getGson();
        JestClientFactory factory = new JestClientFactory();
        factory.setClientConfig(config);
        client = factory.getObject();
    }

    /**
     * Returns a single instance of the ElasticSearchClient.
     * 
     * @return the instance
     */
    public static ElasticSearchClient getInstance() {
        if (instance == null) {
            instance = new ElasticSearchClient();
        }
        return instance;
    }

    /**
     * Posts a ThreadComment to the ElasticSearch server.
     * 
     * @param thread
     *            the ThreadComment
     * @return the Thread for the work for monitoring purposes
     */
    public Thread postThread(final ThreadComment thread) {
        return post(gson.toJson(thread), TYPE_THREAD, thread.getId());
    }

    /**
     * Posts a Comment to the ElasticSearch server.
     * 
     * @param thread
     *            the ThreadComment
     * @return the Thread for the work for monitoring purposes
     */
    public Thread postComment(final ThreadComment thread, final Comment commentToReplyTo,
            final Comment comment) {
        String query = ElasticSearchQueries.commentListScript(comment.getId());
        update(query, TYPE_INDEX, commentToReplyTo.getId());
        return post(gson.toJson(comment), TYPE_COMMENT, comment.getId());
    }

    /**
     * Returns the total number of ThreadComments on the server
     * 
     * @return number of ThreadComments
     */
    public int getThreadCount() {
        return count(TYPE_THREAD);
    }

    /**
     * Returns the total number of Comments under a parent comment on the server
     * 
     * @return number of Comments
     */
    public int getCommentCount(Comment parent) {
        return count(TYPE_COMMENT);
    }

    /**
     * Gets an ArrayList of ThreadComments from the server
     * 
     * @return the ThreadComments
     */
    public ArrayList<ThreadComment> getThreads() {
        Type elasticSearchSearchResponseType = new TypeToken<ElasticSearchSearchResponse<ThreadComment>>() {
        }.getType();
        ElasticSearchSearchResponse<ThreadComment> esResponse = gson.fromJson(
                searchAll(ElasticSearchQueries.SEARCH_MATCH_ALL, TYPE_THREAD),
                elasticSearchSearchResponseType);
        ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
        for (ElasticSearchResponse<ThreadComment> r : esResponse.getHits()) {
            ThreadComment object = r.getSource();
            list.add(object);
        }
        return list;
    }

    /**
     * Gets a list of comments for a specified parent comment
     * 
     * @param topComment
     *            the parent comment
     * @return the Comments
     */
    public ArrayList<Comment> getComments(Comment topComment) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        Get get = new Get.Builder(URL_INDEX, topComment.getId()).type(TYPE_INDEX).build();
        JestResult result = null;
        try {
            result = client.execute(get);
            JsonArray array = result.getJsonObject().get("_source").getAsJsonObject()
                    .get("comments").getAsJsonArray();
            ArrayList<String> hits = new ArrayList<String>();
            for (int i = 0; i < array.size(); ++i) {
                hits.add(array.get(i).getAsString());
            }
            for (String hit : hits) {
                comments.add(get(hit));
            }
            for (Comment comment : comments) {
                comment.setParent(topComment);
                ArrayList<Comment> children = getComments(comment);
                if (children != null) {
                    comment.setChildren(children);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return comments;
    }

    /**
     * Gets a Comment, specified by its id
     * 
     * @param id
     *            the id
     * @return the Comment
     */
    public Comment get(final String id) {
        Get get = new Get.Builder(URL_INDEX, id).type(TYPE_COMMENT).build();
        JestResult result = null;
        try {
            result = client.execute(get);
            Type type = new TypeToken<ElasticSearchResponse<Comment>>() {
            }.getType();
            ElasticSearchResponse<Comment> esResponse = gson.fromJson(result.getJsonString(), type);
            return esResponse.getSource();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Posts a JSON string request to the ElasticSearch server
     * 
     * @param json
     *            the JSON query string
     * @param type
     *            the ElasticSearch type
     * @param id
     *            the record ID
     * @return the network Thread (for monitoring purposes)
     */
    public Thread post(final String json, final String type, final String id) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Index index = new Index.Builder(json).index(URL_INDEX).type(type).id(id).build();
                try {
                    client.execute(index);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Updates a record on the ElasticSearch server
     * 
     * @param query
     *            the JSON query string
     * @param type
     *            the ElasticSearch type
     * @param id
     *            the record ID
     * @return the network Thread (for monitoring purposes)
     */
    public Thread update(final String query, final String type, final String id) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Update update = new Update.Builder(query).index(URL_INDEX).type(type).id(id)
                        .build();
                try {
                    client.execute(update);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Counts the number of records on the ElasticSearch server
     * 
     * @param type
     *            the ElasticSearch type
     * @return the count
     */
    public int count(final String type) {
        Count count = new Count.Builder().addIndex(URL_INDEX).addType(type).build();
        JestResult result = null;
        try {
            result = client.execute(count);
            Type elasticSearchCountResponseType = new TypeToken<ElasticSearchCountResponse>() {
            }.getType();
            ElasticSearchCountResponse esResponse = gson.fromJson(result.getJsonString(),
                    elasticSearchCountResponseType);
            return esResponse.getCount();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Searches the ElasticSearch server for all results for a specific type
     * 
     * @param query
     *            the JSON query string
     * @param type
     *            the ElasticSearch type
     * @return the JSON result string
     */
    public String searchAll(final String query, final String type) {
        Search search = new Search.Builder(query).addIndex(URL_INDEX).addType(type).build();
        JestResult result = null;
        try {
            result = client.execute(search);
            return result.getJsonString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
}