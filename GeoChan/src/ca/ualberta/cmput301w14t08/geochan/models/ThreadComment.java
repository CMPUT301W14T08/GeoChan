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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;

/**
 * ThreadComment is a model class that handles all operations of threads in the
 * application. It aggregates a Comment object and adds thread specific fields:
 * title, id
 * 
 */
public class ThreadComment {
    private Comment bodyComment;
    private String title;
    private long id;

    public ThreadComment(Comment bodyComment, String title) {
        super();
        this.bodyComment = bodyComment;
        this.setTitle(title);
        this.id = HashHelper.getCommentIdHash();
    }

    /* This constructor is only used for testing. */
    public ThreadComment() {
        super();
        this.bodyComment = new Comment();
        this.title = "This thread is being used to test!";
        this.id = HashHelper.getCommentIdHash();
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

    public void setId(long id) {
        this.id = id;
    }

    public Comment getBodyComment() {
        return bodyComment;
    }

    public void setBodyComment(Comment bodyComment) {
        this.bodyComment = bodyComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void addComment(Comment c) {
        this.bodyComment.addChild(c);
    }

    /**
     * Determines the distance between the Thread (defined by the GeoLocation of
     * the top comment) and the provided GeoLocation in terms of coordinates.
     * 
     * @param g
     *            The GeoLocation we want to determine the distance from.
     * @return The distance, in terms of coordinates, between the Thread and the
     *         passed GeoLocation.
     */
    public double getDistanceFrom(GeoLocation g) {
        if(this.getBodyComment().getLocation() == null){
            Log.e("Error:","Body comment has null location.");
        }
        return this.getBodyComment().getLocation().distance(g);
    }

    /**
     * Determines the time passed between when the Thread was posted and the
     * passed Date in terms of number of hours.
     * 
     * @param d
     *            The Date we are comparing with.
     * @return The number of hours between when the Thread was posted and the
     *         passed Date. Returns a minimum of 0.5.
     */
    public double getTimeFrom(Date d) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.getThreadDate());
        cal2.setTime(d);
        long t1 = cal1.getTimeInMillis();
        long t2 = cal2.getTimeInMillis();
        if (TimeUnit.MILLISECONDS.toHours(Math.abs(t1 - t2)) < 1) {
            return 0.5;
        } else {
            return TimeUnit.MILLISECONDS.toHours(Math.abs(t1 - t2));
        }
    }

    /**
     * Determines the score of a thread relevant to
     * 
     * @param g
     *            The GeoLocation relevant to sorting. In sorting, the
     *            Thread.sortLoc GeoLocation of the sorting thread is used and
     *            should be set in the fragment.
     * @return The score of the comment in relation to the user's location and
     *         current time.
     */
    public double getScoreFromUser(GeoLocation g) {
        int distConst = 25;
        int timeConst = 10;
        long maxScore = 100000000;
        double minScore = 0.0001;

        if (g == null) {
            Log.e("Thread:" + this.getTitle(), "getScoreFromUser() was incorrectly called with a null location.");
            return 0;
        }
        double distScore = distConst * (1 / Math.sqrt(this.getDistanceFrom(g)));
        double timeScore = timeConst * (1 / Math.sqrt(this.getTimeFrom(new Date())));
        if (distScore + timeScore > maxScore) {
            return maxScore;
        } else if (distScore + timeScore < minScore){
            return minScore;
        } else {
            return distScore + timeScore;
        }
    }

}
