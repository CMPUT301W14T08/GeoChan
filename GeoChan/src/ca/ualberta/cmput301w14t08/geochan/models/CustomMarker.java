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

package ca.ualberta.cmput301w14t08.geochan.models;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;

/**
 * Custom Marker class designed to construct map Marker objects and handle their
 * operations.
 * 
 * @author Brad Simons
 * 
 */
public class CustomMarker extends Marker {

	private GeoLocation geoLocation;
	private MapView mapView;
	private Drawable icon;

	/**
	 * Constructor for initializing just the marker and setting its position
	 * 
	 * @param mapView The MapView the marker is being placed on.
	 * @param geoLocation The GeoLocation the marker is being placed at.
	 */
	public CustomMarker(GeoLocation geoLocation, MapView mapView) {
		super(mapView);
		super.setPosition(geoLocation.makeGeoPoint());
		this.mapView = mapView;
		this.geoLocation = geoLocation;
	}

	/**
	 * Constructor for initializing the marker, setting its position and icon
	 * 
	 * @param mapView The Mapview the marker is being placed on.
	 * @param geoLocation The GeoLocation the marker is being placed at.
	 * @param icon The Drawable icon for the marker.
	 */
	public CustomMarker(GeoLocation geoLocation, MapView mapView, Drawable icon) {
		super(mapView);
		super.setPosition(geoLocation.makeGeoPoint());
		super.setIcon(icon);
		this.mapView = mapView;
		this.geoLocation = geoLocation;
		this.icon = icon;
	}

	/**
	 * Constructor for initializing the marker, setting its position and icon
	 * 
	 * @param mapView
	 *            which the marker will be displayed in
	 * @param geoLocation
	 *            where the marker is located
	 * @param icon
	 *            image of the marker
	 */
	public CustomMarker(GeoPoint geoPoint, MapView mapView, Drawable icon) {
		super(mapView);
		super.setPosition(geoPoint);
		super.setIcon(icon);
		this.mapView = mapView;
		this.geoLocation = new GeoLocation(geoPoint.getLatitude(),
				geoPoint.getLongitude());
		this.icon = icon;
	}

	/**
	 * Sets up the infoWindow bubble for the Marker. Sets a title and icon image
	 * 
	 * @param icon
	 *            of the marker
	 * @param title
	 *            string for the info window
	 * @param activity
	 *            which this object resides in
     */
	public void setUpInfoWindow(String title, Activity activity) {
		this.createInfoWindow();
		
		super.setTitle(title);
		super.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}

	/**
	 * Retrieves the POI string for the marker
	 * 
	 * @param activity
	 *            which this object resides in
	 */
	public void getPOIString(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}

	/**
	 * Creates a new infoWindow bubble for the Marker object
	 */
	public void createInfoWindow() {
		MarkerInfoWindow infoWindow = new MarkerInfoWindow(
				R.layout.bonuspack_bubble, mapView);
		this.setInfoWindow(infoWindow);
	}

	/**
	 * Constructs a geoPoint from the geoLocation and returns it
	 * 
	 * @return GeoPoint  GeoPoint corresponding to the CustomMarker's Geolocation.
	 */
	public GeoPoint getGeoPoint() {
		return geoLocation.makeGeoPoint();
	}

	/* Getters and setters */

	public GeoLocation getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(GeoLocation geoLocation) {
		this.geoLocation = geoLocation;
	}

	public MapView getMapView() {
		return mapView;
	}

	public void setMapView(MapView openMapView) {
		this.mapView = openMapView;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
