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

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Adapter used for displaying a ThreadComment in the ThreadViewFragment. It
 * inflates layouts for OP, op level comments and comment replies.
 */
public class ThreadViewAdapter extends BaseAdapter {
    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_OP = 1;
    private static final int TYPE_SEPARATOR = 2;
    private static final int TYPE_MAX_COUNT = 3;

    private Context context;
    private ThreadComment thread;
    private ArrayList<Comment> comments;
    private FragmentManager manager;

    public ThreadViewAdapter(Context context, ThreadComment thread, FragmentManager manager) {
        super();
        this.context = context;
        this.thread = thread;
        this.manager = manager;
        this.comments = new ArrayList<Comment>();
        buildAList(thread.getBodyComment());
    }

    private void buildAList(Comment comment) {
        ArrayList<Comment> children = comment.getChildren();
        if(children.size() == 0) {
            return;
        } else {
            for(Comment c : children) {
                comments.add(c);
                buildAList(c);
            }
        }
    }
    
    /**
     * This method is called once the comments of a thread loaded.
     * 
     * @param thread
     */
    public void setThread(ThreadComment thread) {
        this.thread = thread;
        //this.comments = new ArrayList<Comment>();
        buildAList(thread.getBodyComment());
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = getCountChildren(thread.getBodyComment());
        return size + 2; // The +2 is for OP + Separator
    }

    /**
     * This method recursively counts the amount of children a comment object
     * has
     * 
     * @param comment
     * @return size
     */
    private int getCountChildren(Comment comment) {
        if (comment.getChildren().size() == 0) {
            return 0;
        }
        int size = 0;

        for (Comment c : comment.getChildren()) {
            ++size;
            size += getCountChildren(c);
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return thread.getBodyComment();
        }
        if (position == 1) {
            return null;
        } else {
            return comments.get(position-2);
        }
    }

    /*
    private int getItemGetTC(int position) {
        int count = 0;
        if (position == 0) {
            return count;
        }
        for (int i = 0; i < comments.size(); ++i) {
            Comment topComment = comments.get(i);
            ++count;
            count += getCountChildren(topComment);
            if (count > position) {
                return i;
            }
        }
        return 0;
    }

    private Comment getItemGetComment(int TCindex, int position) {
        int count = 0;
        Comment topComment = comments.get(TCindex);
        // Count all the comments up to the Top Comment in question
        for (int i = 0; i < TCindex; ++i) {
            count += 1;
            count += getCountChildren(comments.get(i));
        }
        // Case where the item at position is a top comment
        if (count == position) {
            return topComment;
        } else {
            int index = position - count;
            return recursiveSearch(topComment, index);
        }
    }
    
    private Comment recursiveSearch(Comment parent, int index) {
        if(index == 0) {
            return parent;
        } else {
            
        }
        return null;
    }
    
    */

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (position == 0) {
            type = TYPE_OP;
        } else if (position == 1) {
            type = TYPE_SEPARATOR;
        } else {
            type = TYPE_COMMENT;
        }
        return type;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        switch (type) {
        case TYPE_OP:
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_op, null);
            }
            setOPFields(convertView);
            break;

        case TYPE_COMMENT:
            final Comment comment = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // This switch inflates the correct layout for comment's "depth"
                // in the tree, where thread body comment is root.
                switch (comment.getDepth()) {
                case 0:
                    convertView = inflater.inflate(R.layout.thread_view_comment_0, null);
                    break;
                case 1:
                    convertView = inflater.inflate(R.layout.thread_view_comment_1, null);
                    break;
                case 2:
                    convertView = inflater.inflate(R.layout.thread_view_comment_2, null);
                    break;
                case 3:
                    convertView = inflater.inflate(R.layout.thread_view_comment_3, null);
                    break;
                case 4:
                    convertView = inflater.inflate(R.layout.thread_view_comment_4, null);
                    break;
                case 5:
                    convertView = inflater.inflate(R.layout.thread_view_comment_5, null);
                    break;
                case 6:
                    convertView = inflater.inflate(R.layout.thread_view_comment_6, null);
                    break;
                case 7:
                    convertView = inflater.inflate(R.layout.thread_view_comment_7, null);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.thread_view_comment_0, null);
                    break;
                }
            }
            setCommentFields(convertView, comment);

            // Here handle button presses
            final ImageButton replyButton = (ImageButton) convertView
                    .findViewById(R.id.comment_reply_button);

            // This if condition will be removed once every comment has its own
            // reply button.
            if (replyButton != null) {
                replyButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        Log.e("ButtonClick", "click");
                        Log.e("Comment being replied:", comment.getTextPost());
                        Fragment fragment = new PostCommentFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("cmt", comment);
                        fragment.setArguments(bundle);

                        manager.beginTransaction()
                                .replace(R.id.fragment_container, fragment, "repFrag")
                                .addToBackStack(null).commit();
                        manager.executePendingTransactions();
                    }
                });
            }

            break;

        case TYPE_SEPARATOR:
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_separator, null);
            }
            TextView numComments = (TextView) convertView.findViewById(R.id.textSeparator);
            numComments.setText(Integer.toString(getCount() - 2) + " Comments:");
            break;
        }
        return convertView;
    }

    /**
     * This method sets all the required fields for the OP
     * 
     * @param convertView
     */
    private void setOPFields(View convertView) {
        // Thread title
        TextView title = (TextView) convertView.findViewById(R.id.thread_view_op_threadTitle);
        title.setText(thread.getTitle());
        // Thread creator
        TextView threadBy = (TextView) convertView.findViewById(R.id.thread_view_op_commentBy);
        threadBy.setText("Posted by " + thread.getBodyComment().getUser() + "#"
                + thread.getBodyComment().getHash());
        // Thread body comment
        TextView body = (TextView) convertView.findViewById(R.id.thread_view_op_commentBody);
        body.setText(thread.getBodyComment().getTextPost());
        // Thread timestamp
        TextView threadTime = (TextView) convertView.findViewById(R.id.thread_view_op_commentDate);
        threadTime.setText(thread.getBodyComment().getCommentDateString());
        // Location text
        TextView origPostLocationText = (TextView) convertView
                .findViewById(R.id.thread_view_op_locationText);
        GeoLocation loc = thread.getBodyComment().getLocation();
        if (loc != null) {
            double origPostLat = Math.round(loc.getLatitude() * 100) / 100;
            double origPostLong = Math.round(loc.getLongitude() * 100) / 100;
            origPostLocationText.setText("Latitude: " + Double.toString(origPostLat)
                    + " Longitude: " + Double.toString(origPostLong));
        } else {
            origPostLocationText.setText("Error: No location found");
        }
    }

    /**
     * This method sets all the equired views for a comment reply
     * 
     * @param convertView
     * @param reply
     */
    private void setCommentFields(View convertView, Comment reply) {
        // Comment body
        TextView replyBody = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBody);
        replyBody.setText(reply.getTextPost());
        // Comment creator
        TextView replyBy = (TextView) convertView.findViewById(R.id.thread_view_comment_commentBy);
        replyBy.setText("Posted by " + reply.getUser() + "#" + thread.getBodyComment().getHash());
        // Comment timestamp
        TextView replyTime = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentDate);
        replyTime.setText(reply.getCommentDateString());
        // Comment location
        TextView replyLocationText = (TextView) convertView
                .findViewById(R.id.thread_view_comment_locationText);
        GeoLocation repLocCom = reply.getLocation();
        if (repLocCom != null) {
            double commentLat = Math.round(repLocCom.getLatitude() * 100) / 100;
            double commentLong = Math.round(repLocCom.getLongitude() * 100) / 100;
            replyLocationText.setText("Latitude: " + Double.toString(commentLat) + " Longitude: "
                    + Double.toString(commentLong));
        } else {
            replyLocationText.setText("Error: No location found");
        }
    }
}
