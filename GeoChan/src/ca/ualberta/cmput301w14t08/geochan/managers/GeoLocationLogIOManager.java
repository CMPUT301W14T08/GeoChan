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
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.LogEntry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GeoLocationLogIOManager {
    private static GeoLocationLogIOManager instance;
    private Context context;
    private Gson gson;
    private static final String FILENAME = "geolog.sav";

    private GeoLocationLogIOManager(Context context) {
        this.context = context;
        this.gson = GsonHelper.getOnlineGson();
    }

    public static GeoLocationLogIOManager getInstance(Context context) {
        if (instance == null) {
            instance = new GeoLocationLogIOManager(context);
        }
        return instance;
    }

    public void serializeLog(ArrayList<LogEntry> list) {
        try {
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
            Type type = new TypeToken<ArrayList<LogEntry>>() {
            }.getType();
            list = gson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("size of location log:", Integer.toString(list.size()));
        for (LogEntry l : list) {
            Log.e("WWW", l.getThreadTitle());
        }
        return list;
    }
}
