package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.fragments.PostCommentFragment;

public class ImageTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mainActivity;
    PostCommentFragment postFrag;
    
    
    public ImageTest() {
        super(MainActivity.class);
    }
    
    protected void setUp() throws Exception {
        this.mainActivity = getActivity();
    }
    
}
