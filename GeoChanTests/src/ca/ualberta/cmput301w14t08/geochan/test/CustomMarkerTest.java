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

import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.CustomMarker;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * J-unit test class for CustomMarker objects
 * 
 * @author Brad Simons
 * 
 */
public class CustomMarkerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Activity activity;

    /**
     * Constructor
     */
    public CustomMarkerTest() {
        super(MainActivity.class);
    }

    /**
     * sets up the test by getting the activity
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
    }

    /**
     * Tests each type of construction pattern
     */
    public void testConstruction() {
        MapView map = new MapView(activity, null);
        GeoLocation geoLocation = new GeoLocation(1.0, 2.0);
        Drawable icon = activity.getResources().getDrawable(
                ca.ualberta.cmput301w14t08.geochan.R.drawable.blue_map_pin);

        // test constructor 1
        CustomMarker customMarker = new CustomMarker(geoLocation, map);
        assertEquals("Maps should be the same", map, customMarker.getMapView());
        assertEquals("latitudes should be the same", geoLocation.getLatitude(), customMarker
                .getGeoLocation().getLatitude());
        assertEquals("longitudes should be the same", geoLocation.getLongitude(), customMarker
                .getGeoLocation().getLongitude());
        assertEquals("GeoPoint positions should be the same", geoLocation.makeGeoPoint(),
                customMarker.getPosition());

        // test constructor 2
        CustomMarker customMarker2 = new CustomMarker(geoLocation, map, icon);
        assertEquals("Maps should be the same", map, customMarker2.getMapView());
        assertEquals("latitudes should be the same", geoLocation.getLatitude(), customMarker2
                .getGeoLocation().getLatitude());
        assertEquals("longitudes should be the same", geoLocation.getLongitude(), customMarker2
                .getGeoLocation().getLongitude());
        assertEquals("GeoPoint positions should be the same", geoLocation.makeGeoPoint(),
                customMarker2.getPosition());
        assertEquals("Icons should be the same", icon, customMarker2.getIcon());

        // test constructor 3
        CustomMarker customMarker3 = new CustomMarker(geoLocation.makeGeoPoint(), map, icon);
        assertEquals("Maps should be the same", map, customMarker3.getMapView());
        assertEquals("latitudes should be the same", geoLocation.getLatitude(), customMarker3
                .getGeoLocation().getLatitude());
        assertEquals("longitudes should be the same", geoLocation.getLongitude(), customMarker3
                .getGeoLocation().getLongitude());
        assertEquals("GeoPoint positions should be the same", geoLocation.makeGeoPoint(),
                customMarker3.getPosition());
        assertEquals("Icons should be the same", icon, customMarker3.getIcon());
    }

    /**
     * Tests the setup of the info window. The info window of the marker will
     * have a title string and the point of interest string in it after the 
     * setup
     */
    public void testSetupInfoWindow() {
        MapView map = new MapView(activity, null);
        GeoLocation geoLocation = new GeoLocation(1.0, 1.0);

        String title = "testTitle";

        final CustomMarker customMarker1 = new CustomMarker(geoLocation, map);
        customMarker1.setUpInfoWindow(title, activity);

        final CustomMarker customMarker2 = new CustomMarker(geoLocation, map);
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Retrieving Location");
        ThreadManager.startGetPOI(geoLocation, dialog, customMarker2);

        assertEquals("titles should be the same", title, customMarker1.getTitle());

        // check the POI
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                assertEquals("POI should be the same", customMarker1.getSubDescription(),
                        customMarker2.getSubDescription());
                Log.e("poi", customMarker1.getSubDescription());
            }
        };
        handler.postDelayed(runnable, 2000);
    }
    
    /**
     * Tests the getPOIString method. This can be used instead of the setUpInfoWindow 
     * if only the POIString is required.
     */
    public void testGetPOIString() {
        MapView map = new MapView(activity, null);
        GeoLocation geoLocation = new GeoLocation(1.0, 1.0);
        
        final CustomMarker customMarker1 = new CustomMarker(geoLocation, map);
        customMarker1.getPOIString(activity);
        
        final CustomMarker customMarker2 = new CustomMarker(geoLocation, map);
        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Retrieving Location");
        ThreadManager.startGetPOI(geoLocation, dialog, customMarker2);
        
        // check the POI
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                assertEquals("POI should be the same", customMarker1.getSubDescription(),
                        customMarker2.getSubDescription());
                Log.e("poi", customMarker1.getSubDescription());
            }
        };
        handler.postDelayed(runnable, 2000);
    }
}
