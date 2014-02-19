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

package ca.ualberta.cmput301w14t08.geochan;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ThreadListActivity extends Activity {
    private ListView threadListView;
    private ThreadListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_list);
        threadListView = (ListView) findViewById(R.id.thread_list);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Test code, remember to delete:
        Comment comment = new Comment("Testing testing testing testing", null);
        ThreadList.addThread(comment, "First Thread");
        // End of test code
        adapter = new ThreadListAdapter(this, ThreadList.getThreads());
        // Assign custom adapter to the list
        threadListView.setEmptyView(findViewById(R.id.empty_list_view));
        threadListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.thread_list, menu);
        return true;
    }

}
