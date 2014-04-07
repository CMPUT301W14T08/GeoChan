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

package ca.ualberta.cmput301w14t08.geochan.runnables;

import java.util.ArrayList;

import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

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
		task.setGetPOIThread(Thread.currentThread());
		android.os.Process
				.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);
		task.handleGetPOIState(STATE_GET_POI_RUNNING);
		try {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			POI poi;
			// get the Geonames provider
			GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider(
					"bradleyjsimons");
			GeoLocation location = task.getLocation();
			GeoPoint geoPoint = new GeoPoint(location.getLatitude(),
					location.getLongitude());
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
			if (task.getPOICache() == null
					|| task.getPOICache().equals("Unknown Location")) {
				task.setPOICache("Unknown Location ("
						+ task.getLocation().getLongitude() + ","
						+ task.getLocation().getLatitude() + ")");
				task.handleGetPOIState(STATE_GET_POI_FAILED);
			} else {
				task.handleGetPOIState(STATE_GET_POI_COMPLETE);
			}
			// task.setGetPOIThread(null);
			Thread.interrupted();
		}
	}
}