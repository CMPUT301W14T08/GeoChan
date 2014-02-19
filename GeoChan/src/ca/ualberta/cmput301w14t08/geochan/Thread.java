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
import java.util.Date;

public class Thread {
	private ArrayList<Comment> comments;
	private Comment topComment;
	private Date threadDate;
	
	
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
}
