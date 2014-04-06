package ca.ualberta.cmput301w14t08.geochan.runnables;

import java.util.ArrayList;

import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.tasks.GetPOITask;

public class GetPOIRunnable implements Runnable {

    private GetPOITask task;
    public static final int STATE_GET_POI_FAILED = -1;
    public static final int STATE_GET_POI_RUNNING = 0;
    public static final int STATE_GET_POI_COMPLETE = 1;

    public GetPOIRunnable(GetPOITask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        Log.e("POI", "START");
        task.setGetPOIThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
        task.handleGetPOIState(STATE_GET_POI_RUNNING);
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            POI poi;
            // get the Geonames provider
            GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("bradleyjsimons");
            GeoLocation location = task.getLocation();
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            ArrayList<POI> pois = poiProvider.getPOICloseTo(geoPoint, 1, 0.8);
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            if (pois.size() > 0 && pois != null) {
                poi = pois.get(0);
            } else {
                poi = null;
            }
            task.setPOICache(poi.mType);
        } catch (Exception e) {
            Log.e("POI", "EXCEPTION");
            task.setPOICache("Unknown Location");
            e.printStackTrace();
        } finally {
            if (task.getPOICache() == null || task.getPOICache().equals("Unknown Location")) {
                task.setPOICache("Unknown Location (" + task.getLocation().getLongitude()
                                                      + "," + task.getLocation().getLatitude() + ")");
                task.handleGetPOIState(STATE_GET_POI_FAILED);
            } else {
                task.handleGetPOIState(STATE_GET_POI_COMPLETE);
                Log.e("POI", "COMPLETE");
            }
            //task.setGetPOIThread(null);
            Thread.interrupted();
        }
    }
}