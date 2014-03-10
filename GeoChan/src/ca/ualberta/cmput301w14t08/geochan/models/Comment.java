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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.graphics.Picture;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;
import ca.ualberta.cmput301w14t08.geochan.helpers.UserHashManager;

public class Comment implements Parcelable {
	private String textPost;
	private Date commentDate;
	private Picture image;
	private Picture imageThumb;
	private GeoLocation location;
	private String user;
	private String hash;
	private int depth;
	private Comment parent;
	private ArrayList<Comment> children;
	private UserHashManager manager;
	private long id;

	/**
	 * a comment without an image and without a parent
	 */
	public Comment(String textPost, GeoLocation location) {
		super();
		this.manager = UserHashManager.getInstance();
		this.setTextPost(textPost);
		this.setCommentDate(new Date());
		this.setImage(null);
		this.setImageThumb(null);
		this.setLocation(location);
		this.setUser(manager.getUser());
		this.setHash(manager.getHash());
		this.depth = -1;
		this.setParent(null);
		this.setChildren(new ArrayList<Comment>());
		this.id = manager.getCommentIdHash();
	}

	/**
	 * a comment with an image and without a parent
	 */
	public Comment(String textPost, Picture image, GeoLocation location) {
		super();
		this.manager = UserHashManager.getInstance();
		this.setTextPost(textPost);
		this.setCommentDate(new Date());
		this.setImage(image);
		this.setImageThumb(image);
		this.setLocation(location);
		this.setUser(manager.getUser());
		this.setHash(manager.getHash());
		this.depth = -1;
		this.setParent(null);
		this.setChildren(new ArrayList<Comment>());
		this.id = manager.getCommentIdHash();
	}

	/**
	 * a comment with an image, with a parent
	 */
	public Comment(String textPost, Picture image, GeoLocation location,
			Comment parent) {
		super();
		this.manager = UserHashManager.getInstance();
		this.setTextPost(textPost);
		this.setCommentDate(new Date());
		this.setImage(image);
		this.setImageThumb(image);
		this.setLocation(location);
		this.setUser(manager.getUser());
		this.setHash(manager.getHash());
		this.depth = parent.depth + 1;
		this.setParent(parent);
		this.setChildren(new ArrayList<Comment>());
		this.id = manager.getCommentIdHash();
	}

	/**
	 * a comment without an image and with a parent
	 */
	public Comment(String textPost, GeoLocation location, Comment parent) {
		super();
		this.manager = UserHashManager.getInstance();
		this.setTextPost(textPost);
		this.setCommentDate(new Date());
		this.setImage(null);
		this.setImageThumb(null);
		this.setLocation(location);
		this.setUser(manager.getUser());
		this.setHash(manager.getHash());
		this.depth = parent.depth + 1;
		this.setParent(parent);
		this.setChildren(new ArrayList<Comment>());
		this.id = manager.getCommentIdHash();
	}

	/**
	 * a comment initialized with no data. Only used for testing.
	 */
	public Comment() {
		super();
		this.textPost = "This is a test comment.";
		this.commentDate = null;
		this.image = null;
		this.location = null;
		this.parent = null;
		this.children = new ArrayList<Comment>();
		this.setUser(new String());
		this.setHash(new String());
		this.depth = -1;
		this.setParent(null);
		this.setChildren(new ArrayList<Comment>());
		this.id = -1;
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

	public String getId() {
		return Long.toString(id);
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Sorts child comments according to the tag passed.
	 * 
	 * @param tag
	 *            Tag to sort comments by
	 */
	public void sortChildren(int tag) {
		switch (tag) {
		case SortTypes.SORT_DATE_NEWEST:
			Collections.sort(this.getChildren(),
					SortTypes.sortCommentsByDateNewest());
			break;
		case SortTypes.SORT_DATE_OLDEST:
			Collections.sort(this.getChildren(),
					SortTypes.sortCommentsByDateOldest());
			break;
		case SortTypes.SORT_LOCATION_OP:
			Collections.sort(this.getChildren(),
					SortTypes.sortCommentsByParentDistance());
			break;
		case SortTypes.SORT_SCORE_HIGHEST:
			Collections.sort(this.getChildren(),
					SortTypes.sortCommentsByParentScoreHighest());
			break;
		case SortTypes.SORT_SCORE_LOWEST:
			Collections.sort(this.getChildren(),
					SortTypes.sortCommentsByParentScoreLowest());
			break;
		}
	}

	public double getDistanceFrom(GeoLocation g) {
		/*
		 * Determines the distance between a comment and a GeoLocation in terms
		 * of latitude and longitude coordinates.
		 */
		return this.getLocation().distance(g);
	}

	public double getTimeFrom(Date d) {
		/*
		 * Determines the amount of time between when a comment was posted and a
		 * date for determining a comment's relative score.
		 */
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(this.getCommentDate());
		cal2.setTime(d);
		long t1 = cal1.getTimeInMillis();
		long t2 = cal2.getTimeInMillis();
		return TimeUnit.MILLISECONDS.toHours(Math.abs(t1 - t2));
	}

	public double getScoreFromParent() {
		/*
		 * Determines the "score" of a comment in relation to its parent. Logs
		 * an error and returns 0 if parent is null, since this shouldn't be
		 * being called on a top comment.
		 */
		int distConst = 25;
		int timeConst = 10;
		int maxScore = 1000;
		/*
		 * These can be changed depending on how we want to weight distance vs.
		 * time for comment scoring.
		 */
		if (this.parent == null) {
			Log.e("Comment:",
					"getScore() was incorrectly called on a top comment.");
			return 0;
		}
		double distScore = distConst
				* (1 / Math.sqrt(this.getDistanceFrom(this.getParent()
						.getLocation())));
		double timeScore = timeConst
				* (1 / Math.sqrt(this.getTimeFrom(this.getParent()
						.getCommentDate())));
		if ((distScore + timeScore) > maxScore) {
			return maxScore;
		} else {
			return distScore + timeScore;
		}
	}

	public String getCommentDateString() {
		SimpleDateFormat formatDate = new SimpleDateFormat("MMM dd/yy",
				Locale.getDefault());
		SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm aa",
				Locale.getDefault());
		return " on " + formatDate.format(commentDate) + " at "
				+ formatTime.format(commentDate);
	}

	/**
	 * @return the imageThumb
	 */
	public Picture getImageThumb() {
		return imageThumb;
	}

	/**
	 * @param imageThumb
	 *            the imageThumb to set
	 */
	public void setImageThumb(Picture imageThumb) {
		this.imageThumb = imageThumb;
	}

	/**
	 * @return the hash
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash
	 *            the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return the depth
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * @param depth
	 *            the depth to set
	 */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(textPost);
		dest.writeValue(commentDate);
		dest.writeValue(image);
		dest.writeValue(location);
		dest.writeValue(user);
		dest.writeParcelable(parent, flags);
		dest.writeTypedList(children);
	}

	public Comment(Parcel in) {
		super();
		this.setTextPost((String) in.readValue(getClass().getClassLoader()));
		this.setCommentDate((Date) in.readValue(getClass().getClassLoader()));
		this.setImage((Picture) in.readValue(getClass().getClassLoader()));
		this.setLocation((GeoLocation) in
				.readValue(getClass().getClassLoader()));
		this.setUser((String) in.readValue(getClass().getClassLoader()));
		this.setParent((Comment) in.readParcelable(getClass().getClassLoader()));
		in.readTypedList(children, Comment.CREATOR);
	}

	public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
		public Comment createFromParcel(Parcel in) {
			return new Comment(in);
		}

		public Comment[] newArray(int size) {
			return new Comment[size];
		}
	};
}
