package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface ImageRunnableInterface {
    void setImageThread(Thread thread);
    
    void handleImageState(int state);
}
