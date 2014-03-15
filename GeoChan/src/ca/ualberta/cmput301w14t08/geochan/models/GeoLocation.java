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
import android.location.LocationManager;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;

/**
 * Responsible for GeoLocation services for Comment objects
 */
public class GeoLocation {

    private Location location;

    /**
     * Constructs a new GeoLocation object when supplied a 
     * locationListenerService object
     * @param locationListenerService
     */
    public GeoLocation(LocationListenerService locationListenerService) {
        this.location = locationListenerService.getCurrentLocation();
    }
    
    /**
     * Construcs a new GeoLocation object with a supplied location object
     * @param location
     */
    public GeoLocation(Location location) {
        this.location = location;
    }
    
    /**
     * Construct a new GeoLocation object with a supplied latitude
     * and longitude
     * @param latitude
     * @param longitude
     */
    public GeoLocation(double latitude, double longitude) {
        this.location = new Location(LocationManager.GPS_PROVIDER);
        setCoordinates(latitude, longitude);
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

    /**
     * Sets both the longitude and latitude of the GeoLocation to new values.
     * Creates a new location object so that the LocationListenerServices's
     * lastKnownLocation attribute is not affected.
     * @param newLat The new latitude to be assigned.
     * @param newLong The new longitude to be assigned.
     */
    public void setCoordinates(double newLat, double newLong) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(newLat);
        newLocation.setLongitude(newLong);
        this.location = newLocation;
    }
    
    /**
     * Sets new latitude value. A new location object is created so that 
     * the LocationListnerService's lastKnownLocation attribute is not
     * affected
     * @param newLat
     */
    public void setLatitude(double newLat) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(newLat);
        if (location != null) {
            newLocation.setLongitude(location.getLongitude());
        } else {
            newLocation.setLongitude(0);
        }
        this.location = newLocation;
    }
    
    /**
     * Sets new longitude value. A new location object is created so that 
     * the LocationListnerService's lastKnownLocation attribute is not
     * affected
     * @param newLong
     */
    public void setLongitude(double newLong) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLongitude(newLong);
        if (location != null) {
            newLocation.setLatitude(location.getLatitude());
        } else {
            newLocation.setLatitude(0);
        }
        this.location = newLocation;
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
     * gets Location
     * @return location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Replaces the location attribute with a new supplied locatoin
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;
    }
}
