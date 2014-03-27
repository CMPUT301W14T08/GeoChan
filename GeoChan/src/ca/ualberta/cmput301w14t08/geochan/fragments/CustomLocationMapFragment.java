package ca.ualberta.cmput301w14t08.geochan.fragments;

import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

/**
 * COMMENT HERE
 * 
 * @author Brad Simons
 * 
 */
public class CustomLocationMapFragment extends Fragment {

    private LocationListenerService locationListenerService;
    private GeoLocation currentLocation;
    private MapView openMapView;

    /**
     * COMMENT HERE
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_custom_location_map, container, false);
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onStart() {
        super.onStart();

        locationListenerService = new LocationListenerService(getActivity());
        locationListenerService.startListening();
        currentLocation = new GeoLocation(locationListenerService);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver();
        MapEventsOverlay overlay = new MapEventsOverlay(getActivity(),getActivity());
        openMapView.getOverlays().add(overlay);


        
        openMapView = (MapView) getActivity().findViewById(R.id.select_location_map_view);

        this.setupMap();
    }

    /**
     * COMMENT HERE
     */
    @Override
    public void onStop() {
        super.onStop();
        locationListenerService.stopListening();
    }

    /**
     * COMMENT HERE
     */
    public void setupMap() {
        openMapView.setTileSource(TileSourceFactory.MAPNIK);
        openMapView.setBuiltInZoomControls(true);
        openMapView.setMultiTouchControls(true);
        openMapView.getController().setZoom(3);
        // openMapView.getController().animateTo(new
        // GeoPoint(currentLocation.getLocation()));
    }
}
