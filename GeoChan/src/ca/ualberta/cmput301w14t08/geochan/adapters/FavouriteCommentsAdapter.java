package ca.ualberta.cmput301w14t08.geochan.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.fragments.ExpandImageFragment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * COMMENT HERE
 * 
 * @author Artem Chikin
 * 
 */
public class FavouriteCommentsAdapter extends BaseAdapter {
    private ArrayList<ThreadComment> list;
    private Context context;
    private FragmentManager manager;

    public FavouriteCommentsAdapter(ArrayList<ThreadComment> list, Context context,
            FragmentManager manager) {
        this.list = list;
        this.context = context;
        this.manager = manager;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ThreadComment getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    /**
     * Return a location log item view and set fields.
     * 
     */
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

        if (comment.getBodyComment().hasImage()) {
            ImageButton thumbnail = (ImageButton) convertView
                    .findViewById(R.id.thread_view_comment_thumbnail);
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setFocusable(false);
            thumbnail.setImageBitmap(comment.getBodyComment().getImageThumb());
            listenForCommentThumbnail(convertView, comment);
        }
        return convertView;
    }

    private void listenForCommentThumbnail(View convertView, final ThreadComment comment) {
        ImageButton thumbnail = (ImageButton) convertView
                .findViewById(R.id.thread_view_comment_thumbnail);
        if (thumbnail != null) {
            thumbnail.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    Fragment fragment = new ExpandImageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", comment.getId());
                    manager.beginTransaction().add(R.id.fragment_container, fragment, "thumbFrag")
                            .addToBackStack(null).commit();
                    manager.executePendingTransactions();
                }
            });
        }
    }

}
