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

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerClickListener;
import org.osmdroid.bonuspack.overlays.Marker.OnMarkerDragListener;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.adapters.CustomLocationAdapter;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.helpers.SortUtil;
import ca.ualberta.cmput301w14t08.geochan.managers.ThreadManager;
import ca.ualberta.cmput301w14t08.geochan.models.CustomMarker;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;

/**
 * This class is a fragment which allows the user to specify a custom location
 * for their post. It gives the user the ability to select a location on a map,
 * pick from previously used locations, or simply use the user's current
 * location.
 * 
 * @author Brad Simons
 * 
 */
public class CustomLocationFragment extends Fragment {

	private int postType;
	private FragmentManager fm;
	private LocationListenerService locationListenerService;
	private GeoLocation newLocation;
	private MapView openMapView;
	private MapEventsOverlay mapEventsOverlay;
	private ArrayList<Marker> markers;
	private CustomMarker currentLocationMarker;

	// flags for type of post that initiated this fragment
	public static final int POST = 1;
	public static final int REPLY = 3;
	public static final int SORT_THREAD = 4;
	public static final int SORT_COMMENT = 5;
	public static final int EDIT = 6;

	/**
	 * Inflates the custom location fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(false);
		return inflater.inflate(R.layout.fragment_custom_location, container,
				false);
	}

	/**
	 * Inflates the menu and adds any action bar items that are present
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// inflater.inflate(R.menu.thread_list, menu);
		MenuItem item = menu.findItem(R.id.action_settings);
		item.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * Setups up the Location Log to display previous locations, connects the UI
	 * buttons, starts listening for location updates and finally calls setup
	 * map.
	 * 
	 */
	public void onStart() {
		super.onStart();
		GeoLocationLog log = GeoLocationLog.generateInstance(getActivity());
		ArrayList<GeoLocation> logArray = log.getLogEntries();

		FavouritesFragment favFrag = (FavouritesFragment) getFragmentManager()
				.findFragmentByTag("favouritesFrag");
		if (favFrag != null) {
			fm = getChildFragmentManager();
		} else {
			fm = getFragmentManager();
		}

		ListView lv = (ListView) getView().findViewById(
				R.id.custom_location_list_view);
		openMapView = (MapView) getActivity().findViewById(R.id.map_view);

		locationListenerService = new LocationListenerService(getActivity());
		locationListenerService.startListening();

		markers = new ArrayList<Marker>();

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// clicks a previous location item in the list
				GeoLocation logEntry = (GeoLocation) parent
						.getItemAtPosition(position);
				setBundleArguments(logEntry, "PREVIOUS_LOCATION");
				fm.popBackStackImmediate();
			}
		});

		CustomLocationAdapter customLocationAdapter = new CustomLocationAdapter(
				getActivity(), logArray);
		lv.setAdapter(customLocationAdapter);

		setupMap(getMapEventsReceiver());

		openMapView.invalidate();
	}

	/**
	 * Calls onStop in the super class and tells the locationListenerService to
	 * stop listening for location updates
	 */
	@Override
	public void onStop() {
		super.onStop();
		locationListenerService.stopListening();
	}

	/**
	 * Sets up the map view, gets the current location and initiates a new
	 * marker object from it, if valid. Then plots it on the map.
	 */
	private void setupMap(MapEventsReceiver mapEventsReceiver) {

		mapEventsOverlay = new MapEventsOverlay(getActivity(),
				mapEventsReceiver);
		openMapView.getOverlays().add(mapEventsOverlay);

		openMapView.setTileSource(TileSourceFactory.MAPNIK);
		openMapView.setBuiltInZoomControls(true);
		openMapView.setMultiTouchControls(true);

		GeoLocation currentLocation = new GeoLocation(locationListenerService);

		markers = new ArrayList<Marker>();

		// if valid current location, put it on the map
		if (currentLocation.getLocation() != null) {
			Drawable icon = getResources().getDrawable(
					R.drawable.current_location_pin);
			currentLocationMarker = createMarker(currentLocation, icon,
					"Current Location");

			openMapView.getOverlays().add(currentLocationMarker);
			openMapView.getController().setZoom(13);
			openMapView.getController().animateTo(
					currentLocation.makeGeoPoint());

		} else {
			ErrorDialog.show(getActivity(),
					"Could not retrieve current location");
			openMapView.getController().setZoom(3);
		}
	}

	/**
	 * Returns a MapEventsReceiver object which is used to handle click events
	 * on the map. Only the long click is implemented here, for setting a custom
	 * location pin
	 * 
	 * @return mapEventsReceiver
	 */
	private MapEventsReceiver getMapEventsReceiver() {
		MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {

			/**
			 * Called on a single tap
			 */
			@Override
			public boolean singleTapUpHelper(IGeoPoint clickedPoint) {
				return false;
			}

			/**
			 * Called on a long press on the map. A location marker is created
			 * and placed on the map where the user clicked
			 */
			@Override
			public boolean longPressHelper(IGeoPoint clickedPoint) {
				newLocation = new GeoLocation(clickedPoint.getLatitude(),
						clickedPoint.getLongitude());
				handleNewLocationPressed(newLocation);
				return false;
			}
		};

		return mapEventsReceiver;
	}

	/**
	 * Creates a map marker object based on a passed geoLocation, icon image,
	 * and title string. A network request for the Point of Interest of the
	 * location, and sets up the marker. Calls setMarkerListeners to react to a
	 * click on a marker
	 * 
	 * @param geoLocation
	 */
	public CustomMarker createMarker(GeoLocation geoLocation, Drawable icon,
			String title) {

		CustomMarker marker = new CustomMarker(geoLocation, openMapView, icon);
		marker.setUpInfoWindow(title, getActivity());

		// all markers are draggable except current location
		if (!(title.equals("Current Location"))) {
			marker.setDraggable(true);
		}

		setMarkerListeners(marker);
		markers.add(marker);
		return marker;
	}

	/**
	 * Sets up listeners for the marker passed in. First sets up the
	 * onMarkerClick listener, which hides all infoWindows and then shows the
	 * infoWindow of the marker that was clicked. Second, if the marker is
	 * draggable,
	 * 
	 * @param locationMarker
	 */
	private void setMarkerListeners(CustomMarker locationMarker) {

		locationMarker.setOnMarkerClickListener(new OnMarkerClickListener() {

			/**
			 * called if marker is clicked, hides all other infoWindows
			 */
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

		if (locationMarker.isDraggable()) {
			locationMarker.setOnMarkerDragListener(new OnMarkerDragListener() {

				/**
				 * Called as the marker is being dragged, no implementation
				 * 
				 * @param marker
				 */
				@Override
				public void onMarkerDrag(Marker marker) {
				}

				/**
				 * Called when the onDragListen action is complete Updates the
				 * location and POI when the drag is finished
				 * 
				 * @param marker
				 */
				@Override
				public void onMarkerDragEnd(Marker marker) {
					GeoLocation geoLocation = new GeoLocation(marker
							.getPosition().getLatitude(), marker.getPosition()
							.getLongitude());
					ProgressDialog dialog = new ProgressDialog(getActivity());
					dialog.setMessage("Retrieving Location");
					ThreadManager.startGetPOI(geoLocation, dialog, marker);
				}

				/**
				 * Called when the drag operation begins. No implementation at
				 * this time
				 * 
				 * @param marker
				 */
				@Override
				public void onMarkerDragStart(Marker marker) {
					hideInfoWindows();
				}
			});
		}
	}

	/**
	 * Creates a marker object by taking in latitude and longitude values and
	 * sets its position on the map view. Also adds a listener to the Marker for
	 * dragging the marker around the map
	 * 
	 * @param geoLocation
	 * @return marker
	 */
	private void handleNewLocationPressed(GeoLocation geoLocation) {
		hideInfoWindows();

		Drawable icon = getResources().getDrawable(R.drawable.red_map_pin);
		CustomMarker newLocationMarker = createMarker(geoLocation, icon,
				"New Location");

		setMarkerListeners(newLocationMarker);

		markers.clear();
		markers.add(newLocationMarker);

		openMapView.getOverlays().clear();
		openMapView.getOverlays().add(mapEventsOverlay);
		openMapView.getOverlays().add(newLocationMarker);
		if (currentLocationMarker != null) {
			openMapView.getOverlays().add(currentLocationMarker);
			markers.add(currentLocationMarker);
		}

		openMapView.invalidate();
	}

	/**
	 * Iterates through all markers on the map and hides their infoWindows
	 */
	private void hideInfoWindows() {
		for (Marker marker : markers) {
			marker.hideInfoWindow();
		}
	}

	/**
	 * Called when a user clicks the current location button. Gets the user's
	 * current location, puts it in a bundle and passes it back to the previous
	 * fragment
	 * 
	 * @param v
	 * 
	 */
	public void submitCurrentLocation(View v) {
		GeoLocation currentGeoLocation = new GeoLocation(
				locationListenerService);
		if (currentGeoLocation.getLocation() == null) {
			ErrorDialog.show(getActivity(), "Could not obtain location");
		} else {
			setBundleArguments(currentGeoLocation, "CURRENT_LOCATION");
		}
		fm.popBackStackImmediate();
	}

	/**
	 * Called when a user clicks the submit button. If the user has placed a
	 * location marker on the map, that location is placed in a bundle and
	 * passed back to the previous fragment
	 * 
	 * @param v
	 * 
	 */
	public void submitNewLocation(View v) {
		if (newLocation == null) {
			ErrorDialog.show(getActivity(),
					"Please select a location on the map");
		} else {
			setBundleArguments(newLocation, "NEW_LOCATION");
			fm.popBackStackImmediate();
		}
	}

	/**
	 * Bundles up all arguments required to be passed to previous fragment. This
	 * depends on the post type. It will attach latitude, longitude and
	 * description of the location to be submitted, as well as the type of
	 * location being returned (current location of user or a new location set
	 * on the map)
	 * 
	 * @param locationToSubmit
	 * @param locationType
	 */
	public void setBundleArguments(GeoLocation locationToSubmit,
			String locationType) {
		Bundle bundle = getArguments();
		postType = bundle.getInt("postType");
		if (postType == POST) {
			PostFragment fragment = (PostFragment) getFragmentManager()
					.findFragmentByTag("postFrag");
			if (fragment == null) {
				fragment = (PostFragment) getChildFragmentManager()
						.findFragmentByTag("postFrag");
			}
			Bundle args = fragment.getArguments();
			args.putDouble("LATITUDE", locationToSubmit.getLatitude());
			args.putDouble("LONGITUDE", locationToSubmit.getLongitude());
			args.putString("LocationType", locationType);
			args.putString("locationDescription",
					locationToSubmit.getLocationDescription());
		} else if (postType == SORT_THREAD) {
			SortUtil.setThreadSortGeo(locationToSubmit);
		} else if (postType == SORT_COMMENT) {
			SortUtil.setCommentSortGeo(locationToSubmit);
		} else if (postType == EDIT) {
			EditFragment fragment = (EditFragment) fm
					.findFragmentByTag("editFrag");
			Bundle args = fragment.getArguments();
			args.putDouble("LATITUDE", locationToSubmit.getLatitude());
			args.putDouble("LONGITUDE", locationToSubmit.getLongitude());
			args.putString("LocationType", locationType);
			args.putString("locationDescription",
					locationToSubmit.getLocationDescription());
		}
	}
}
