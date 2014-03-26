package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class EditCommentFragment extends Fragment {
    private Comment editComment;
    private EditText newTextPost;
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_edit_comment, container, false);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState); 
    }
    
    @Override
    public void onStart(){
        super.onStart();
        Bundle bundle = getArguments();
        String commentId = bundle.getString("commentId");
        int threadIndex = bundle.getInt("threadIndex");
        Log.e("EDIT:","Value of commentId:" + commentId);
        ThreadComment thread = ThreadList.getThreads().get(threadIndex);
        Log.e("EDIT:","Body comment of thread:" + thread.getBodyComment().getTextPost());
        if(thread.getBodyComment().getId() == commentId){
            editComment = thread.getBodyComment();
        } else {
            getCommentFromId(commentId, thread.getBodyComment().getChildren());
        }
        String oldText = editComment.getTextPost();
        TextView oldTextView = (TextView) getActivity().findViewById(R.id.old_comment_text);
        oldTextView.setText(oldText);
        newTextPost = (EditText) getActivity().findViewById(R.id.editBody);
        newTextPost.setText(oldText);
        newTextPost.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    public void onResume(){
        super.onResume();
    }
    
    @Override
    public void onPause(){
        super.onPause();
    }
    
    public void getCommentFromId(String id, ArrayList<Comment> comments){
        for(Comment com: comments){
            Log.e("EDIT","com's id:" + com.getId());
            if(com.getId().equals(id)){
                //Log.e("EDIT","com's id:" + com.getId());
                editComment = com;
                return;
            } else {
                getCommentFromId(id, com.getChildren());
            }
        }
        return;
    }
    
    public void makeEdit(View view){
        editComment.setTextPost(newTextPost.getText().toString());
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        getFragmentManager().popBackStackImmediate();
        
    }
    
    public void changeLocation(View v){
        //Can probably adapt this from PostCommentFragment.
    }

}
