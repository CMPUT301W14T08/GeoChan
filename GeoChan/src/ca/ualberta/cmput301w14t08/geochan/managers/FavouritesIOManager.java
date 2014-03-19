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
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class is responsible for persistency of favourite threads/comments
 * 
 */
public class FavouritesIOManager {
    private static FavouritesIOManager instance;
    private Context context;
    private static final String FILENAME = "fav.sav";

    private FavouritesIOManager(Context context) {
        this.context = context;
    }

    public static FavouritesIOManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesIOManager(context);
        }
        return instance;
    }

    public void serializeComments() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(FavouritesLog.getInstance(context).getComments());
            FileOutputStream f = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f)); 
            w.write(json);
            w.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serializeThreads() {

    }

    public ArrayList<Comment> deSerializeComments() {
        ArrayList<Comment> list = new ArrayList<Comment>();
        try {
            Gson gson = new Gson();
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
            Type type = new TypeToken<ArrayList<Comment>>() {}.getType();
            list = gson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deSerializeThreads() {

    }
}
