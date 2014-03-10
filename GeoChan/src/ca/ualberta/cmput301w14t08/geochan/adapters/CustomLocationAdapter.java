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
import ca.ualberta.cmput301w14t08.geochan.helpers.LogEntry;

public class CustomLocationAdapter extends BaseAdapter {
	private ArrayList<LogEntry> logArray;
	private Context context;

	public CustomLocationAdapter(Context context, ArrayList<LogEntry> logEntries) {
		this.logArray = logEntries;
		this.context = context;
	}

	@Override
	public int getCount() {
		return logArray.size();
	}

	@Override
	public LogEntry getItem(int arg0) {
		return logArray.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogEntry logEntry = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.log_entry_layout, null);
		}

		TextView title = (TextView) convertView
				.findViewById(R.id.log_entry_title);
		title.setText(logEntry.getThreadTitle());

		TextView loc = (TextView) convertView
				.findViewById(R.id.log_entry_location);
		String lat = Double.toString(logEntry.getGeoLocation().getLatitude());
		String lon = Double.toString(logEntry.getGeoLocation().getLongitude());
		loc.setText("Lattitiude: " + lat + ", Longtitude: " + lon);

		return convertView;
	}

}
