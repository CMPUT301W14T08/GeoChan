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

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.MapViewFragment;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * Adapter used for displaying a ThreadComment in the ThreadViewFragment. It
 * inflates layouts for OP, top level comments and comment replies.
 * 
 * @author AUTHOR HERE
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
    private int id;
    private ThreadComment thread;
    private ArrayList<Comment> comments;
    private FragmentManager manager;

    public ThreadViewAdapter(Context context, ThreadComment thread, FragmentManager manager, int id) {
        super();
        this.context = context;
        this.thread = thread;
        this.manager = manager;
        this.id = id;
        this.comments = new ArrayList<Comment>();
        buildAList(thread.getBodyComment());
    }

    /**
     * This method takes a comment and recursively builds a list of comment
     * objects from the Comment's children tree.
     * 
     * @param comment
     * 
     * @author AUTHOR HERE
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
     * 
     * @author AUTHOR HERE
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
     * 
     * @author AUTHOUR HERE
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
     * its own case. Apparently if one is to have 10 layouts in a listview, one 
     * is to have a switch like this, uniting different layouts under single type
     * does not work.
     */
    @Override
    /**
     * COMMENT HERE
     * 
     * @author AUTHOR HERE
     */
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
            listenForThreadButtons(convertView, thread);
            break;

        case TYPE_COMMENT0:
            final Comment comment = (Comment) getItem(position);
            if (convertView == null) {
                if (comment.hasImage()) {
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.thread_view_top_comment_image, null);
                } else {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_0, null);
                }
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT1:
            final Comment comment1 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_1, null);
            }
            setCommentFields(convertView, comment1);
            break;

        case TYPE_COMMENT2:
            final Comment comment2 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_2, null);
            }
            setCommentFields(convertView, comment2);
            break;

        case TYPE_COMMENT3:
            final Comment comment3 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_3, null);
            }
            setCommentFields(convertView, comment3);
            break;

        case TYPE_COMMENT4:
            final Comment comment4 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_4, null);
            }
            setCommentFields(convertView, comment4);
            break;
        case TYPE_COMMENT5:
            final Comment comment5 = (Comment) getItem(position);
            if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_5, null);
                }
            setCommentFields(convertView, comment5);
            break;

        case TYPE_COMMENT6:
            final Comment comment6 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_6, null);
            }
            setCommentFields(convertView, comment6);
            break;

        case TYPE_COMMENT7:
            final Comment comment7 = (Comment) getItem(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.thread_view_comment_7, null);
            }
            setCommentFields(convertView, comment7);
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
            break;

        }
        return convertView;
    }

    /**
     * COMMENT HERE
     * @param convertView
     * @param thread
     * 
     * @author AUTHOR HERE
     */
    private void listenForThreadButtons(View convertView, final ThreadComment thread) {
        // Here handle button presses
        final ImageButton replyButton = (ImageButton) convertView
                .findViewById(R.id.comment_reply_button);

        final ImageButton starButton = (ImageButton) convertView
                .findViewById(R.id.comment_star_button);
        if(FavouritesLog.getInstance(context).hasThreadComment(thread.getId())) {
            starButton.setImageResource(R.drawable.ic_rating_marked);
        }

        final ImageButton mapButton = (ImageButton) convertView
                .findViewById(R.id.thread_map_button);

        if (starButton != null) {
            starButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!FavouritesLog.getInstance(context).hasThreadComment(thread.getId())) {
                        Toast.makeText(context, "Thread saved to Favourites.", Toast.LENGTH_SHORT).show();
                        starButton.setImageResource(R.drawable.ic_rating_marked);
                        FavouritesLog log = FavouritesLog.getInstance(context);
                        log.addThreadComment(thread);
                    } else {
                        Toast.makeText(context, "Thread removed from Favourites.", Toast.LENGTH_SHORT).show();
                        starButton.setImageResource(R.drawable.ic_rating_important);
                        FavouritesLog log = FavouritesLog.getInstance(context);
                        log.removeThreadComment(thread);
                    }
                }
            });
        }

        if (mapButton != null) {
            mapButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.e("ButtonClick", "mapView");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("thread_comment", thread.getBodyComment());

                    Fragment mapFrag = new MapViewFragment();
                    mapFrag.setArguments(bundle);
                    Fragment fav = manager.findFragmentByTag("favThrFragment");
                    if (fav != null) {
                        manager.beginTransaction().replace(R.id.container, mapFrag, "mapFrag")
                                .addToBackStack(null).commit();
                    } else {
                        manager.beginTransaction()
                                .replace(R.id.fragment_container, mapFrag, "mapFrag")
                                .addToBackStack(null).commit();
                    }
                    manager.executePendingTransactions();
                }
            });
        }

        if (replyButton != null) {
            replyButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Log.e("ButtonClick", "click");
                    Log.e("Comment being replied:", thread.getBodyComment().getTextPost());
                    Fragment fragment = new PostCommentFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("cmt", thread.getBodyComment());
                    bundle.putLong("id", id);
                    fragment.setArguments(bundle);

                    manager.beginTransaction()
                            .replace(R.id.fragment_container, fragment, "repFrag")
                            .addToBackStack(null).commit();
                    manager.executePendingTransactions();
                }

            });
        }
        // Comment image
        if (comment.hasImage()) {
            ImageView image = (ImageView) convertView.findViewById(R.id.imageButton1);
            image.setImageBitmap(comment.getImageThumb());
        }
    }

    /**
     * This method sets all the required fields for the OP
     * NEEDS MORE DETAILED COMMENT
     * 
     * @param convertView
     * 
     * @author AUTHOUR HERE
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
     * This method sets all the required views for a comment reply.
     * NEED MORE DETAILED COMMENT
     * 
     * @param convertView
     * @param reply
     * 
     * @author AUTHOR HERE
     */
    private void setCommentFields(View convertView, Comment reply) {
        // Comment body
        TextView replyBody = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBody);
        replyBody.setText(reply.getTextPost());
        // Comment creator
        TextView replyBy = (TextView) convertView.findViewById(R.id.thread_view_comment_commentBy);
        replyBy.setText(reply.getUser() + "#" + reply.getHash() + "  ");
        String username = reply.getUser();
        if(HashHelper.getHash(username).equals(reply.getHash())) {
            replyBy.setBackgroundResource(R.drawable.username_background_rect);
            replyBy.setTextColor(Color.WHITE);
        }
        // Comment timestamp
        TextView replyTime = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentDate);
        replyTime.setText(reply.getCommentDateString());
        }
        // Image
        if (reply.hasImage()) {
            ImageView image = (ImageView) convertView.findViewById(R.id.imageButton1);
            image.setImageBitmap(reply.getImageThumb());
    }
}