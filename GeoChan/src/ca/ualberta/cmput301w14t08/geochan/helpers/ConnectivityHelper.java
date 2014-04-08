/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Provides status about the user's current Internet connection.
 * 
 * @author Artem Herasymchuk
 * @author Brad Simons
 * 
 */
public class ConnectivityHelper {

    private static ConnectivityHelper instance = null;
    private static ConnectivityManager connectivityManager;
    private static ComponentName reciever;
    private static PackageManager packageManager;
    private static boolean wasNotConnected = false;

    /**
     * Constructor, sets up the connectivity manager. Private to avoid usage
     * outside of as a singleton.
     * 
     * @param context The Context in which the ConnectivityHelper is running.
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
     *            The Context in which the ConnectivityHelper is running.
     */
    public static void generateInstance(Context context) {
        instance = new ConnectivityHelper(context);
    }

    /**
     * Returns the singleton instance of this helper
     * 
     * @return The instance of ConnectivityHelper.
     */
    public static ConnectivityHelper getInstance() {
        return instance;
    }

    /**
     * Returns true if device is connected to internet
     * 
     * @return The connection status.
     */
    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        if (isConnected == false) {
        	packageManager.setComponentEnabledSetting(reciever, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        	wasNotConnected = true;
        }
        return isConnected;
    }

    /**
     * Returns true if device is connected via WiFi
     * 
     * @return The Wifi status.
     */
    public boolean isWifi() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI));
    }

    /**
     * Returns true if device is connected via Mobile
     * 
     * @return The mobile status.
     */
    public boolean isMobile() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return (isConnected() && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }
    
    /* Getters and setters below */
    
    public void setWasNotConnected(boolean _wasNotConnected) {
    	wasNotConnected = _wasNotConnected;
    }
    
    public boolean getWasNotConnected() {
    	return wasNotConnected;
    }

}
