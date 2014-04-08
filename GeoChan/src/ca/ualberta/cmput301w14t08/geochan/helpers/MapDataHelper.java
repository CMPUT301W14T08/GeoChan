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

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.GridMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import ca.ualberta.cmput301w14t08.geochan.models.CustomMarker;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * This class holds all map data and encapsulates some common procedures
 * required to perform map operations
 * 
 * @author Brad Simons
 * 
 */
public class MapDataHelper {

	private MapView map;
	private GeoLocation mapCenter;

	/**
	 * Simple constructor that sets the map attribute to the map passed in
	 * 
	 * @param mapView The MapView to be set.
	 */
	public MapDataHelper(MapView mapView) {
		this.map = mapView;
	}

	/**
	 * Sets up the map parameters
	 */
	public void setUpMap() {
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);
		map.getController().setZoom(5);
	}

	/**
	 * Adds a marker to the map, and zooms to that point with a passed in zoom
	 * level argument.
	 * 
	 * @param Marker
	 *             The Marker to be put on the map.
	 * @param int Zoom level for the map.
	 */
	public void addMarkerToOverlayAndCenter(CustomMarker marker, int zoomLevel) {
		map.getOverlays().add(marker);
		map.getController().setZoom(zoomLevel);
		map.getController().animateTo(marker.getPosition());
		this.mapCenter = marker.getGeoLocation();
	}

	/**
	 * Adds an array of clusterer objects to the map overlay list
	 * 
	 * @param clusterers
	 *            The GridMarkerClusterers to be added to the map overlay list.
	 */
	public void addClustererMarkersToOverlay(
			ArrayList<GridMarkerClusterer> clusterers) {
		for (GridMarkerClusterer clusterer : clusterers) {
			map.getOverlays().add(clusterer);
		}
	}

	/**
	 * Sets the zoom level for the map view.
	 * 
	 * @param zoomLevel
	 *            The zoom level to be set.
	 */
	public void setZoom(int zoomLevel) {
		map.getController().setZoom(zoomLevel);
	}

	/**
	 * Sets the center of the map to the position of the GeoPoint passed in
	 * 
	 * @param geoPoint The GeoPoint for the map to be centered around.
	 */
	public void setCenter(GeoPoint geoPoint) {
		map.getController().setCenter(geoPoint);
	}

	/**
	 * Clears the maps overlay list.
	 */
	public void clearOverlays() {
		map.getOverlays().clear();
	}

	/**
	 * Adds an Overlay object to the maps overlay list.
	 * 
	 * @param Overlay
	 *            The Overlay to be added to the map overlay list.
	 */
	public void addToOverlays(Overlay overlay) {
		map.getOverlays().add(overlay);
	}

	/**
	 * Refreshes the map view.
	 */
	public void refreshMap() {
		map.invalidate();
	}

	/* Getters and Setters */

	public MapView getMap() {
		return map;
	}

	public void setMap(MapView map) {
		this.map = map;
	}

	public List<Overlay> getOverlays() {
		return map.getOverlays();
	}

	public IMapController getController() {
		return map.getController();
	}
	

	public GeoLocation getMapCenter() {
		return mapCenter;
	}

	public void setMapCenter(GeoLocation mapCenter) {
		this.mapCenter = mapCenter;
	}
}
