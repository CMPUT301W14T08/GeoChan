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

package ca.ualberta.cmput301w14t08.geochan.models;

import android.location.Location;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;

/**
 * Responsible for GeoLocation services for Comment objects
 */
public class GeoLocation {

    private Location location;

    public GeoLocation(LocationListenerService locationListenerService) {
        this.location = locationListenerService.getCurrentLocation();
    }
    
    public GeoLocation(Location location) {
        this.location = location;
    }

    /**
     * Determines the distance in terms of coordinates between
     * the GeoLocation object and the passed GeoLocation.
     * @param toLocation The GeoLocation to be compared to.
     * @return The distance between the GeoLocations in terms
     * of coordinates.
     */
    public double distance(GeoLocation toLocation) {
        double latDist = this.getLatitude() - toLocation.getLatitude();
        double longDist = this.getLongitude() - toLocation.getLongitude();
        return Math.sqrt(Math.pow(latDist, 2) + Math.pow(longDist, 2));
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Returns the latitude of the GeoLocation.
     * @return Latitude of the GeoLocation.
     */
    public double getLatitude() {
        return location.getLatitude();
    }

    /**
     * Returns the longitude of the GeoLocation.
     * @return Longitude of the GeoLocation.
     */
    public double getLongitude() {
        return location.getLongitude();
    }

    /**
     * Changes the latitude of the GeoLocation to the passed value.
     * @param newLat The new latitude.
     */
    public void setLatitude(double newLat) {
        location.setLatitude(newLat);
    }

    /**
     * Changes the longitude of the GeoLocation to the passed value.
     * @param newLong The new longitude.
     */
    public void setLongitude(double newLong) {
        location.setLongitude(newLong);
    }
}
