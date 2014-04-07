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

package ca.ualberta.cmput301w14t08.geochan.test;

import io.searchbox.client.JestClient;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.ElasticSearchClient;

/**
 * Android JUnit tests for our ElasticSearchClient tests.
 * @author Henry Pabst
 *
 */
public class ElasticSearchClientTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private ElasticSearchClient client;
    
    public ElasticSearchClientTest() {
        super(MainActivity.class);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    /**
     * Tests the proper construction of the ElasticSearchClient instance.
     */
    public void testConstruction() {
        client = ElasticSearchClient.getInstance();
        assertNotNull(client);
    }
    
    /**
     * Tests the retrieval of the JestClient.
     */
    public void testGetJest() {
        JestClient jest = client.getClient();
        assertNotNull(jest);
    }
    
}
