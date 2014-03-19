package ca.ualberta.cmput301w14t08.geochan.managers;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;

import com.google.gson.Gson;

/**
 * This class is responsible for persistency of favourite threads/comments
 * 
 */
public class FavouritesIOManager {
    private static FavouritesIOManager instance;
    private Context context;
    private static final String FILENAME = "fav.sav";

    private FavouritesIOManager(Context context) {
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

    public void deSerializeComments() {

    }

    public void deSerializeThreads() {

    }
}
