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

/**
 * This is a container class for one GeoLocationLog entry, it contains a String
 * and a geolocation objects. The string is the title of the thread, where the
 * location object was used.
 * 
 * @author bradsimons
 */
public class LogEntry {

    private String threadTitle;
    private String locationDescription;
    private GeoLocation geoLocation;

    /**
     * Constructor using a string and a GeoLocation object
     * 
     * @param title
     * @param geoLocation
     */
    public LogEntry(String title, GeoLocation geoLocation) {
        this.threadTitle = title;
        this.geoLocation = geoLocation;
        
        if (geoLocation.getLocationDescription() != null) {
            this.locationDescription = geoLocation.getLocationDescription();
        } else {
            this.locationDescription = "Unknown Location";
        }
    }

    /**
     * Getters and Setters
     */

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }
    
    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}
