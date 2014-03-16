package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityListenerService extends BroadcastReceiver {

    private ConnectivityManager connectivityManager;

    /**
     * Constructor. Gets the system service Connectivity Service
     * @param context
     */
    public ConnectivityListenerService(Context context) {
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO -> Implement :)
    }
    
    /**
     * Returns true if device is connected to internet
     * @return isConnected
     */
    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * Returns true if device is connected via WiFi
     * @return isConnectedToWifi
     */
    public boolean isWifi() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToWifi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        return isConnectedToWifi;
    }

    /**
     * Returns true if device is connected via Mobile
     * @return isConnectedToMobile
     */
    public boolean isMobile() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnectedToMobile = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        return isConnectedToMobile;
    }

    public ConnectivityManager getConnectivityManager() {
        return connectivityManager;
    }

    public void setConnectivityManager(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }
}
