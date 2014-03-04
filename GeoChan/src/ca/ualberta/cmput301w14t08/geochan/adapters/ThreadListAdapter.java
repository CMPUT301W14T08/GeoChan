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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.R.id;
import ca.ualberta.cmput301w14t08.geochan.R.layout;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.Thread;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This is a custom adapter, used to display Thread objects in a list
 */
public class ThreadListAdapter extends BaseAdapter {

    private Context context;
    private static ArrayList<Thread> displayList;

    public ThreadListAdapter(Context context, ArrayList<Thread> list) {
        this.context = context;
        ThreadListAdapter.displayList = list;
    }

    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public Thread getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Thread thread = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.thread_list_item_no_image, null);
        }
        // Thread title
        TextView title = (TextView) convertView.findViewById(R.id.threadTitle);
        title.setText(thread.getTitle());
        // Thread bodyComment snippet 
        TextView body = (TextView) convertView.findViewById(R.id.commentBody);
        body.setText(thread.getBodyComment().getTextPost());
        // Thread timestamp 
        TextView time = (TextView) convertView.findViewById(R.id.commentDate);
        time.setText(makeCommentTimeString(thread.getBodyComment()));
        // Thread user creator
        TextView user = (TextView) convertView.findViewById(R.id.commentBy);
        user.setText("posted by " + thread.getBodyComment().getUser());
        // Location text
        TextView location = (TextView) convertView.findViewById(R.id.locationText);
        GeoLocation loc = thread.getBodyComment().getLocation();
        if (loc != null) {
            double roundedLat = Math.round(loc.getLatitude() * 100)/100;
            double roundedLong = Math.round(loc.getLongitude() * 100)/100;
            location.setText("Latitude: " + Double.toString(roundedLat) +
                    " Longitude: " + Double.toString(roundedLong));
        } else {
            location.setText("Error: No location found");
        }
        return convertView;      
    }
    
    public String makeCommentTimeString(Comment comment) {
        Date date = comment.getCommentDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        String ret = " | on " 
                + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CANADA) 
                + "." + cal.get(Calendar.DATE)
                + "," + cal.get(Calendar.YEAR) 
                + " at " + cal.get(Calendar.HOUR_OF_DAY)
                + ":" + cal.get(Calendar.MINUTE);
        return ret;
    }
    
}
