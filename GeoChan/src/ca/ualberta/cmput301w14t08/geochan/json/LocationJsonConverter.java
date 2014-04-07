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

package ca.ualberta.cmput301w14t08.geochan.json;

import java.lang.reflect.Type;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Handles the serialization and deserialization of useful data in a Location
 * to and from JSON format.
 * 
 * @author Artem Chikin
 */
public class LocationJsonConverter implements JsonSerializer<Location>, JsonDeserializer<Location> {

	/**
	 * Serializes data from a Location into JSON format.
	 * 
	 * (Some of this code is taken from a stackOverflow user
	 * Brian Roach, for details, see: http://stackoverflow.com/a/13997920)
	 * 
	 * @param location
	 *            the Location to serialize
	 * @param type
	 *            the Type
	 * @param jsc
	 *            the JSON serialization context
	 *
     */
    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsc) {
        JsonObject jo = new JsonObject();
        
        jo.addProperty("mProvider", location.getProvider());
        jo.addProperty("mAccuracy", location.getAccuracy());
        jo.addProperty("latitude", location.getLatitude());
        jo.addProperty("longitude", location.getLongitude());
        
        return jo;
    }

	/**
	 * Deserializes data into a Location from JSON format.
	 * 
	 * (Some of this code is taken from a stackOverflow user
	 * Brian Roach, for details, see: http://stackoverflow.com/a/13997920)
	 * 
	 * @param je
	 *            the JSON element to deserialize
	 * @param type
	 *            the Type
	 * @param jsc
	 *            the JSON serialization context
	 *
     */
    @Override
    public Location deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {
        JsonObject jo = je.getAsJsonObject();
        
        Location l = new Location(jo.getAsJsonPrimitive("mProvider").getAsString());
        l.setAccuracy(jo.getAsJsonPrimitive("mAccuracy").getAsFloat());
        l.setLongitude(jo.getAsJsonPrimitive("longitude").getAsFloat());
        l.setLatitude(jo.getAsJsonPrimitive("latitude").getAsFloat());
        
        return l;
    }

}
