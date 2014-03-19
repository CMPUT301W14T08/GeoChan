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

import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.MapViewFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Adapter used for displaying a ThreadComment in the ThreadViewFragment. It
 * inflates layouts for OP, top level comments and comment replies.
 */
public class ThreadViewAdapter extends BaseAdapter {
    private static final int TYPE_COMMENT0 = 0;
    private static final int TYPE_COMMENT1 = 1;
    private static final int TYPE_COMMENT2 = 2;
    private static final int TYPE_COMMENT3 = 3;
    private static final int TYPE_COMMENT4 = 4;
    private static final int TYPE_COMMENT5 = 5;
    private static final int TYPE_COMMENT6 = 6;
    private static final int TYPE_COMMENT7 = 7;
    private static final int TYPE_COMMENTMAX = 10;
    private static final int TYPE_OP = 8;
    private static final int TYPE_SEPARATOR = 9;
    private static final int TYPE_MAX_COUNT = 11;

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

    /**
     * This method takes a comment and recursively builds a list of comment
     * objects from the Comment's children tree.
     * 
     * @param comment
     */
    private void buildAList(Comment comment) {
        ArrayList<Comment> children = comment.getChildren();
        if (children.size() == 0) {
            return;
        } else {
            for (Comment c : children) {
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
        // this.comments = new ArrayList<Comment>();
        buildAList(thread.getBodyComment());
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int size = getCountChildren(thread.getBodyComment());
        return size + 2; // The +2 is for OP + Separator
    }

    /**
     * This method recursively counts the total amount of children a comment
     * object has.
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
            return comments.get(position - 2);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = 0;
        if (position == 0) {
            type = TYPE_OP;
        } else if (position == 1) {
            type = TYPE_SEPARATOR;
        } else {
            int depth = ((Comment) getItem(position)).getDepth();
            if (depth <= 7) {
                type = depth;
            } else {
                type = TYPE_COMMENTMAX;
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

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     * 
     * This getView method, depending on the item type, inflates the correct
     * layout. Currently, it is a switch with each of 10 possible layouts having
     * its own case. The code is cumbersome, given sufficient time, I will try
     * to find a more elegant solution.
     */
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
            listenForButtons(convertView, thread.getBodyComment());
            break;

        case TYPE_COMMENT0:
            final Comment comment = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_0, null);
            }
            setCommentFields(convertView, comment);
            listenForButtons(convertView, comment);
            break;

        case TYPE_COMMENT1:
            final Comment comment1 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_1, null);
            }
            setCommentFields(convertView, comment1);
            listenForButtons(convertView, comment1);
            break;

        case TYPE_COMMENT2:
            final Comment comment2 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_2, null);
            }
            setCommentFields(convertView, comment2);
            listenForButtons(convertView, comment2);
            break;

        case TYPE_COMMENT3:
            final Comment comment3 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_3, null);
            }
            setCommentFields(convertView, comment3);
            listenForButtons(convertView, comment3);
            break;

        case TYPE_COMMENT4:
            final Comment comment4 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_4, null);
            }
            setCommentFields(convertView, comment4);
            listenForButtons(convertView, comment4);
            break;

        case TYPE_COMMENT5:
            final Comment comment5 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_5, null);
            }
            setCommentFields(convertView, comment5);
            listenForButtons(convertView, comment5);
            break;

        case TYPE_COMMENT6:
            final Comment comment6 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_6, null);
            }
            setCommentFields(convertView, comment6);
            listenForButtons(convertView, comment6);
            break;

        case TYPE_COMMENT7:
            final Comment comment7 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_7, null);
            }
            setCommentFields(convertView, comment7);
            listenForButtons(convertView, comment7);
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

        case TYPE_COMMENTMAX:
            final Comment commentMax = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_max, null);
            }
            setCommentFields(convertView, commentMax);
            TextView depthMeter = (TextView) convertView
                    .findViewById(R.id.thread_view_comment_depth_meter);
            depthMeter.setText("Max depth + " + Integer.toString(commentMax.getDepth() - 7));
            listenForButtons(convertView, commentMax);
            break;

        }
        return convertView;
    }

    private void listenForButtons(View convertView, final Comment comment) {
        // TODO Auto-generated method stub
        // Here handle button presses
        final ImageButton replyButton = (ImageButton) convertView
                .findViewById(R.id.comment_reply_button);

        final ImageButton starButton = (ImageButton) convertView
                .findViewById(R.id.comment_star_button);
        
        final ImageButton mapButton = (ImageButton) convertView
                .findViewById(R.id.thread_map_button);

        if (starButton != null) {
            starButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(context, "Saved to Favourites.", Toast.LENGTH_SHORT).show();
                    FavouritesLog log = FavouritesLog.getInstance();
                    log.addComment(comment);
                }
            });
        }
        
        if (mapButton != null) {
            mapButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new MapViewFragment();
                    manager.beginTransaction().replace(R.id.fragment_container, fragment, "mapFrag").addToBackStack(null).commit();
                    manager.executePendingTransactions();
                }
            });
        }

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
            DecimalFormat format = new DecimalFormat();
            format.setRoundingMode(RoundingMode.HALF_EVEN);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(4);

            origPostLocationText.setText("Latitude: " + format.format(loc.getLatitude())
                    + " Longitude: " + format.format(loc.getLongitude()));
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
        replyBy.setText("Posted by " + reply.getUser() + "#" + reply.getHash());
        // Comment timestamp
        TextView replyTime = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentDate);
        replyTime.setText(reply.getCommentDateString());
        // Comment location
        TextView replyLocationText = (TextView) convertView
                .findViewById(R.id.thread_view_comment_locationText);
        GeoLocation repLocCom = reply.getLocation();
        if (repLocCom != null) {
            DecimalFormat format = new DecimalFormat();
            format.setRoundingMode(RoundingMode.HALF_EVEN);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(4);

            replyLocationText.setText("Latitude: " + format.format(repLocCom.getLatitude())
                    + " Longitude: " + format.format(repLocCom.getLongitude()));
        } else {
            replyLocationText.setText("Error: No location found");
        }
    }
}