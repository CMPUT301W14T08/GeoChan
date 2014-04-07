package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Provides status about the user's current Internet connection.
 * 
 * @author Artem Herasymchuk, Brad Simons
 * 
 */
public class ConnectivityHelper {

    private static ConnectivityHelper instance = null;
    private static ConnectivityManager connectivityManager;
    private static ComponentName reciever;
    private static PackageManager packageManager;

    /**
     * Constructor, sets up the connectivity manager. Private to avoid usage
     * outside of as a singleton.
     * 
     * @param context
     */
    private ConnectivityHelper(Context context) {
        connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        reciever = new ComponentName(context, ConnectivityBroadcastReceiver.class);
        packageManager = context.getPackageManager();
    }

    /**
     * Generates a singleton instance of this helper.
     * 
     * @param context
     *            the context
     */
    public static void generateInstance(Context context) {
        instance = new ConnectivityHelper(context);
    }

    /**
     * Returns the singleton instance of this helper
     * 
     * @return the instance
     */
    public static ConnectivityHelper getInstance() {
        return instance;
    }

    /**
     * Returns true if device is connected to internet
     * 
     * @return the connection status
     */
    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        if (isConnected == false) {
        	packageManager.setComponentEnabledSetting(reciever, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
        return isConnected;
    }

    /**
     * Returns true if device is connected via WiFi
     * 
     * @return the connection status
     */
    public boolean isWifi() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI));
    }

    /**
     * Returns true if device is connected via Mobile
     * 
     * @return the connection status
     */
    public boolean isMobile() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }

}
