package ca.ualberta.cmput301w14t08.geochan.runnables;

import java.util.ArrayList;

import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocationLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadList;
import ca.ualberta.cmput301w14t08.geochan.tasks.PostTask;

public class GetPOIOnPostRunnable implements Runnable {

    private PostTask task;
    public static final int STATE_GET_POI_FAILED = -1;
    public static final int STATE_GET_POI_RUNNING = 0;
    public static final int STATE_GET_POI_COMPLETE = 1;

    public GetPOIOnPostRunnable(PostTask task) {
        this.task = task;
    }
    
    @Override
    public void run() {
        task.setGetPOIThread(Thread.currentThread());
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
        task.handleGetPOIState(STATE_GET_POI_RUNNING);
        GeoLocation location = task.getLocation();
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            // get the Geonames provider
            POI poi;
            GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("bradleyjsimons");
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
            task.setPOICache("Unknown Location");
            e.printStackTrace();
        } finally {
        	String poiString = task.getPOICache();
            if (poiString == null || poiString.equals("Unknown Location")) {
                poiString = "Unknown Location (" + task.getLocation().getLongitude()
                        + "," + task.getLocation().getLatitude() + ")";
                task.setPOICache(poiString);
                task.getLocation().setLocationDescription(poiString);
                task.getComment().setLocation(task.getLocation());
                task.handleGetPOIState(STATE_GET_POI_FAILED);
            } else {
                task.getLocation().setLocationDescription(poiString);
                task.getComment().setLocation(task.getLocation());
                GeoLocationLog geoLocationLog = GeoLocationLog.getInstance();
                geoLocationLog.addLogEntry(location);
                task.handleGetPOIState(STATE_GET_POI_COMPLETE);
            }
            //task.setGetPOIThread(null);
            Thread.interrupted();
        }
    }
}