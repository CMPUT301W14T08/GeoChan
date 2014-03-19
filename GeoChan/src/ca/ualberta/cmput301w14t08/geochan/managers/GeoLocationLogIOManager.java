package ca.ualberta.cmput301w14t08.geochan.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;
import ca.ualberta.cmput301w14t08.geochan.serializers.LocationDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.LocationSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GeoLocationLogIOManager {
    private static GeoLocationLogIOManager instance;
    private Context context;
    private static final String FILENAME = "geolog.sav";

    private GeoLocationLogIOManager(Context context) {
        this.context = context;
    }

    public static GeoLocationLogIOManager getInstance(Context context) {
        if (instance == null) {
            instance = new GeoLocationLogIOManager(context);
        }
        return instance;
    }

    public void serializeLog(ArrayList<LogEntry> list) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Location.class, new LocationDeserializer());
            gsonBuilder.registerTypeAdapter(Location.class, new LocationSerializer());
            Gson gson = gsonBuilder.create();
            
            String json = gson.toJson(list);
            FileOutputStream f = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f)); 
            Log.e("GGG", "serialized");
            w.write(json);
            w.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LogEntry> deserializeLog() {
        ArrayList<LogEntry> list = new ArrayList<LogEntry>();
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Location.class, new LocationDeserializer());
            gsonBuilder.registerTypeAdapter(Location.class, new LocationSerializer());
            Gson gson = gsonBuilder.create();
            
            FileInputStream f = context.openFileInput(FILENAME);
            BufferedReader r = new BufferedReader(new InputStreamReader(f));
            String json = "";
            String temp = "";
            temp = r.readLine();
            while (temp != null) {
                json = json + temp;
                temp = r.readLine();
            }
            r.close();
            f.close();
            Type type = new TypeToken<ArrayList<LogEntry>>() {}.getType();
            list = gson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("size of location log:", Integer.toString(list.size()));
        return list;
    }
}
