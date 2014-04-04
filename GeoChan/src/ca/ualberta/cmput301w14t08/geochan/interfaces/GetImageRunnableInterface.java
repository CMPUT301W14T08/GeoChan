package ca.ualberta.cmput301w14t08.geochan.interfaces;

import android.graphics.Bitmap;

public interface GetImageRunnableInterface {
    void setGetImageThread(Thread thread);

    void handleGetImageState(int state);

    void setImageCache(Bitmap cache);

    Bitmap getImageCache();
}
