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
 * Serialize a location object to Json
 * 
 * @author Artem Chikin
 */
public class LocationJsonConverter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
     * java.lang.reflect.Type, com.google.gson.JsonSerializationContext) Some of
     * this code is taken from a stackOverflow user Brian Roach, for details,
     * see: http://stackoverflow.com/a/13997920
     */
    @Override
    public JsonElement serialize(Location t, Type type, JsonSerializationContext jsc) {
        JsonObject jo = new JsonObject();
        jo.addProperty("mProvider", t.getProvider());
        jo.addProperty("mAccuracy", t.getAccuracy());
        jo.addProperty("latitude", t.getLatitude());
        jo.addProperty("longitude", t.getLongitude());
        return jo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
     * java.lang.reflect.Type, com.google.gson.JsonSerializationContext) Some of
     * this code is taken from a stackOverflow user Brian Roach, for details,
     * see: http://stackoverflow.com/a/13997920
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
