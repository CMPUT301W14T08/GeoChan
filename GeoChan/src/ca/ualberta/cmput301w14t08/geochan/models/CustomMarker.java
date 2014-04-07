package ca.ualberta.cmput301w14t08.geochan.models;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
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

	private String title;
	private Drawable icon;
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
		super.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
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
		super.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
		super.setIcon(icon);
		this.icon = icon;
		this.openMapView = mapView;
		this.geoLocation = geoLocation;
	}

	/**
	 * Sets up the infoWindow bubble for the Marker. Sets a title and icon image
	 * 
	 * @param icon
	 * @param title
	 * @param activity
	 */
	public void setUpInfoWindow(String title, Activity activity) {
		MarkerInfoWindow infoWindow = new MarkerInfoWindow(
				R.layout.bonuspack_bubble, openMapView);
		super.setInfoWindow(infoWindow);
		super.setTitle(title);
		this.title = title;
		
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}

	/**
	 * Retrieves the POI string for the marker
	 * 
	 * @param activity
	 */
	public void getPOIString(Activity activity) {
		ProgressDialog dialog = new ProgressDialog(activity);
		dialog.setMessage("Retrieving Location");
		ThreadManager.startGetPOI(geoLocation, dialog, this);
	}

	/**
	 * Getters and setters
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

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
