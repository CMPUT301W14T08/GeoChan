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

package ca.ualberta.cmput301w14t08.geochan.loaders;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.AsyncTaskLoader;
import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

public class CommentLoader extends AsyncTaskLoader<ArrayList<Comment>> {
    ArrayList<Comment> list = null;
    ElasticSearchClient client;
    ThreadComment thread;

    public static final int LOADER_ID = 1;

    public CommentLoader(Context context, ThreadComment thread) {
        super(context);
        client = ElasticSearchClient.getInstance();
        this.thread = thread;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.content.AsyncTaskLoader#loadInBackground()
     */
    @Override
    public ArrayList<Comment> loadInBackground() {
        if (list == null) {
            list = new ArrayList<Comment>();
        }
        return client.getComments(thread.getBodyComment());
    }

    @Override
    public void deliverResult(ArrayList<Comment> list) {
        this.list = list;
        if (isStarted()) {
            super.deliverResult(list);
        }
    }

    @Override
    protected void onStartLoading() {
        if (list != null) {
            deliverResult(list);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                int count = client.getCommentCount();
                if (count != list.size()) {
                    forceLoad();
                }
            }
        }, 5000, 60000);

        if (list == null) {
            forceLoad();
        }
    }
}
