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

import ca.ualberta.cmput301w14t08.geochan.services.LocationListenerService;
import android.location.Location;
<<<<<<< HEAD:GeoChan/src/ca/ualberta/cmput301w14t08/geochan/GeoLocation.java
import android.util.Log;
=======
>>>>>>> 705d0b74a4046788700b6a8b260d4e7b9587fba2:GeoChan/src/ca/ualberta/cmput301w14t08/geochan/models/GeoLocation.java

/**
 * Responsible for GeoLocation services for Comment objects
 */
public class GeoLocation {

    private Location location;
    private LocationListenerService locationListenerService;

    public GeoLocation(LocationListenerService locationListenerService) {
        this.locationListenerService = locationListenerService;
        this.location = locationListenerService.getCurrentLocation();
    }

    public void updateLocation(Location location) {
        this.location = location;
    }

    public double distance(GeoLocation toLocation) {
        Log.e("location Lat:", Double.toString(getLatitude()));
        Log.e("toLocation Lat:", Double.toString(toLocation.getLatitude()));
        Log.e("location Long:", Double.toString(getLongitude()));
        Log.e("toLocation Long:", Double.toString(toLocation.getLongitude()));
        
        double latDist = this.getLatitude() - toLocation.getLatitude();
        double longDist = this.getLongitude() - toLocation.getLongitude();
<<<<<<< HEAD:GeoChan/src/ca/ualberta/cmput301w14t08/geochan/GeoLocation.java
        Log.e("latDist", Double.toString(latDist));
        Log.e("longDist", Double.toString(longDist));
        return Math.sqrt(Math.pow(latDist,2) + Math.pow(longDist,2));
=======
        return Math.sqrt(Math.pow(latDist, 2) + Math.pow(longDist, 2));
>>>>>>> 705d0b74a4046788700b6a8b260d4e7b9587fba2:GeoChan/src/ca/ualberta/cmput301w14t08/geochan/models/GeoLocation.java
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
