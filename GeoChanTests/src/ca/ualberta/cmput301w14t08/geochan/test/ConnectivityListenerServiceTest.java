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

package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;

public class ConnectivityListenerServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public ConnectivityListenerServiceTest() {
        super(MainActivity.class);
    }
    /*
    public void testConstruction() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService();
        assertNotNull(connectivityListenerService);
        assertNotNull(connectivityListenerService.getConnectivityManager());
    }
    
    public void testIsConnectedToWifi() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService();
        assertEquals("Wifi should be enabled", true, connectivityListenerService.isWifi());        
    }
    
    public void testIsConnectedToMobile() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService();
        assertEquals("Mobile should be enabled", true, connectivityListenerService.isMobile());   
    }
    
    public void testIsConnected() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService();
        assertEquals("Connection should be active", true, connectivityListenerService.isConnected());
    }
    
    public void testOnReceive() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService();
        Intent intent = new Intent();
        connectivityListenerService.onReceive(getActivity(), intent);
    }
    */
}
