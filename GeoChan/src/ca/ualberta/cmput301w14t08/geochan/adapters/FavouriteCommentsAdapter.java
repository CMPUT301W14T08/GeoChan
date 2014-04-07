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

package ca.ualberta.cmput301w14t08.geochan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 *  Adapter for the favorites fragment, used to display user's 
 *  favourited comments.
 * 
 * @author Artem Chikin
 * 
 */
public class FavouriteCommentsAdapter extends BaseAdapter {
    private ArrayList<ThreadComment> list;
    private Context context;

    /**
     * Constructs the adapter and initializes its context and list of ThreadComments.
     * @param list  the list
     * @param context  the context
     */
    public FavouriteCommentsAdapter(ArrayList<ThreadComment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    /**
     * Returns the number of entries in the array.
     * @return the number of entries
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Gets the entry at a specified position
     * @param position  the position
     * @return the ThreadComment
     */
    @Override
    public ThreadComment getItem(int position) {
        return list.get(position);
    }

    /** 
     * Returns the id of a specific entry.
     * @param position  the position
     * @return the id 
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Return a location log item view and set fields.
     * @param position the position
     * @param convertView a previous recycled view
     * @param parent parent view
     * @return the view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadComment comment = (ThreadComment) getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.favourites_comment, null);
        }
        // Comment body
        TextView commentBody = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBody);
        commentBody.setText(comment.getBodyComment().getTextPost());
        // Comment creator
        TextView commentBy = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBy);
        commentBy.setText("Posted by " + comment.getBodyComment().getUser() + "#" + comment.getBodyComment().getHash());
        // Comment timestamp
        TextView commentTime = (TextView) convertView
        		.findViewById(R.id.thread_view_comment_commentDate);
        commentTime.setText(comment.getBodyComment().getCommentDateString());
        // Comment location
        TextView commentLocationText = (TextView) convertView
        		.findViewById(R.id.thread_view_comment_location);
        GeoLocation repLocCom = comment.getBodyComment().getLocation();
        if (repLocCom != null) {
        	commentLocationText.setText(repLocCom.getLocationDescription());
        } else {
        	commentLocationText.setText("Error: No location found");
        }
        listenForStarButton(convertView, comment);
        return convertView;
    }
    
    /**
     * Listens for the star button on a favorite comment and removes
     * it from the list when pressed.
     * @param convertView  the View on which to check buttons
     * @param comment  the ThreadComment
     */
    public void listenForStarButton(View convertView, final ThreadComment comment) {
    	ImageButton starButton = (ImageButton) convertView
    			.findViewById(R.id.comment_star_button);
    	starButton.setFocusable(false);
    	if (starButton != null) {
    		starButton.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				FavouritesLog.getInstance(context).removeFavComment(comment.getId());
    				notifyDataSetChanged();
    			}
            });
        }
    	
    }
}
