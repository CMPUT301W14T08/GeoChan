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

import java.util.ArrayList;

import org.osmdroid.bonuspack.location.GeoNamesPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import ca.ualberta.cmput301w14t08.geochan.helpers.LocationListenerService;

/**
 * Responsible for GeoLocation services for Comment objects Responsible for
 * keeping track of location, latitude and longitude values, point of interest
 * string (location description), and calculating distance between itself and
 * another geoLocation object.
 * 
 * @author Brad Simons
 */
public class GeoLocation {

    private Location location;
    private String locationDescription;
    private Activity activity;

    /**
     * Constructs a new GeoLocation object when supplied a
     * locationListenerService object
     * 
     * @param locationListenerService
     */
    public GeoLocation(LocationListenerService locationListenerService) {
        this.location = locationListenerService.getCurrentLocation();
        if (this.location == null) {
            this.location = locationListenerService.getLastKnownLocation();
        }
    }

    /**
     * Constructs a new GeoLocation object with a supplied location object
     * 
     * @param location
     */
    public GeoLocation(Location location) {
        this.location = location;
    }

    /**
     * Construct a new GeoLocation object with a supplied latitude and longitude
     * 
     * @param latitude
     * @param longitude
     */
    public GeoLocation(double latitude, double longitude) {
        this.location = new Location(LocationManager.GPS_PROVIDER);
        this.setCoordinates(latitude, longitude);
    }

    /**
     * Determines the distance in terms of coordinates between the GeoLocation
     * object and the passed GeoLocation.
     * 
     * @param toLocation
     *            The GeoLocation to be compared to.
     * @return The distance between the GeoLocations in terms of coordinates.
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
     * 
     * @param newLat
     *            The new latitude to be assigned.
     * @param newLong
     *            The new longitude to be assigned.
     */
    public void setCoordinates(double newLat, double newLong) {
        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
        newLocation.setLatitude(newLat);
        newLocation.setLongitude(newLong);
        this.location = newLocation;
    }

    /**
     * Sets new latitude value. A new location object is created so that the
     * LocationListnerService's lastKnownLocation attribute is not affected
     * 
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
     * Sets new longitude value. A new location object is created so that the
     * LocationListenerService's lastKnownLocation attribute is not affected
     * 
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
     * perform a request from GeoNames for the location Point of Interest
     * information. This is done with an async task on the network thread
     */
    public void retreivePOIString(Activity activity) {
        this.activity = activity;
        if (getLocation() == null) {
            this.setLocationDescription("Unknown Location");
        } else {
            new GetPOIAsyncTask().execute(makeGeoPoint());
        }
    }

    /**
     * Helper method to construct and return a GeoPoint object corresponding to
     * the location of this object.
     * 
     * @return geoPoint
     */
    public GeoPoint makeGeoPoint() {
        return new GeoPoint(getLatitude(), getLongitude());
    }

    /**
     * Async task for getting the POI of a location. Sets the location
     * description string with result.
     * 
     * @author Brad Simons
     */
    private class GetPOIAsyncTask extends AsyncTask<GeoPoint, Void, GeoPoint> {

        ProgressDialog retrievePOIDialog = new ProgressDialog(activity);
        POI poi;

        /**
         * Displays a ProgessDialog while the task is executing
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retrievePOIDialog.setMessage("Retrieving Location");
            retrievePOIDialog.show();
        }

        /**
         * Get the points of interest with 0.3 kilometers of of the location
         */
        @Override
        protected GeoPoint doInBackground(GeoPoint... geoPoints) {
            // get the Geonames provider
            GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("bradleyjsimons");

            for (GeoPoint geoPoint : geoPoints) {

                ArrayList<POI> pois = poiProvider.getPOICloseTo(geoPoint, 1, 0.8);

                if (pois.size() > 0 && pois != null) {
                    poi = pois.get(0);
                } else {
                    poi = null;
                }

                return geoPoint;
            }
            return null;
        }

        /**
         * Task is now finished, dismiss the ProgressDialog Set the
         * locationDescription string to the Point of Interest mType
         */
        @Override
        protected void onPostExecute(GeoPoint geoPoint) {
            super.onPostExecute(geoPoint);
            retrievePOIDialog.dismiss();

            if (poi != null) {
                setLocationDescription(poi.mType);
            } else {
                setLocationDescription("Unknown Location");
            }

        }
    }

    /**
     * Getters and Setters
     */

    public double getLatitude() {
        return location.getLatitude();
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}
