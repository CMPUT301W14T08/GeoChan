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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;

/**
 * ThreadComment is a model class that handles all operations of threads in the
 * application. It aggregates a Comment object and adds thread specific fields:
 * title, id
 * 
 * @author Henry Pabst, Artem Chikin
 * 
 */
public class ThreadComment implements Parcelable {
    private Comment bodyComment;
    private String title;
    private long id;

    public ThreadComment(Comment bodyComment, String title) {
        super();
        this.title = title;
        this.id = Long.parseLong(bodyComment.getId());
        this.bodyComment = bodyComment;
    }

    /* This constructor is only used for testing. */
    public ThreadComment() {
        super();
        this.title = "This thread is being used to test!";
        this.bodyComment = new Comment();
        Long.parseLong(bodyComment.getId());
    }

    /**
     * Getters and setters
     */
    public Date getThreadDate() {
        return getBodyComment().getCommentDate();
    }

    public void setThreadDate(Date threadDate) {
        getBodyComment().setCommentDate(threadDate);
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
        Long.parseLong(bodyComment.getId());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Determines the distance between the Thread (defined by the GeoLocation of
     * the top comment) and the provided GeoLocation in terms of coordinates.
     * 
     * @param geo
     *            The GeoLocation we want to determine the distance from.
     * @return The distance, in terms of coordinates, between the Thread and the
     *         passed GeoLocation.
     */
    public double getDistanceFrom(GeoLocation geo) {
    	GeoLocation loc = this.getBodyComment().getLocation();
    	double dist = loc.distance(geo);
        return dist;
    }

    /**
     * Determines the time passed between when the Thread was posted and the
     * passed Date in terms of number of hours.
     * 
     * @param date
     *            The Date we are comparing with.
     * @return The number of hours between when the Thread was posted and the
     *         passed Date. Returns a minimum of 0.5.
     */
    public double getTimeFrom(Date date) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(this.getThreadDate());
        cal2.setTime(date);
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
     * @param geo
     *            The GeoLocation relevant to sorting. In sorting, the
     *            Thread.sortLoc GeoLocation of the sorting thread is used and
     *            should be set in the fragment.
     * @return The score of the comment in relation to the user's location and
     *         current time.
     */
    public double getScoreFromUser(GeoLocation geo) {
        int distConst = 25;
        int timeConst = 10;
        long maxScore = 100000000;
        double minScore = 0.0001;

        if (geo == null) {
            Log.e("Thread:" + this.getTitle(),
                    "getScoreFromUser() was incorrectly called with a null location.");
            return 0;
        }
        double distScore = distConst * (1 / Math.sqrt(this.getDistanceFrom(geo)));
        double timeScore = timeConst * (1 / Math.sqrt(this.getTimeFrom(new Date())));
        if (distScore + timeScore > maxScore) {
            return maxScore;
        } else if (distScore + timeScore < minScore) {
            return minScore;
        } else {
            return distScore + timeScore;
        }
    }

    public Comment findCommentById(Comment parent, String id) {
        Comment c = null;
        Log.e("??", "Searching comment " + parent.getId() + " for " + id);
        if (parent.getId().equals(id)) {
            c = parent;
        }
        for (Comment child : parent.getChildren()) {
            Comment c2 = findCommentById(child, id);
            if (c2 != null) {
                c = c2;
            }
        }
        return c;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getBodyComment(), flags);
        dest.writeValue(title);
        dest.writeValue(id);
    }

    public ThreadComment(Parcel in) {
        super();
        this.setBodyComment((Comment) in.readValue(getClass().getClassLoader()));
        this.setTitle((String) in.readValue(getClass().getClassLoader()));
        this.setId((long) in.readLong());
    }

    public static final Parcelable.Creator<ThreadComment> CREATOR = new Parcelable.Creator<ThreadComment>() {
        public ThreadComment createFromParcel(Parcel in) {
            return new ThreadComment(in);
        }

        public ThreadComment[] newArray(int size) {
            return new ThreadComment[size];
        }
    };
}
