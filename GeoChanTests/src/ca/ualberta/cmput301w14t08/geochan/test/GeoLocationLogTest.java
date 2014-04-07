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
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;

/**
 * Test the functionality of the GeoLocationLog methods
 */
public class GeoLocationLogTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private LocationListenerService locationListenerService;
    
    public GeoLocationLogTest() {
        super(MainActivity.class);
    }
    
    /**
     * Verifies the constructor constructs object
     */
    public void testConstruction() {
        GeoLocationLog geoLocationLog =  GeoLocationLog.generateInstance(getActivity());
        assertNotNull(geoLocationLog.getLogEntries());
    }
    
    /**
     * Tests adding a log entry by adding several log entries, then
     * iterating over the log and verifying the entries have been added
     * correctly.
     */
    public void testAddLogEntry() {
        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        GeoLocation geoLocation = new GeoLocation(locationListenerService);
        
        GeoLocationLog geoLocationLog = GeoLocationLog.generateInstance(getActivity());
        
        geoLocationLog.addLogEntry(geoLocation);
        geoLocationLog.addLogEntry(geoLocation);
        geoLocationLog.addLogEntry(geoLocation);
        
        for (GeoLocation entry : geoLocationLog.getLogEntries()) {
            assertTrue("geoLocations should be equal", geoLocation.equals(entry));
        }
    }
    
    /**
     * Tests the clearLog method by populating the log,
     * calling the method and verifying the log is empty
     */
    public void testClearLogAndCheckIsLogEmpty() {
        GeoLocationLog geoLocationLog = GeoLocationLog.generateInstance(getActivity());
        
        geoLocationLog.addLogEntry(new GeoLocation(1.0,2.0));
        assertEquals("Entries array should NOT be empty", false, geoLocationLog.isEmpty());
        
        geoLocationLog.clearLog();
        assertEquals("Entries array should be empty",true, geoLocationLog.isEmpty());
    }
    
    /**
     * Test the corectness of the size method by populating the log with
     * objects and verifying the method returns the correct quantity
     */
    public void testSizeOfLog() {        
        locationListenerService = new LocationListenerService(getActivity());
        GeoLocation geoLocation1 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation2 = new GeoLocation(locationListenerService);
        GeoLocation geoLocation3 = new GeoLocation(locationListenerService);
        
        GeoLocationLog geoLocationLog = GeoLocationLog.generateInstance(getActivity());
        
        geoLocationLog.addLogEntry(geoLocation1);
        geoLocationLog.addLogEntry(geoLocation2);
        geoLocationLog.addLogEntry(geoLocation3);
        
        assertEquals("Size of entries should be 3", 3, geoLocationLog.size());
    }
}
