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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Thread {
    private ArrayList<Comment> comments;
    private Comment topComment;
    private Date threadDate;
    private String title;

    public Thread(Comment topComment, String title) {
        super();
        this.comments = new ArrayList<Comment>();
        this.topComment = topComment;
        this.threadDate = new Date();
        this.setTitle(title);
    }

    /**
     * Getters and setters
     */
    public Date getThreadDate() {
        return threadDate;
    }

    public void setThreadDate(Date threadDate) {
        this.threadDate = threadDate;
    }

    public Comment getTopComment() {
        return topComment;
    }

    public void setTopComment(Comment topComment) {
        this.topComment = topComment;
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
    
    public void addComment(Comment c){
        this.getComments().add(c);
    }
    
    public void sortComments(String tag){
        /*
         * Sorts the thread's comments based
         * on the tag passed in.
         * "DATE_NEWEST" pushes the most recent comments to the top.
         * "DATE_OLDEST" pushes the oldest comments to the top.
         * "LOCATION_OP" pushes the closest comments to the OP to the top.
         * "SCORE_HIGHEST" pushes the highest scored comments to the top.
         * "SCORE_LOWEST" pushes the lowest scored comments to the top.
         */
        
        /*
         * Can't use switch statements and strings. GG Android.
         */
        
        if(tag == "DATE_NEWEST"){
            Collections.sort(this.getComments(), SortComparators.sortCommentsByDateNewest());
            
        } else if(tag == "DATE_OLDEST"){
            Collections.sort(this.getComments(), SortComparators.sortCommentsByDateOldest());
            
        } else if(tag == "LOCATION_OP"){
            /*
             * To be implemented once location work is finished.
             */
            
        } else if(tag == "SCORE_HIGHEST"){
            /*
             * To be implemented once location work is finished.
             */
            
        } else if(tag == "SCORE_LOWEST"){
            /*
             * To be implemented once location work is finished.
             */
            
        }
        
        return;
    }
    
}