package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface EditRunnableInterface {
    void setEditThread(Thread thread);
    
    void handleEditState(int state);
}


