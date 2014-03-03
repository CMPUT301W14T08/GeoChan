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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Responsible for the UI fragment that allows a user to post
 * a new thread.
 */
public class PostThreadFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_post_thread, container, false);
    }


    public void postNewThread(View v) {
        if(v.getId() == R.id.post_thread_button) {
            EditText editTitle = (EditText) this.getView().findViewById(R.id.titlePrompt);
            EditText editComment = (EditText) this.getView().findViewById(R.id.commentBody);
            String title = editTitle.getText().toString();
            String comment = editComment.getText().toString();
            if(title.equals("")) {
                ErrorDialog.show(getActivity(), "Title can not be left blank.");
            } else {
                GeoLocation geoLocation = new GeoLocation(this.getActivity());
                if (geoLocation.getLocation() == null) {
                    ErrorDialog.show(getActivity(), "Could not obtain location.");
                    ThreadList.addThread(new Comment(comment, null), title);
                } else {
                    ThreadList.addThread(new Comment(comment,geoLocation),title);
                }
                InputMethodManager inputManager = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE); 
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                this.getFragmentManager().popBackStackImmediate();
            }
        }
    }
}
