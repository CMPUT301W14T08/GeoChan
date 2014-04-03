package ca.ualberta.cmput301w14t08.geochan.interfaces;


public interface GetImageRunnableInterface {
    void setGetImageThread(Thread thread);
    
    void handleGetImageState(int state);
    
    void setImageCache(Byte[] cache);
    
    Byte[] getImageCache();
}


