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

import com.google.gson.annotations.Expose;

/**
 * Container class that stores a comment object, its id, and a list of children
 * commentLists that match the Comment's child replies. Used for posting lists
 * of comments to ElasticSearch and rebuilding a comment tree from
 * ElasticSearch.
 * 
 * @author Artem Chikin
 * @author Artem Herasymchuk
 * 
 */
public class CommentList {
	@Expose
	private ArrayList<CommentList> comments;
	@Expose
	private String id;
	private Comment comment;

	public CommentList(Comment comment) {
		comments = new ArrayList<CommentList>();
		this.id = comment.getId();
		this.comment = comment;
	}

	public CommentList(String id) {
		comments = new ArrayList<CommentList>();
		this.id = id;
		this.comment = null;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Comment getComment() {
		return comment;
	}

	public void setChildren(ArrayList<CommentList> comments) {
		this.comments = comments;
	}

	public void addCommentList(CommentList commentList) {
		comments.add(commentList);
	}

	public ArrayList<CommentList> getChildren() {
		return comments;
	}

	/**
	 * Recursively finds and returns the commentList with a given id.
	 * 
	 * @param commentList
	 *            search starting node
	 * @param id
	 *            id to search by
	 * @return The found CommentList. Return value is null if the CommentList is not found.
	 */
	public CommentList findCommentListById(CommentList commentList, String id) {
		CommentList c = null;
		if (commentList.getId().equals(id)) {
			c = commentList;
		}
		for (CommentList child : commentList.getChildren()) {
			CommentList c2 = findCommentListById(child, id);
			if (c2 != null) {
				c = c2;
			}
		}
		return c;
	}

	/**
	 * Gets and stores all CommentList IDs in the passed ArrayList of Strings recursively
	 * for the passed CommentList and all its children.
	 * @param list The ArrayList to store the found IDs.
	 * @param idList The CommentList to start getting IDs from.
	 */
	public void getIdsFromList(CommentList list, ArrayList<String> idList) {
		idList.add(list.getId());
		for (CommentList childList : list.getChildren()) {
			getIdsFromList(childList, idList);
		}
	}

	// Should only be called on bodyComment, returns the bodyComment with the
	// children all set.
	// depth first traverasl
	/**
	 * Reconstructs the parent-child relationship between Comments given a CommentList
	 * and the top Comment.
	 * @param list The CommentList to construct the relationship from.
	 * @param comment The top Comment to get children of.
	 * @return The passed in Comment with the parent-child relationship constructed.
	 */
	public Comment reconsructFromCommentList(CommentList list, Comment comment) {
		comment.setChildren(new ArrayList<Comment>());
		if (list.getChildren().size() == 0) {
			return comment;
		} else {
			for (CommentList cl : list.getChildren()) {
				comment.addChild(reconsructFromCommentList(cl, cl.getComment()));
			}
		}
		return comment;
	}
}
