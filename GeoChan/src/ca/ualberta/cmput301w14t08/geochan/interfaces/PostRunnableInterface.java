package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface PostRunnableInterface {
    void setPostThread(Thread thread);
    
    void handlePostState(int state);
}