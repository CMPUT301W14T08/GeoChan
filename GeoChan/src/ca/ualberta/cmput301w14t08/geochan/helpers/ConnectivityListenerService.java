package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityListenerService extends BroadcastReceiver {

    /**
     * called when BroacastReceiver receives an intent broadcast
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityHelper helper = ConnectivityHelper.getInstance();
        if (helper.isConnected()) {
            // Internet connection is back after not having it
        } else {
            // Internet connection lost
        }
    }
}
