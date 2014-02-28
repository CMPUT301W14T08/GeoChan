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

import android.graphics.Picture;
import android.location.Location;

public class Comment {
    private String textPost;
    private Date commentDate;
    private Picture image;
    private Location location;
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
    public Comment(String textPost, Location location) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(null);
        this.setLocation(location);
        this.setParent(null);
        this.setChildren(new ArrayList<Comment>());
    }

    /**
     * a comment with an image and without a parent
     */
    public Comment(String textPost, Picture image, Location location) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(image);
        this.setLocation(location);
        this.setParent(null);
        this.setChildren(new ArrayList<Comment>());
    }

    /**
     * a comment with an image, with a parent
     */
    public Comment(String textPost, Picture image, Location location, Comment parent) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(image);
        this.setLocation(location);
        this.setParent(parent);
        parent.addChild(this);
        this.setChildren(new ArrayList<Comment>());
    }

    /**
     * a comment without an image and with a parent
     */
    public Comment(String textPost, Location location, Comment parent) {
        super();
        this.setTextPost(textPost);
        this.setCommentDate(new Date());
        this.setImage(null);
        this.setLocation(location);
        this.setParent(parent);
        parent.addChild(this);
        this.setChildren(new ArrayList<Comment>());
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
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
