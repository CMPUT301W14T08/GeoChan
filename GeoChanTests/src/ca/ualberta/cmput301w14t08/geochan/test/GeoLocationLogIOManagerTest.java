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

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
/**
 * Tests the functionality of the GeoLocation IO manager
 */
public class GeoLocationLogIOManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    
    public GeoLocationLogIOManagerTest(Class<MainActivity> activityClass) {
        super(MainActivity.class);
    }

    private MainActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
    }
    
    /**
     * Test serialization/deserialization by serializing a log of LogEntry 
     * object, deserializing it and verifying that the result matches the
     * log that was serialized.
     */
    public void testSerializeDeserialize() {
        GeoLocationLog log1 = GeoLocationLog.generateInstance(activity.getApplicationContext());
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
        log1.addLogEntry(new GeoLocation(22,22));
     
        ArrayList<GeoLocation> geoList = log1.getLogEntries();
        assertTrue("size should be 5", geoList.size() == 5);
        for(GeoLocation loc : geoList) {
            assertTrue("location should be 22,22", loc.getLatitude() == 22);
            assertTrue("location should be 22,22", loc.getLongitude() == 22);
        }
    }
}
