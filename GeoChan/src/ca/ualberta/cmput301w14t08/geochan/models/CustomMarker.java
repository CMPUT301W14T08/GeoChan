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
	private MapView openMapView;

	/**
	 * Constructor for initializing just the marker and setting its position
	 * 
	 * @param mapView
	 * @param geoLocation
	 */
	public CustomMarker(GeoLocation geoLocation, MapView mapView) {
		super(mapView);
		super.setPosition(geoLocation.makeGeoPoint());
		this.openMapView = mapView;
		this.geoLocation = geoLocation;
	}
	
	/**
	 * Constructor for initializing the marker, setting its position and icon
	 * 
	 * @param mapView
	 * @param geoLocation
	 * @param icon
	 */
	public CustomMarker(GeoLocation geoLocation, MapView mapView, Drawable icon) {
		super(mapView);
		super.setPosition(geoLocation.makeGeoPoint());
		super.setIcon(icon);
		this.openMapView = mapView;
		this.geoLocation = geoLocation;
	}

	/**
	 * Constructor for initializing the marker, setting its position and icon
	 * 
	 * @param mapView which the marker will be displayed in
	 * @param geoLocation where the marker is located
	 * @param icon image of the marker
	 */
	public CustomMarker(GeoPoint geoPoint, MapView mapView, Drawable icon) {
		super(mapView);
		super.setPosition(geoPoint);
		super.setIcon(icon);
		this.openMapView = mapView;
		this.geoLocation = new GeoLocation(geoPoint.getLatitude(),
				geoPoint.getLongitude());
	}

	/**
	 * Sets up the infoWindow bubble for the Marker. Sets a title and icon image
	 * 
	 * @param icon of the marker
	 * @param title string for the info window
	 * @param activity which this object resides in
	 */
	public void setUpInfoWindow(String title, Activity activity) {
		MarkerInfoWindow infoWindow = new MarkerInfoWindow(
				R.layout.bonuspack_bubble, openMapView);
		super.setInfoWindow(infoWindow);
		super.setTitle(title);
		super.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}

	/**
	 * Retrieves the POI string for the marker
	 * 
	 * @param activity which this object resides in
	 */
	public void getPOIString(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}
	
	/**
	 * Constructs a geoPoint from the geoLocation and returns it
	 * 
	 * @return GeoPoint corresponding to the geoLocation
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

	public MapView getOpenMapView() {
		return openMapView;
	}

	public void setOpenMapView(MapView openMapView) {
		this.openMapView = openMapView;
	}
}
