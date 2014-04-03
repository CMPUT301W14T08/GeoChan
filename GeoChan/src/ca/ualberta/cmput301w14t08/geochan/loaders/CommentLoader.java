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

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CommentList;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Responsible for asynchronously loading Comments from the ElasticSearch server
 * for delivery to an adapter.
 * 
 */
public class CommentLoader extends AsyncTaskLoader<ArrayList<Comment>> {
    private ArrayList<Comment> list = null;
    private ElasticSearchClient client;
    private PreferencesManager manager;
    private ThreadComment thread;
    private Boolean loading = false;
    private CommentList commentList = null;

    public static final int LOADER_ID = 1;

    public CommentLoader(Context context, ThreadComment thread) {
        super(context);
        client = ElasticSearchClient.getInstance();
        this.thread = thread;
        this.manager = PreferencesManager.getInstance();
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
        loading = true;
        commentList = null;
        ThreadManager.startGetCommentList(this, thread.getId());
        while (commentList == null) {};
        recursiveGetComments(commentList);
        while (loading) {};
        return list;
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

        if (list == null) {
            forceLoad();
        }
    }
    
    public void recursiveGetComments(CommentList list) {
        for (CommentList cl: list.getComments()) {
            ThreadManager.startGetComment(this, cl.getId());
            recursiveGetComments(cl);
        }
    }

    /**
     * @return the list
     */
    public ArrayList<Comment> getList() {
        return list;
    }

    /**
     * @param list the list to set
     */
    public void setList(ArrayList<Comment> list) {
        this.list = list;
    }

    /**
     * @return the loading
     */
    public Boolean getLoading() {
        return loading;
    }

    /**
     * @param loading the loading to set
     */
    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    /**
     * @return the commentList
     */
    public CommentList getCommentList() {
        return commentList;
    }

    /**
     * @param commentList the commentList to set
     */
    public void setCommentList(CommentList commentList) {
        this.commentList = commentList;
    }
}
