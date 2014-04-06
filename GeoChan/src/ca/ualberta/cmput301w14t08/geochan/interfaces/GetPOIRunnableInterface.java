package ca.ualberta.cmput301w14t08.geochan.interfaces;


public interface GetPOIRunnableInterface {

void setGetPOIThread(Thread thread);

void handleGetPOIState(int state);

void setPOICache(String poi);

String getPOICache();
}
