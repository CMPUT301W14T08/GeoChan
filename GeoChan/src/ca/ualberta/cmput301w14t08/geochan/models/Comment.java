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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ca.ualberta.cmput301w14t08.geochan.helpers.SortComparators;
import android.graphics.Picture;
import android.util.Log;

public class Comment {
    private String textPost;
    private Date commentDate;
    private Picture image;
    private GeoLocation location;
    private String user;
    /**
     * parent is the comment this comment is replying to
     */
    private Comment parent;
    /**
     * child is a reply to this comment
     */
    private ArrayList<Comment> children;

    /**
     * a comment without an image and without a parent
     */
    public Comment(String textPost, GeoLocation location) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(null);
        this.setLocation(location);
        this.setParent(null);
        this.setChildren(new ArrayList<Comment>());
        this.setUser(new String());
    }

    /**
     * a comment with an image and without a parent
     */
    public Comment(String textPost, Picture image, GeoLocation location) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(image);
        this.setLocation(location);
        this.setParent(null);
        this.setChildren(new ArrayList<Comment>());
        this.setUser(new String());
    }

    /**
     * a comment with an image, with a parent
     */
    public Comment(String textPost, Picture image, GeoLocation location, Comment parent) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(image);
        this.setLocation(location);
        this.setParent(parent);
        parent.addChild(this);
        this.setChildren(new ArrayList<Comment>());
        this.setUser(new String());
    }

    /**
     * a comment without an image and with a parent
     */
    public Comment(String textPost, GeoLocation location, Comment parent) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(null);
        this.setLocation(location);
        this.setParent(parent);
        parent.addChild(this);
        this.setChildren(new ArrayList<Comment>());
        this.setUser(new String());
    }
    
    /**
     * a comment initialized with no data. Only used for testing.
     */
    public Comment(){
        super();
        this.textPost = "This is a test comment.";
        this.commentDate = null;
        this.image = null;
        this.location = null;
        this.parent = null;
        this.children = new ArrayList<Comment>();
        this.setUser(new String());
    }

    public boolean hasImage() {
        return !(image == null);
    }

    public void addChild(Comment comment) {
        children.add(comment);
    }

    /**
     * Getters and setters
     */
    public String getTextPost() {
        return textPost;
    }

    public void setTextPost(String textPost) {
        this.textPost = textPost;
    }

    public Picture getImage() {
        return image;
    }

    public void setImage(Picture image) {
        this.image = image;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public ArrayList<Comment> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Comment> children) {
        this.children = children;
    }
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
    
    public void sortChildren(String tag){
        /*
         * Sorts child comments according to the tag passed.
         * "DATE_NEWEST" pushes the most recent children to the top.
         * "DATE_OLDEST" pushes the oldest children to the top.
         * "LOCATION_OP" pushes the closest children to the parent to the top.
         * "SCORE_HIGHEST" pushes the highest scored children to the top.
         * "SCORE_LOWEST" pushes the lowest scored children to the top.
         */
         
        if(tag == "DATE_NEWEST"){
            Collections.sort(this.getChildren(), SortComparators.sortCommentsByDateNewest());
            
        } else if(tag == "DATE_OLDEST"){
            Collections.sort(this.getChildren(), SortComparators.sortCommentsByDateOldest());
            
        } else if(tag == "LOCATION_OP"){
            Collections.sort(this.getChildren(), SortComparators.sortCommentsByParentDistance());
            
        } else if(tag == "PARENT_SCORE_HIGHEST"){
            Collections.sort(this.getChildren(), SortComparators.sortCommentsByParentScoreHighest());
            
        } else if(tag == "PARENT_SCORE_LOWEST"){
            Collections.sort(this.getChildren(), SortComparators.sortCommentsByParentScoreLowest());
            
        }
        
        return;
    }    
    
    public double getDistanceFrom(GeoLocation g){
        /*
         * Determines the distance between a comment and a GeoLocation
         * in terms of latitude and longitude coordinates.
         */
        return this.getLocation().distance(g);
    }
    
    public double getTimeFrom(Date d){
        /*
         * Determines the amount of time between when a comment
         * was posted and a date for determining a comment's relative score.
         */
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.getCommentDate());
        cal2.setTime(d);
        long t1 = cal1.getTimeInMillis();
        long t2 = cal2.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toHours(Math.abs(t1 - t2));
    }
    
    public double getScoreFromParent(){
        /*
         * Determines the "score" of a comment
         * in relation to its parent. Logs an error
         * and returns 0 if parent is null, since this
         * shouldn't be being called on a top comment.
         */
        int distConst = 25;
        int timeConst = 10;
        int maxScore = 1000;
        /*These can be changed depending on how we want to weight distance vs. time
         * for comment scoring.*/
        if(this.parent == null){
            Log.e("Comment:","getScore() was incorrectly called on a top comment.");
            return 0;
        }
        double distScore = distConst * (1/Math.sqrt(this.getDistanceFrom(this.getParent().getLocation())));
        double timeScore = timeConst * (1/Math.sqrt(this.getTimeFrom(this.getParent().getCommentDate())));
        if ((distScore + timeScore) > maxScore){
            return maxScore;
        }else{
            return distScore + timeScore;      
        } 
    }
}
