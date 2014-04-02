package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface EditImageRunnableInterface {
    void setEditImageThread(Thread thread);
    
    void handleEditImageState(int state);
}
