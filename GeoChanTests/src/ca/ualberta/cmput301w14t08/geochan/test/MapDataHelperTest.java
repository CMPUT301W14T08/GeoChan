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

import org.osmdroid.bonuspack.clustering.GridMarkerClusterer;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.MapDataHelper;
import ca.ualberta.cmput301w14t08.geochan.models.CustomMarker;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * J-unit test class for testing the MapDataHelper class
 * 
 * @author Brad Simons
 * 
 */
public class MapDataHelperTest extends ActivityInstrumentationTestCase2<MainActivity> {

    Activity activity;

    /**
     * Constructor
     * 
     */
    public MapDataHelperTest() {
        super(MainActivity.class);
    }

    /**
     * Sets up the test, assigns activity attribute
     * 
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
    }

    /**
     * Checks construction of this object, verifies the correct map object was
     * passed in
     * 
     */
    public void testConstruction() {
        MapView map = new MapView(activity, null);

        MapDataHelper mapHelper = new MapDataHelper(map);
        assertNotNull(mapHelper);
        assertEquals("Map objects should be the same", map, mapHelper.getMap());
    }

    /**
     * Tests the setup of the map, checks to see if the map can zoom in and out
     * 
     */
    public void testSetupMap() {
        MapView map = new MapView(activity, null);
        MapDataHelper mapHelper = new MapDataHelper(map);

        mapHelper.setUpMap();

        assertEquals("Map should be able to zoom in", true, mapHelper.getMap().canZoomIn());
        assertEquals("Map should be able to zoom out", true, mapHelper.getMap().canZoomOut());
    }

    /**
     * Tests adding a marker to the map. The map should center itself around the
     * marker
     * 
     */
    public void testAddMarkerAndCenter() {
        MapView map = new MapView(activity, null);
        MapDataHelper mapHelper = new MapDataHelper(map);
        mapHelper.setUpMap();

        GeoLocation location = new GeoLocation(1.0, 2.0);

        CustomMarker marker = new CustomMarker(location, mapHelper.getMap());

        mapHelper.addMarkerToOverlayAndCenter(marker, 5);

        assertEquals("Overlays should be the same", marker, mapHelper.getOverlays().get(0));
        assertEquals("The latitude of the center of the map should be 1.0", 1.0, mapHelper
                .getMapCenter().getLatitude());
        assertEquals("The longitude of the center of the map should be 2.0", 2.0, mapHelper
                .getMapCenter().getLongitude());
    }

    /**
     * Tests the adding of an array of clusterers to the map overlay
     * 
     */
    public void testAddClustererToOverlay() {
        MapView map = new MapView(activity, null);
        MapDataHelper mapHelper = new MapDataHelper(map);
        mapHelper.setUpMap();

        GeoLocation location = new GeoLocation(1.0, 2.0);
        CustomMarker marker = new CustomMarker(location, mapHelper.getMap());

        ArrayList<GridMarkerClusterer> clusterers = new ArrayList<GridMarkerClusterer>();
        GridMarkerClusterer clusterer = new GridMarkerClusterer(activity);
        clusterer.add(marker);
        clusterers.add(clusterer);

        mapHelper.addClustererMarkersToOverlay(clusterers);

        assertEquals("Clusterer objects should be the same", clusterer, mapHelper.getOverlays()
                .get(0));
    }
    
    /**
     * Tests the adding of an overlay to the map overlays array
     * 
     */
    public void testAddOverlay() {
        MapView map = new MapView(activity, null);
        MapDataHelper mapHelper = new MapDataHelper(map);
        mapHelper.setUpMap();

        GeoLocation location = new GeoLocation(1.0, 2.0);
        CustomMarker marker = new CustomMarker(location, mapHelper.getMap());
        
        mapHelper.addToOverlays(marker);

        assertEquals("Marker objects should be the same", marker, mapHelper.getOverlays()
                .get(0));
    }
    
    /**
     * Tests the adding overlays to map overlays array, and then clearing that array
     * 
     */
    public void testClearOverlays() {
        MapView map = new MapView(activity, null);
        MapDataHelper mapHelper = new MapDataHelper(map);
        mapHelper.setUpMap();

        GeoLocation location = new GeoLocation(1.0, 2.0);
        CustomMarker marker = new CustomMarker(location, mapHelper.getMap());
        CustomMarker marker2 = new CustomMarker(location, mapHelper.getMap());
        
        mapHelper.addToOverlays(marker);
        mapHelper.addToOverlays(marker2);

        assertEquals("Overlays array should have size 2", 2, mapHelper.getOverlays().size());
        
        ArrayList<GridMarkerClusterer> clusterers = new ArrayList<GridMarkerClusterer>();
        GridMarkerClusterer clusterer = new GridMarkerClusterer(activity);
        clusterers.add(clusterer);

        mapHelper.addClustererMarkersToOverlay(clusterers);
        
        assertEquals("Overlays array should have size 3", 3, mapHelper.getOverlays().size());
        
        mapHelper.clearOverlays();
        
        assertEquals("Overlays array should have size 0", 0, mapHelper.getOverlays().size());
    }
}
