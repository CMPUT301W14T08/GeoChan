package ca.ualberta.cmput301w14t08.geochan.fragments;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.ErrorDialog;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

public class MapViewFragment extends Fragment {

    private MapView openMapView;
    private IMapController mapController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_map_view, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override 
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        Comment comment = (Comment) args.getParcelable("thread_comment");
        GeoLocation geoLocation = comment.getLocation();
        if (geoLocation.getLocation() == null) {
            ErrorDialog.show(getActivity(), "Thread has no location");
            FragmentManager fm = getFragmentManager();
            fm.popBackStackImmediate();
        } else {

            openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
            openMapView.setTileSource(TileSourceFactory.MAPNIK);
            openMapView.setBuiltInZoomControls(true);
            openMapView.setMultiTouchControls(true);
            
            GeoPoint geoPoint = new GeoPoint(geoLocation.getLocation());
            
            Marker startMarker = new Marker(openMapView);
            startMarker.setPosition(geoPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            openMapView.getOverlays().add(startMarker);
            
            mapController = openMapView.getController();
            mapController.setZoom(12);
            mapController.setCenter(geoPoint);

        }
    }
}
