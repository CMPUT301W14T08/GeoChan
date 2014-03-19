package ca.ualberta.cmput301w14t08.geochan.fragments;

import org.osmdroid.api.IMapController;
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
        openMapView = (MapView) getActivity().findViewById(R.id.open_map_view);
        openMapView.setBuiltInZoomControls(true);
        mapController = openMapView.getController();
        mapController.setZoom(4);
    }
}
