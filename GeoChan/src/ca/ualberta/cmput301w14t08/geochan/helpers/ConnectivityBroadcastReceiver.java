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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import ca.ualberta.cmput301w14t08.geochan.managers.CacheManager;

/**
 * Receives a broadcast anytime the connection state changes.
 * Should only be enabled manually when connection is detected to be off
 * as it only handles the case of the connection coming back,
 * in order to save battery by not constantly monitoring the connection state.
 * 
 * @author Artem Herasymchuk
 *
 */
public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
	
	public static final String UPDATE_FROM_SERVER_INTENT = "ca.ualberta.cmput301w14t08.geochan.updatefromserverintent";
	
    /**
     * Called when BroacastReceiver receives an intent broadcast.
     * 
     * @param context The Context in which the ConnectivityBroadcastReceiver is running.
     * @param intent The received broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityHelper helper = ConnectivityHelper.getInstance();
        if (helper.isConnected()) {
        	ComponentName reciever = new ComponentName(context, ConnectivityBroadcastReceiver.class);
        	
        	PackageManager packageManager = context.getPackageManager();
        	packageManager.setComponentEnabledSetting(reciever, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        	
        	CacheManager cacheManager = CacheManager.getInstance();
        	cacheManager.postAll();
        	
        	Intent i = new Intent();
        	i.setAction(UPDATE_FROM_SERVER_INTENT);
        	context.sendBroadcast(i);
        } else {
            // Internet connection lost
        	// This listener is registered manually when we lose connection, and disabled
        	// as soon as a connection is re-established. Therefore, there is no need
        	// to implement anything for detecting a connection loss as we never use it for this.
        }
    }
}
