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

package ca.ualberta.cmput301w14t08.geochan;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This is a custom adapter, used to display Thread objects in a list in the
 * ThreadListActivity
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

        TextView title = (TextView) convertView.findViewById(R.id.threadTitle);
        title.setText(thread.getTitle());

        TextView body = (TextView) convertView.findViewById(R.id.commentBody);
        body.setText(thread.getTopComment().getTextPost());

        return convertView;
    }
}
