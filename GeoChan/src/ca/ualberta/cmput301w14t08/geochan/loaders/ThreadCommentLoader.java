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

import android.content.AsyncTaskLoader;
import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;

public class ThreadCommentLoader extends AsyncTaskLoader<ArrayList<ThreadComment>> {
    public ThreadCommentLoader(Context context) {
        super(context);
        forceLoad(); 
    }

    /* (non-Javadoc)
     * @see android.content.AsyncTaskLoader#loadInBackground()
     */
    @Override
    public ArrayList<ThreadComment> loadInBackground() {
        ElasticSearchClient client = ElasticSearchClient.getInstance();
        ArrayList<ThreadComment> list = client.getThreads();
        ThreadList.setThreads(list);
        return list;
    }
}
