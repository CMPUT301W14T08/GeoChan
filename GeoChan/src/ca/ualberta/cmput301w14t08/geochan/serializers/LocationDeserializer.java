package ca.ualberta.cmput301w14t08.geochan.serializers;

import java.lang.reflect.Type;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class LocationDeserializer implements JsonDeserializer<Location> {

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