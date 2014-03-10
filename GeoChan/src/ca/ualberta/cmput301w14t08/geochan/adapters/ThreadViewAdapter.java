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
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostReplyFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Adapter used for displaying a ThreadComment in the
 * ThreadViewFragment.
 */
public class ThreadViewAdapter extends BaseAdapter {
    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_OP = 1;
    private static final int TYPE_SEPARATOR = 2;
    private static final int TYPE_COMMENT_REPLY = 3;
    private static final int TYPE_MAX_COUNT = 4;

    private Context context;
    private ThreadComment thread;
    private ArrayList<Comment> comments;
    private FragmentManager manager;

    public ThreadViewAdapter(Context context, ThreadComment thread, FragmentManager manager) {
        super();
        this.context = context;
        this.thread = thread;
        this.manager = manager;
        this.comments = this.thread.getComments();
    }

    public void setThread(ThreadComment thread) {
        this.thread = thread;
        this.comments = this.thread.getComments();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = thread.getComments().size() + 2; // OP + separator + top
                                                    // comments
        for (Comment c : comments) {
            size = size + c.getChildren().size();
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
            int TCindex = getItemGetTC(position - 2);
            //Log.e("TCindex", Integer.toString(TCindex));

            int Cindex = getItemGetChild(TCindex, position - 2);
            //Log.e("Cindex", Integer.toString(Cindex));

            if (Cindex == -1) {
                return comments.get(TCindex);
            } else {
                return comments.get(TCindex).getChildren().get(Cindex);
            }
        }
    }

    // Get top comment related to the position.
    private int getItemGetTC(int position) {
        int count = 0;
        if (position == 0) {
            return 0;
        }

        for (int i = 0; i < comments.size(); ++i) {
            Comment topComment = comments.get(i);
            ++count;
            count = count + topComment.getChildren().size();
            if (count > position) {
                return i;
            }
        }
        // Return which top comment this item belongs to, -1 for index
        // compensation
        // i.e. if it's the first top comment, we want index 0.
        return 0;
    }

    private int getItemGetChild(int TCindex, int position) {
        // int childIndex = 0;
        int count = 0;
        // Count all the comments up to the Top Comment in question
        for (int i = 0; i < TCindex; ++i) {
            count += 1;
            count += comments.get(i).getChildren().size();
        }
        // Case where the item at position is a top comment
        if (count == position) {
            return -1;
        } else {
            return position - count - 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (position == 0) {
            type = TYPE_OP;
        }

        else if (position == 1) {
            type = TYPE_SEPARATOR;
        }

        else if (position > 1) {
            int Cindex = getItemGetChild(getItemGetTC(position - 2), position - 2);
            if (Cindex == -1) {
                type = TYPE_COMMENT;
            } else {
                type = TYPE_COMMENT_REPLY;
            }
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
                convertView = inflater.inflate(R.layout.thread_view_top_comment, null);
            }

            setTopCommentFields(convertView, comment);
            // Here handle button presses
            final TextView replyButton = (TextView) convertView
                    .findViewById(R.id.comment_reply_button);
            replyButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Log.e("ButtonClick", "click");
                    Log.e("Comment being replied:", comment.getTextPost());
                    Fragment fragment = new PostReplyFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cmt", comment);
                    fragment.setArguments(bundle);

                    manager.beginTransaction()
                            .replace(R.id.fragment_container, fragment, "repFrag")
                            .addToBackStack(null).commit();
                    manager.executePendingTransactions();
                }
            });
            break;

        case TYPE_COMMENT_REPLY:
            Comment reply = (Comment) getItem(position);
            if (convertView == null) {
                /** Code here will choose depth of reply layout */

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_1, null);
            }
            setCommentReplyFields(convertView, reply);
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

    private void setTopCommentFields(View convertView, Comment comment) {
        // Comment body
        TextView commentBody = (TextView) convertView
                .findViewById(R.id.thread_view_top_comment_commentBody);
        commentBody.setText(comment.getTextPost());
        // Comment creator
        TextView commentBy = (TextView) convertView
                .findViewById(R.id.thread_view_top_comment_commentBy);
        commentBy.setText("posted by " + comment.getUser() + "#" + comment.getHash());
        // Comment timestamp
        TextView commentTime = (TextView) convertView
                .findViewById(R.id.thread_view_top_comment_commentDate);
        commentTime.setText(comment.getCommentDateString());
        // Comment location
        TextView commentLocationText = (TextView) convertView
                .findViewById(R.id.thread_view_top_comment_locationText);
        GeoLocation locCom = comment.getLocation();
        if (locCom != null) {
            double commentLat = Math.round(locCom.getLatitude() * 100) / 100;
            double commentLong = Math.round(locCom.getLongitude() * 100) / 100;
            commentLocationText.setText("Latitude: " + Double.toString(commentLat) + " Longitude: "
                    + Double.toString(commentLong));
        } else {
            commentLocationText.setText("Error: No location found");
        }
    }

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

    private void setCommentReplyFields(View convertView, Comment reply) {
        // Comment body
        TextView replyBody = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBody);
        replyBody.setText(reply.getTextPost());
        // Comment creator
        TextView replyBy = (TextView) convertView.findViewById(R.id.thread_view_comment_commentBy);
        replyBy.setText("Posted by " + reply.getUser() + "#"
                + thread.getBodyComment().getHash());
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

    // TODO
    public void addTopComment() {

        notifyDataSetChanged();
    }
}
