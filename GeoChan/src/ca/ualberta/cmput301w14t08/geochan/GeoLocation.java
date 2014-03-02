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

package ca.ualberta.cmput301w14t08.geochan;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Responsible for GeoLocation services for Comment objects
 */

public class GeoLocation {

    private Location location;

    public GeoLocation(Activity activity) {
   
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        /**
         * Set up a listener so that the device can get the most current location.
         * Without the listener, new GeoLocation objects only will be set to the
         * cached location.
         */
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location newLocation) {
                //location = newLocation;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        /** 
         * I'm not totally sure why this is required yet, but it crashes without.
         * I believe this is the cached location. The listener above is required to 
         * update this, but I can't seem to just set the location from 
         * onLocationChanged() above (returns null);
         */
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
        locationManager.removeUpdates(locationListener);
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    /**
     * Calculates the distance between this object and another GeoLocation object
     * @param toLocation
     * @return distance as a double
     */
    public double distance(GeoLocation toLocation) {
        double latDist = this.getLatitude() - toLocation.getLatitude();
        double longDist = this.getLongitude() - toLocation.getLongitude();
        return Math.sqrt(Math.pow(latDist,2) + Math.pow(longDist,2));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }
    
    public void setLatitude(double newLat) {
        location.setLatitude(newLat);
    }
    
    public void setLongitude(double newLong) {
        location.setLongitude(newLong);
    }
}
