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
import android.widget.TextView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * Adapter to display a list of locations the user posted from in
 * the past. Each list item consists of a point of interest that
 * was obtain for the location on post.
 * 
 * @author Artem Chikin
 */
public class CustomLocationAdapter extends BaseAdapter {
    private ArrayList<GeoLocation> logArray;
    private Context context;

    /** 
     * Constructs the adapter and initializes its context and list of log entries.
     * @param context the Context
     * @param logEntries the ArrayList of GeoLocation entries
     */
    public CustomLocationAdapter(Context context, ArrayList<GeoLocation> logEntries) {
        this.context = context;
        this.logArray = logEntries;
    }

    /**
     * Returns the number of entries in the log array.
     * @return the number of entries
     */
    @Override
    public int getCount() {
        return logArray.size();
    }

    /**
     * Returns a specific log entry.
     * @param position  the position of the entry
     * @return  the log entry
     */
    @Override
    public GeoLocation getItem(int position) {
        return logArray.get(position);
    }

    /**
     * Returns the id of a specific entry.
     * @param position  the position of the entry
     * @return the id
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Returns the view for a specific entry.
     * @param position The position.
     * @param convertView A previous recycled view.
     * @param parent The parent View.
     * @return The View.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	GeoLocation logEntry = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.log_entry_layout, null);
        }

        TextView poi = (TextView) convertView.findViewById(R.id.log_entry_poi);
        poi.setText(logEntry.getLocationDescription());
        return convertView;
    }

}
