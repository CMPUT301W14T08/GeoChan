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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Responsible for the UI fragment that allows a user to post
 * a reply to a comment.
 */
public class PostCommentFragment extends Fragment {
    Thread thread;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_post_comment, container, false);
    }
    
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        thread = ThreadList.getThreads().get((int) bundle.getLong("id"));
        TextView titleView = (TextView) getActivity().findViewById(R.id.op_title);
        TextView bodyView = (TextView) getActivity().findViewById(R.id.op_body);
        titleView.setText(thread.getTitle());
        bodyView.setText(thread.getBodyComment().getTextPost());
    }

    public void postComment(View v) {
        if(v.getId() == R.id.post_comment_button) {
            EditText editComment = (EditText) this.getView().findViewById(R.id.commentBody);
            String comment = editComment.getText().toString();
            GeoLocation geoLocation = new GeoLocation(this.getActivity());
            if (geoLocation.getLocation() == null) {
                ErrorDialog.show(getActivity(), "Could not obtain location.");
                thread.addComment(new Comment(comment, null));
            } else {
                thread.addComment(new Comment(comment, geoLocation));
            }
            InputMethodManager inputManager = (InputMethodManager)getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE); 
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            this.getFragmentManager().popBackStackImmediate();
        }
    }
}
