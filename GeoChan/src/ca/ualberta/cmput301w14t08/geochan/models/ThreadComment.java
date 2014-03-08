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

package ca.ualberta.cmput301w14t08.geochan.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import ca.ualberta.cmput301w14t08.geochan.helpers.UserHashManager;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;

public class ThreadComment {
    private ArrayList<Comment> comments;
    private Comment bodyComment;
    private String title;
    private UserHashManager manager;
    private long id;

    public ThreadComment(Comment bodyComment, String title) {
        super();
        this.comments = new ArrayList<Comment>();
        this.bodyComment = bodyComment;
        this.setTitle(title);
        this.manager = UserHashManager.getInstance();
        this.id = manager.getCommentIdHash();
    }

    /**
     * Getters and setters
     */
    public Date getThreadDate() {
        return bodyComment.getCommentDate();
    }

    public void setThreadDate(Date threadDate) {
        bodyComment.setCommentDate(threadDate);
    }
    
    public String getId() {
        return Long.toString(id);
    }

    public Comment getBodyComment() {
        return bodyComment;
    }

    public void setBodyComment(Comment bodyComment) {
        this.bodyComment = bodyComment;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addComment(Comment c) {
        this.comments.add(c);
    }

    /**
     * Sorts thread comments according to the tag passed.
     * 
     * @param tag
     *            Tag to sort comments by
     */
    public void sortComments(int tag) {
        switch (tag) {
        case SortTypes.SORT_DATE_NEWEST:
            Collections.sort(this.getComments(), SortTypes
                    .sortCommentsByDateNewest());
            break;
        case SortTypes.SORT_DATE_OLDEST:
            Collections.sort(this.getComments(), SortTypes
                    .sortCommentsByDateOldest());
            break;
        case SortTypes.SORT_LOCATION_OP:
            Collections.sort(this.getComments(), SortTypes
                    .sortCommentsByParentDistance());
            break;
        case SortTypes.SORT_SCORE_HIGHEST:
            Collections.sort(this.getComments(), SortTypes
                    .sortCommentsByParentScoreHighest());
            break;
        case SortTypes.SORT_SCORE_LOWEST:
            Collections.sort(this.getComments(), SortTypes
                    .sortCommentsByParentScoreLowest());
            break;
        }
    }
}
