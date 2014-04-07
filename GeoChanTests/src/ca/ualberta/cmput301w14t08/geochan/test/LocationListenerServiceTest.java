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

import android.location.Location;
import android.location.LocationManager;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;

/**
 * Junit Test class for the LocationListenerService class
 * 
 * NOTE: When testing in the emulator, you must inject a mock location via DDMS
 * in eclipse
 * 
 * @author bradsimons
 */
public class LocationListenerServiceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public LocationListenerServiceTest() {
        super(MainActivity.class);
    }

    /**
     * Tests the construction of these objects. Makes sure the object is not
     * null, and that is does not have a null location
     */
    public void testConstruction() {
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        assertNotNull(locationListenerService);
        assertNotNull(locationListenerService.getCurrentLocation());
    }

    /**
     * Tests the helper method for same providers.
     */
    public void testSameProvider() {
        // creates a locationListener object, and two provider objects, one for
        // GPS and one for NETWORK
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        String provider1 = LocationManager.GPS_PROVIDER;
        String provider2 = LocationManager.NETWORK_PROVIDER;

        // assert all 4 scenarios to check if the same provider
        assertTrue(locationListenerService.isSameProvider(provider1, provider1));
        assertTrue(locationListenerService.isSameProvider(provider2, provider2));
        assertFalse(locationListenerService.isSameProvider(provider1, provider2));
        assertFalse(locationListenerService.isSameProvider(provider2, provider1));
    }

    /**
     * Tests for a better provider after two minutes. The isBetterLocation
     * method should return true if a new location is passed to the listener two
     * minutes after it's current location
     */
    public void testBetterLocationAfterTwoMinutes() {
        // create a listener and two location objects
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);

        // set the lat and long of each location object
        location1.setLongitude(0.0);
        location1.setLatitude(0.0);
        location2.setLongitude(1.0);
        location2.setLatitude(1.0);

        // add over just two minutes in milliseconds to one location's time
        location2.setTime(location2.getTime() + 120001);

        // assert better location or not
        assertTrue(locationListenerService.isBetterLocation(location2, location1));
        assertFalse(locationListenerService.isBetterLocation(location1, location2));
    }

    /**
     * Test for better accuracy between two locations. When two objects are
     * created within two minutes of each, the isBetterLocation() method looks
     * at their accuracy rather than time.
     */
    public void testBetterAccuracy() {
        // create a listener object and two location objects
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        Location location1 = new Location(LocationManager.GPS_PROVIDER);
        Location location2 = new Location(LocationManager.GPS_PROVIDER);

        // modify the accuracy of each object
        location1.setAccuracy(1);
        location2.setAccuracy(2);

        // will use the isBetterLocation method to check accuracy
        assertTrue(locationListenerService.isBetterLocation(location1, location2));
        assertFalse(locationListenerService.isBetterLocation(location2, location1));
    }

    /**
     * Test the return of a location
     */
    public void testRetrieveLocation() {
        // create a listener and assert it returns a location
        LocationListenerService locationListenerService = new LocationListenerService(getActivity());
        assertNotNull(locationListenerService.getCurrentLocation());
    }
}
