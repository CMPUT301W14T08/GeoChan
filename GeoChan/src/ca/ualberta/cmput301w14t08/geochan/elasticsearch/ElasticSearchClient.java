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
import io.searchbox.core.Index;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;

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
    
    public static ElasticSearchClient getInstance(Context context) {
        if (instance == null) {
            instance = new ElasticSearchClient(context);
        }
        return instance;
    }
    
    public void putThread(ThreadComment thread) {
        Index index = new Index.Builder(thread).index(URL_INDEX).type(TYPE_THREAD).build();
        try {
            client.execute(index);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public ArrayList<ThreadComment> getThreads() {
        ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
        SharedPreferences sortPref = context.getSharedPreferences("sort", Context.MODE_PRIVATE);
        int sortType = sortPref.getInt("thread", SortTypes.SORT_DATE_NEWEST);
        return list;
        // SearchSourceBuilder needs elasticsearch jar which is huge and inefficient
        // do this using JSON instead
        
        /* SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //searchSourceBuilder.query(QueryBuilders.matchQuery(name, text))
        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex(URL_INDEX)
                .build();
        JestResult result = null;
        try {
            result = client.execute(search);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArrayList<Thread> threadList = new ArrayList<Thread>();
        threadList.addAll(result.getSourceAsObjectList(Thread.class));
        ThreadList.setThreads(threadList); */
    }
    
    public void test(final ThreadComment thread) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String json = gson.toJson(thread);
                Index index = new Index.Builder(json).index("testing").type("yolo").id("1").build();
                Log.e("lol", "test");
                JestResult result;
                try {
                    result = client.execute(index);
                    Log.e("lol", result.toString());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("lol", e.toString());
                }
               
            }
        };
        t.start();
    }
}
