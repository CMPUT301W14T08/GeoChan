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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

/**
 * This is a custom adapter, used to display ThreadComment objects in a list. It
 * is used inside of our ThreadListFragment.
 * 
 * @author Artem Chikin
 */
public class ThreadListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ThreadComment> displayList;

    public ThreadListAdapter(Context context, ArrayList<ThreadComment> list) {
        this.context = context;
        this.displayList = list;
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public ThreadComment getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(ArrayList<ThreadComment> list) {
        displayList = list;
        notifyDataSetChanged();
    }

    @Override
    /**
     * 
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ThreadComment thread = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.thread_list_item, null);
        }
        setThreadFields(convertView, thread);
        return convertView;
    }

    private void setThreadFields(View convertView, ThreadComment thread) {
        // Thread title
        TextView title = (TextView) convertView.findViewById(R.id.threadTitle);
        title.setText(thread.getTitle());
        // Thread bodyComment snippet
        TextView body = (TextView) convertView.findViewById(R.id.commentBody);
        body.setText(thread.getBodyComment().getTextPost());
        // Thread timestamp
        TextView time = (TextView) convertView.findViewById(R.id.commentDate);
        time.setText(thread.getBodyComment().getCommentDateString());
        // Thread user creator
        TextView user = (TextView) convertView.findViewById(R.id.commentBy);
        user.setText("Posted by " + thread.getBodyComment().getUser() + "#"
                + thread.getBodyComment().getHash());

        if (HashHelper.getHash(thread.getBodyComment().getUser()).equals(
                thread.getBodyComment().getHash())) {
            user.setBackgroundResource(R.drawable.username_background_thread_rect);
            user.setTextColor(Color.WHITE);
            user.setText(" " + user.getText() + "  ");
        } else {
            // Hacky, but required for now, unfortunately due to android
            // recycling list views.
            user.setBackgroundResource(0);
            user.setTextColor(Color.RED);
        }

        // Location text
        TextView location = (TextView) convertView.findViewById(R.id.locationText);
        GeoLocation loc = thread.getBodyComment().getLocation();
        if (loc != null) {
            DecimalFormat format = new DecimalFormat();
            format.setRoundingMode(RoundingMode.HALF_EVEN);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(4);

            location.setText("Latitude: " + format.format(loc.getLatitude()) + " Longitude: "
                    + format.format(loc.getLongitude()));
        } else {
            location.setText("Error: No location found");
        }

        if (thread.getBodyComment().hasImage()) {
            ImageButton thumbnail = (ImageButton) convertView
                    .findViewById(R.id.thread_list_thumbnail);
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setFocusable(false);
            thumbnail.setImageBitmap(thread.getBodyComment().getImageThumb());
        }
    }
}
