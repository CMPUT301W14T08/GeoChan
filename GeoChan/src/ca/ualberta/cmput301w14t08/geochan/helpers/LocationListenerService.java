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

package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationListenerService {

    private static Location location;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private static final int TWO_MINUTES = 1000 * 60 * 2; // taken from Android
                                                          // Location Strategy

    /**
     * Constructs a new service object. Creates a locationListener object within
     * and implements its interface.
     * 
     * @param activity
     */
    public LocationListenerService(Activity activity) {
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Creates a LocationListener object and implements its interface.
        // If onLocationChanged is called, send the new location to
        // isBetterLocation to check before setting.
        locationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                if (isBetterLocation(newLocation, location)) {
                    location = newLocation;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    /**
     * LocationManager tells the locationListener to start listening for
     * location changes
     */
    public void startListening() {
        for (String provider : locationManager.getAllProviders()) {
            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
        }
    }

    /**
     * LocationManager tells the locationListener to stop listening for location
     * changes
     */
    public void stopListening() {
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Gets the current location. Checks if the latest location is better than
     * the location currently assigned
     * 
     * @return location
     */
    public Location getCurrentLocation() {
        Location tempLocation = getLastKnownLocation();
        if (isBetterLocation(tempLocation, location)) {
            location = tempLocation;
        }
        return location;
    }

    /**
     * asks the location manager for the last known location on the GPS Provider
     * 
     * @return location
     */
    public Location getLastKnownLocation() {
        // return
        // locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
    }

    /**
     * Checks to see if a new location is better than the currentBestLocation.
     * This code is from the Android Location Strategies guide, found here:
     * http://developer.android.com/guide/topics/location/strategies.html
     * 
     * @param location
     * @param currentBestLocation
     * @return boolean
     */
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be
            // worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks to see if two providers are the same. Used by isBetterLocation
     * method This code is from the Android Location Strategies Guide found
     * here: http://developer.android.com/guide/topics/location/strategies.html
     * 
     * @param provider1
     * @param provider2
     * @return boolean
     */
    public boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
