package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface PostRunnableMethodsInterface {
    void setPostThread(Thread thread);
    
    void handlePostState(int state);
}