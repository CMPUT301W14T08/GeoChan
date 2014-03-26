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
 * @author Artem Chikin
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

    @Override
    /**
     * Depending on the list item type, inflate the correct
     * layout.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        final Comment comment = (Comment) getItem(position);
        switch (type) {
        case TYPE_OP:
            convertView = setConvertView(convertView, R.layout.thread_view_op);
            setOPFields(convertView);
            listenForThreadButtons(convertView, thread);
            break;

        case TYPE_COMMENT0:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_0);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT1:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_1);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT2:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_2);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT3:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_3);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT4:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_4);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT5:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_5);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT6:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_6);
            setCommentFields(convertView, comment);
            break;

        case TYPE_COMMENT7:
            convertView = setConvertView(convertView, R.layout.thread_view_comment_7);
            setCommentFields(convertView, comment);
            break;

        case TYPE_SEPARATOR:
            convertView = setConvertView(convertView, R.layout.thread_view_separator);
            TextView numComments = (TextView) convertView.findViewById(R.id.textSeparator);
            numComments.setText(Integer.toString(getCount() - 2) + " Comments:");
            break;

        case TYPE_COMMENTMAX:
            final Comment commentMax = (Comment) getItem(position);
            convertView = setConvertView(convertView, R.layout.thread_view_comment_max);
            setCommentFields(convertView, commentMax);
            // TYPE_COMMENTMAX has an extra field: depth
            TextView depthMeter = (TextView) convertView
                    .findViewById(R.id.thread_view_comment_depth_meter);
            depthMeter.setText("Max depth + " + Integer.toString(commentMax.getDepth() - 7));
            break;

        }
        return convertView;
    }

    /**
     * Assign convertview layout
     * 
     * @param convertView
     *            view to inflate
     * @param layout
     *            an R.Layout resource
     * @return convertView Inflated view
     */
    private View setConvertView(View convertView, int layout) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }
        return convertView;
    }

    /**
     * Listener for the buttons in the TYPE_OP listView item. Comments have
     * listeners in ThreadViewFragment, threadComment buttons have a listener in
     * the adapter for ease of access to the permanently displayed buttons.
     * 
     * @param convertView
     * @param thread
     * 
     */
    private void listenForThreadButtons(View convertView, final ThreadComment thread) {
        // Here handle button presses
        final ImageButton replyButton = (ImageButton) convertView
                .findViewById(R.id.comment_reply_button);

        final ImageButton starButton = (ImageButton) convertView
                .findViewById(R.id.comment_star_button);
        
        if (FavouritesLog.getInstance(context).hasThreadComment(thread.getId())) {
            starButton.setImageResource(R.drawable.ic_rating_marked);
        }

        final ImageButton mapButton = (ImageButton) convertView
                .findViewById(R.id.thread_map_button);

        final ImageButton editButton = (ImageButton) convertView
                .findViewById(R.id.thread_edit_button);

        if (HashHelper.getHash(thread.getBodyComment().getUser()).equals(thread.getBodyComment().getHash())) {
            editButton.setVisibility(View.VISIBLE);
        }

        if (starButton != null) {
            starButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!FavouritesLog.getInstance(context).hasThreadComment(thread.getId())) {
                        Toast.makeText(context, "Thread saved to Favourites.", Toast.LENGTH_SHORT)
                        .show();
                        starButton.setImageResource(R.drawable.ic_rating_marked);
                        FavouritesLog log = FavouritesLog.getInstance(context);
                        log.addThreadComment(thread);
                    } else {
                        Toast.makeText(context, "Thread removed from Favourites.",
                                Toast.LENGTH_SHORT).show();
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
    }

    /**
     * Method to set the required fields of the orignal post of the thread.
     * Title, creator, comment, timestamp, location.
     * 
     * @param convertView
     *            View container of a listView item
     */
    private void setOPFields(View convertView) {
        // Thread title
        TextView title = (TextView) convertView.findViewById(R.id.thread_view_op_threadTitle);
        title.setText(thread.getTitle());
        // Thread creator
        TextView threadBy = (TextView) convertView.findViewById(R.id.thread_view_op_commentBy);
        threadBy.setText("Posted by " + thread.getBodyComment().getUser() + "#"
                + thread.getBodyComment().getHash()+ "  ");
        if (HashHelper.getHash(thread.getBodyComment().getUser()).equals(thread.getBodyComment().getHash())) {
            threadBy.setBackgroundResource(R.drawable.username_background_thread_rect);
            threadBy.setTextColor(Color.WHITE);
        }

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
            // The rounding of long and lat for max 4 decimal digits.
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
     * This method sets all the required views for a comment reply. Comment,
     * creator, time
     * 
     * @param convertView
     * @param reply
     * 
     */
    private void setCommentFields(View convertView, Comment reply) {
        if(reply.hasImage()) {
            ImageButton thumbnail = (ImageButton) convertView.findViewById(R.id.thread_view_comment_thumbnail);
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setFocusable(false);
            thumbnail.setImageBitmap(reply.getImageThumb());
        }
        // Comment body
        TextView replyBody = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentBody);
        replyBody.setText(reply.getTextPost());
        // Comment creator
        TextView replyBy = (TextView) convertView.findViewById(R.id.thread_view_comment_commentBy);
        replyBy.setText(reply.getUser() + "#" + reply.getHash() + "  ");
        String username = reply.getUser();
        if (HashHelper.getHash(username).equals(reply.getHash())) {
            replyBy.setBackgroundResource(R.drawable.username_background_rect);
            replyBy.setTextColor(Color.WHITE);
        }
        // Comment timestamp
        TextView replyTime = (TextView) convertView
                .findViewById(R.id.thread_view_comment_commentDate);
        replyTime.setText(reply.getCommentDateString());
    }
}