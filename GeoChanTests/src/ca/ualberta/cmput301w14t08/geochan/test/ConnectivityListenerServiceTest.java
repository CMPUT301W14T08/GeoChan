package ca.ualberta.cmput301w14t08.geochan.test;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.ConnectivityListenerService;

public class ConnectivityListenerServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public ConnectivityListenerServiceTest() {
        super(MainActivity.class);
    }
    
    public void testConstruction() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService(getActivity().getApplicationContext());
        assertNotNull(connectivityListenerService);
        assertNotNull(connectivityListenerService.getConnectivityManager());
    }
    
    public void testIsConnectedToWifi() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService(getActivity().getApplicationContext());
        assertEquals("Wifi should be enabled", true, connectivityListenerService.isWifi());        
    }
    
    public void testIsConnectedToMobile() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService(getActivity().getApplicationContext());
        assertEquals("Mobile should be enabled", true, connectivityListenerService.isMobile());   
    }
    
    public void testIsConnected() {
        ConnectivityListenerService connectivityListenerService = 
                new ConnectivityListenerService(getActivity().getApplicationContext());
        assertEquals("Connection should be active", true, connectivityListenerService.isConnected());
    }
}
