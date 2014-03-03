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

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ThreadViewFragment extends Fragment {
    private ListView threadView;
    private ThreadViewAdapter adapter; 
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thread_view, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        final int id = (int) bundle.getLong("id");
        Thread thread = ThreadList.getThreads().get(id);
        threadView = (ListView) getView().findViewById(R.id.thread_view_list);
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int sort = pref.getInt("sort", SortComparators.SORT_DATE_NEWEST);
        thread.sortComments(sort);
        adapter = new ThreadViewAdapter(getActivity(), thread);
        // Assign custom adapter to the thread listView.
        threadView.setAdapter(adapter);        
        adapter.notifyDataSetChanged();
    }
}
