package ca.ualberta.cmput301w14t08.geochan.helpers;

import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class ConnectivityListenerService extends BroadcastReceiver {

    /**
     * called when BroacastReceiver receives an intent broadcast
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityHelper helper = ConnectivityHelper.getInstance();
        if (helper.isConnected()) {
        	ComponentName reciever = new ComponentName(context, ConnectivityListenerService.class);
        	
        	PackageManager packageManager = context.getPackageManager();
        	packageManager.setComponentEnabledSetting(reciever, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        	
        	CacheManager cacheManager = CacheManager.getInstance();
        	cacheManager.postAll();
        } else {
            // Internet connection lost
        	// This listener is registered manually when we lose connection, and disabled
        	// as soon as a connection is re-established. Therefore, there is no need
        	// to implement anything for detecting a connection loss as we never use it for this.
        }
    }
}
