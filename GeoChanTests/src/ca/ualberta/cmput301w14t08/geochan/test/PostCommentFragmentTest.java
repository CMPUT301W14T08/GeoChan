package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.PostCommentFragment;
import ca.ualberta.cmput301w14t08.geochan.MainActivity;

public class PostCommentFragmentTest extends ActivityInstrumentationTestCase2<MainActivity> {
    PostCommentFragment fragment;
    MainActivity activity;

    public PostCommentFragmentTest() {
        super(MainActivity.class);
    }

 /*  @Override
    public void setUp() {
        super.setUp();
        activity = (MainActivity) getActivity();
        fragment = activity.findViewById(ca.ualberta.cmput301w14t08.geochan.R.id.fragment_post_comment);
    }*/
    
    public void testPreconditions() {
        assertNotNull(activity);
        assertNotNull(fragment);
    }
}
