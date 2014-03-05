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
import io.searchbox.core.Search;

import java.util.ArrayList;

import org.elasticsearch.search.builder.SearchSourceBuilder;

import ca.ualberta.cmput301w14t08.geochan.models.Thread;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class ElasticSearchClient {
    private static ElasticSearchClient instance = null;
    private static JestClient client;
    private static final String TYPE_COMMENT = "geoComment";
    private static final String TYPE_THREAD = "geoThread";
    private static final String URL = "http://cmput301.softwareprocess.es:8080";
    private static final String URL_INDEX = "testing";
    
    private ElasticSearchClient() {
        ClientConfig config = new ClientConfig.Builder(URL).multiThreaded(true).build();
        JestClientFactory factory = new JestClientFactory();
        factory.setClientConfig(config);
        client = factory.getObject();
    }
    
    public static ElasticSearchClient getInstance() {
        if (instance == null) {
            instance = new ElasticSearchClient();
        }
        return instance;
    }
    
    public void putThread(Thread thread) {
        Index index = new Index.Builder(thread).index(URL_INDEX).type(TYPE_THREAD).build();
        try {
            client.execute(index);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void setThreads() {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
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
        ThreadList.setThreads(threadList);
    }
}
