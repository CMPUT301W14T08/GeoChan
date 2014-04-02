package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface UpdateRunnableInterface {
    void setUpdateThread(Thread thread);

    void handleUpdateState(int state);
}
