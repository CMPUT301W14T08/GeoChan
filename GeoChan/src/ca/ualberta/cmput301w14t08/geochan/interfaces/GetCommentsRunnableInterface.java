package ca.ualberta.cmput301w14t08.geochan.interfaces;

public interface GetCommentsRunnableInterface {
    void setGetCommentsThread(Thread thread);

    void handleGetCommentsState(int state);
}
