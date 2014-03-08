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
import io.searchbox.client.config.ClientConfig;
import io.searchbox.core.Index;
import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.serializers.CommentSerializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ElasticSearchClient {
    private Context context;
    private static ElasticSearchClient instance = null;
    private static JestClient client;
    private static final String TYPE_COMMENT = "geoComment";
    private static final String TYPE_THREAD = "geoThread";
    private static final String URL = "http://cmput301.softwareprocess.es:8080";
    private static final String URL_INDEX = "testing";
    
    private ElasticSearchClient(Context context) {
        this.context = context;
        ClientConfig config = new ClientConfig.Builder(URL).multiThreaded(true).build();
        JestClientFactory factory = new JestClientFactory();
        factory.setClientConfig(config);
        client = factory.getObject();
    }
    
    public static ElasticSearchClient getInstance() {
        return instance;
    }
    
    public static void generateInstance(Context context) {
        if (instance == null) {
            instance = new ElasticSearchClient(context);
        }
    }
    
    public void postThread(final ThreadComment thread) {
        Thread t = new Thread() {
            @Override
            public void run() {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(ThreadComment.class, new ThreadCommentSerializer());
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(thread);
                Index index = new Index.Builder(json).index(URL_INDEX).type(TYPE_THREAD).id(thread.getId()).build();
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
    
    public void postComment(final ThreadComment thread, final Comment commentToReplyTo, final Comment comment) {
        Thread t = new Thread() {
            @Override
            public void run() {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(Comment.class, new CommentSerializer());
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(comment);
                Index index = new Index.Builder(json).index(URL_INDEX).type(TYPE_COMMENT).id(comment.getId()).build();
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
    
    public String getThreads() {
        return null;
    }
    
    public String getComments() {
        return null;
    }
}
