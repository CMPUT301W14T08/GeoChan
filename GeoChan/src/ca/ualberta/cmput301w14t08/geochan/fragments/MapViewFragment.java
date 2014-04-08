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

package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.GridMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.MapDataHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.CustomMarker;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * A Fragment class for displaying Maps. The Map will display the locations of
 * each comment in the thread, and will center around the original post. It will
 * provide the feature of getting directions from the users current location to
 * the location of the original post.
 * 
 * @author Brad Simons
 * 
 */
public class MapViewFragment extends Fragment {

	private MapDataHelper mapData;
	private LocationListenerService locationListenerService;
	private CustomMarker originalPostMarker;
	private Polyline roadOverlay;
	private GridMarkerClusterer replyPostClusterMarkers;
	private GridMarkerClusterer directionsClusterMarkers;
	private GridMarkerClusterer startAndFinishClusterMarkers;
	private ArrayList<GridMarkerClusterer> clusterers;
	private ArrayList<CustomMarker> markers;

	/**
	 * Set up the fragment's UI.
	 * 
	 * @param inflater
	 *            The LayoutInflater used to inflate the fragment's UI.
	 * @param container
	 *            The parent View that the fragment's UI is attached to.
	 * @param savedInstanceState
	 *            The previously saved state of the fragment.
	 * @return The View for the fragment's UI.
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(false);

		return inflater.inflate(R.layout.fragment_map_view, container, false);
	}

	/**
	 * Inflates the menu and adds and add items to action bar if present.
	 * 
	 * @param menu
	 *            The Menu object for the fragment.
	 * @param inflater
	 *            the MenuInflater for inflating the fragment's menu.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Initiates a location listener which immediately starts listening for
	 * location updates. Gets the current location as well. Then unpacks the
	 * bundle passed to the fragment. It then gets the map setup and prepares
	 * the min and max latitude and longitude required to display the map
	 * properly for calculation. Then finally sets the zoom level
	 */
	@Override
	public void onStart() {
		super.onStart();

		locationListenerService = new LocationListenerService(getActivity());
		locationListenerService.startListening();

		Bundle args = getArguments();
		Comment topComment = (Comment) args.getParcelable("thread_comment");

		markers = new ArrayList<CustomMarker>();

		setupClusterGroups();
		
		GeoLocation geoLocation = topComment.getLocation();
		if (geoLocation.getLocation() == null) {
			ErrorDialog.show(getActivity(), "Thread has no location");
			FragmentManager fm = getFragmentManager();
			fm.popBackStackImmediate();
		} else {
			this.setupMap(topComment);
			this.setZoomLevel(topComment.getLocation());
		}
	}

	/**
	 * Calls onStop in the superclass, and tells the locationListener to stop
	 * listening.
	 */
	@Override
	public void onStop() {
		super.onStop();
		locationListenerService.stopListening();
	}

	/**
	 * Sets up cluster groups so that when many Markers over lap each other in
	 * the same cluster group, a cluster image is displayed instead of the
	 * markers. This cluster image also displays the number of Markers that it
	 * represents
	 */
	public void setupClusterGroups() {
		replyPostClusterMarkers = new GridMarkerClusterer(getActivity());
		directionsClusterMarkers = new GridMarkerClusterer(getActivity());
		startAndFinishClusterMarkers = new GridMarkerClusterer(getActivity());

		Drawable clusterIconD = getResources().getDrawable(
				R.drawable.marker_cluster);
		Bitmap clusterIcon = ((BitmapDrawable) clusterIconD).getBitmap();

		directionsClusterMarkers.setIcon(clusterIcon);
		replyPostClusterMarkers.setIcon(clusterIcon);
		startAndFinishClusterMarkers.setIcon(clusterIcon);
		
		clusterers = new ArrayList<GridMarkerClusterer>();
		clusterers.add(directionsClusterMarkers);
		clusterers.add(replyPostClusterMarkers);
		clusterers.add(startAndFinishClusterMarkers);
	}

	/**
	 * This sets up the comment location the map. The map is centered at the
	 * location of the comment GeoLocation, and places a pin at this point. It
	 * then calls handleChildComments to place pins for each child comment in
	 * the thread.
	 * 
	 * @param topComment
	 *            The OP of the ThreadComment.
	 */
	public void setupMap(Comment topComment) {
		mapData = new MapDataHelper((MapView) getActivity().findViewById(
				R.id.open_map_view));
		mapData.setUpMap();

		if (commentLocationIsValid(topComment)) {
			GeoLocation geoLocation = topComment.getLocation();
			Drawable icon = getResources().getDrawable(R.drawable.red_map_pin);
			originalPostMarker = new CustomMarker(geoLocation,
					mapData.getMap(), icon);
			originalPostMarker.setUpInfoWindow("OP", getActivity());
			
			setMarkerListeners(originalPostMarker);

			markers.add(originalPostMarker);
			startAndFinishClusterMarkers.add(originalPostMarker);

			handleChildComments(topComment);

			mapData.getOverlays().add(replyPostClusterMarkers);
			mapData.getOverlays().add(directionsClusterMarkers);
			mapData.getOverlays().add(originalPostMarker);
		}
		mapData.getMap().invalidate();
	}

	/**
	 * Sets the default zoom level for the mapview. This takes the max and min
	 * of both lat and long, and zooms to span the area required. It also
	 * animates to the startGeoPoint, which is the location of the topComment.
	 * The values must be padded with a zoom_factor, which is a static class
	 * variable
	 * 
	 * @param geoLocation
	 *            GeoLocation used to start the basis of the distance
	 */
	public void setZoomLevel(GeoLocation geoLocation) {
		// get the mapController and set the zoom
		IMapController mapController = mapData.getController();

		int zoomFactor;
		int zoomSpan = calculateZoomSpan();

		// calculates the appropriate zoom level
		zoomFactor = 19 - (int) (Math.log10(zoomSpan) * 2.2);
		if (zoomFactor > 18 || zoomSpan < 1) {
			zoomFactor = 18;
		} else if (zoomFactor < 2) {
			zoomFactor = 2;
		}

		// set the zoom center
		mapController.setZoom(zoomFactor);
		mapController.animateTo(geoLocation.makeGeoPoint());
	}

	/**
	 * Calculates the minimum and maximum values for latitude and longitude
	 * between an array of GeoPoints. This is used to determine the zoom level.
	 * 
	 * @return The maximum distance between markers on the map.
	 */
	private int calculateZoomSpan() {
		int opLat = originalPostMarker.getPosition().getLatitudeE6();
		int opLong = originalPostMarker.getPosition().getLongitudeE6();

		// To calculate the max and min latitude and longitude of all
		// the comments, we set the min's to max integer values and vice versa
		// then have values of each comment modify these variables
		int minLat = opLat;
		int maxLat = opLat;
		int minLong = opLong;
		int maxLong = opLong;

		// get max min lat long for replies
		for (CustomMarker marker : markers) {
			if (marker.getGeoLocation() != originalPostMarker.getGeoLocation()) {
				GeoPoint geoPoint = marker.getGeoPoint();
				int geoLat = geoPoint.getLatitudeE6();
				int geoLong = geoPoint.getLongitudeE6();

				maxLat = Math.max(geoLat, maxLat);
				minLat = Math.min(geoLat, minLat);
				maxLong = Math.max(geoLong, maxLong);
				minLong = Math.min(geoLong, minLong);
			}
		}
		int deltaLong = maxLong - minLong;
		int deltaLat = maxLat - minLat;
		return Math.max(deltaLong, deltaLat);
	}

	/**
	 * Sets an onMarkerClickListener and onMarkerDragListener the marker passed
	 * in. This is used to handle click events for the maps, which will cause
	 * infoWindows to show and hide.
	 * 
	 * @param locationMarker
	 *            Marker that the listeners will be attached to.
	 */
	private void setMarkerListeners(Marker locationMarker) {

		locationMarker.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker, MapView map) {
				if (marker.isInfoWindowShown() != true) {
					hideInfoWindows();
					marker.showInfoWindow();
				} else {
					hideInfoWindows();
				}
				return false;
			}
		});
	}

	/**
	 * Recursive method for handling all comments in the thread. First checks if
	 * the comment has any children or not. If none, simply return. Otherwise,
	 * call setGeoPointMarker for each child of the comment. Call
	 * checkCommmentLocation to calculate the min and max of the lat and long
	 * for the entire thread. Then finally make a recursive call to check if a
	 * child comment has any children.
	 * 
	 * @param comment
	 *            Comment to be added to the map.
	 */
	private void handleChildComments(Comment comment) {
		ArrayList<Comment> children = comment.getChildren();
		if (children.size() == 0) {
			return;
		} else {
			for (Comment childComment : children) {
				GeoLocation commentLocation = childComment.getLocation();

				if (commentLocationIsValid(childComment)) {
					Drawable icon = getResources().getDrawable(
							R.drawable.blue_map_pin);

					CustomMarker replyMarker = new CustomMarker(
							commentLocation, mapData.getMap(), icon);
					replyMarker.createInfoWindow();
					replyMarker.setTitle("Reply");
					
					if (commentLocation.getLocationDescription() != null) {
						replyMarker.setSubDescription(commentLocation
								.getLocationDescription());
					} else {
						replyMarker.setSubDescription("Unknown Location");
					}

					setMarkerListeners(replyMarker);
					replyPostClusterMarkers.add(replyMarker);
					markers.add(replyMarker);
					handleChildComments(childComment);
				}
			}
		}
	}

	/**
	 * Hides infoWindows for every marker on the map
	 */
	private void hideInfoWindows() {
		for (Marker marker : markers) {
			marker.hideInfoWindow();
		}
	}

	/**
	 * Checks to see if a comment in the thread has valid GPS coordinates. Valid
	 * coordinates are -90 < lat < 90, and -180 < longitude < 180. It also does
	 * a null check on location.
	 * 
	 * @param comment
	 *            to be check for valid location
	 * @return boolean isValidLocation
	 */
	public boolean commentLocationIsValid(Comment comment) {
		GeoLocation location = comment.getLocation();
		if (location.getLocation() == null) {
			return false;
		} else {
			return (location.getLatitude() >= -90.0
					|| location.getLatitude() <= 90.0
					|| location.getLongitude() >= -180.0 || location
					.getLongitude() <= 180.0);
		}
	}

	/**
	 * Called when the get_directions_button is clicked. Displays directions
	 * from the users current location to the comment location. Uses an Async
	 * task to get map overlay. If the users current location cannot be
	 * obtained, an error is shown to the screen and the async task is not
	 * called
	 */
	public void getDirections() {
		GeoLocation currentLocation = new GeoLocation(locationListenerService);

		if (currentLocation.getLocation() == null) {
			ErrorDialog.show(getActivity(), "Could not retrieve your location");
		} else {
			new GetDirectionsAsyncTask().execute();
		}

		mapData.refreshMap();
	}

	/**
	 * Async task class. This task is designed to retrieve directions from the
	 * users current location to the location of the original post of the
	 * thread. It displays a ProgressDialog while the location is being
	 * retrieved.
	 * 
	 * @author Brad Simons
	 */
	class GetDirectionsAsyncTask extends AsyncTask<Void, Void, Void> {

		ProgressDialog directionsLoadingDialog = new ProgressDialog(
				getActivity());

		/**
		 * Displays a ProgessDialog while the task is executing
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			directionsLoadingDialog.setMessage("Getting Directions");
			directionsLoadingDialog.show();
		}

		/**
		 * Calculating the directions from the current to the location of the
		 * topComment. Builds a road overlay and adds it to the openMapView
		 * objects overlays.
		 */
		@Override
		protected Void doInBackground(Void... params) {
			RoadManager roadManager = new OSRMRoadManager();
			ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();

			GeoLocation currentLocation = new GeoLocation(
					locationListenerService);

			waypoints.add(new GeoPoint(currentLocation.getLatitude(),
					currentLocation.getLongitude()));
			waypoints.add(originalPostMarker.getPosition());
			Road road = roadManager.getRoad(waypoints);

			roadOverlay = RoadManager.buildRoadOverlay(road, getActivity());

			Drawable nodeIcon = getResources().getDrawable(
					R.drawable.marker_node);
			Drawable nodeImage = getResources().getDrawable(
					R.drawable.ic_continue);

			for (int i = 0; i < road.mNodes.size(); i++) {
				RoadNode node = road.mNodes.get(i);
				GeoLocation geoLocation = new GeoLocation(node.mLocation);
				CustomMarker nodeMarker = new CustomMarker(geoLocation,
						mapData.getMap(), nodeIcon);
				
				//MarkerInfoWindow infoWindow = new MarkerInfoWindow(
				//		R.drawable.bonuspack_bubble, mapData.getMap());
				//nodeMarker.setUpInfoWindow("Step " + i, getActivity());
				nodeMarker.setSnippet(node.mInstructions);
				nodeMarker.setSubDescription(Road.getLengthDurationText(
						node.mLength, node.mDuration));
				nodeMarker.setImage(nodeImage);

				setMarkerListeners(nodeMarker);

				directionsClusterMarkers.add(nodeMarker);
				markers.add(nodeMarker);
			}

			return null;
		}

		/**
		 * Task is now finished. Creates the current location marker and sets it
		 * on the map. Clears the map and re-adds all the overlays to the map,
		 * then refreshes the map
		 */
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			directionsLoadingDialog.dismiss();

			hideInfoWindows();

			GeoLocation currentLocation = new GeoLocation(
					locationListenerService);

			Drawable icon = getResources()
					.getDrawable(R.drawable.green_map_pin);

			CustomMarker currentLocationMarker = new CustomMarker(
					currentLocation, mapData.getMap(), icon);
			currentLocationMarker.setUpInfoWindow("Current Location",
					getActivity());

			setMarkerListeners(currentLocationMarker);

			startAndFinishClusterMarkers.add(currentLocationMarker);
			markers.add(currentLocationMarker);

			mapData.getOverlays().clear();
			mapData.getOverlays().add(roadOverlay);
			mapData.getOverlays().add(directionsClusterMarkers);
			mapData.getOverlays().add(replyPostClusterMarkers);
			mapData.getOverlays().add(startAndFinishClusterMarkers);

			mapData.setZoom(15);
			mapData.setCenter(currentLocation.makeGeoPoint());

			mapData.refreshMap();
		}
	}

}