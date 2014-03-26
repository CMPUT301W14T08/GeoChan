package ca.ualberta.cmput301w14t08.geochan.activities;

import ca.ualberta.cmput301w14t08.geochan.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

//Adapted from http://stackoverflow.com/questions/7693633/android-image-dialog-popup

public class ImageActivity extends Activity {
    private ImageView imageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_fullscreen);
        
        imageDialog = (ImageView)findViewById(R.id.fullscreenImageView);
        imageDialog.setClickable(true);
    }
    
    public void dismissImage(View v) {
        finish();
    }
    
}
