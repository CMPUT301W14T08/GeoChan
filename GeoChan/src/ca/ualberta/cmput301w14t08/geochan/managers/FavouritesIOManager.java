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
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.FavouritesLog;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class is responsible for persistency of favourite threads/comments
 * 
 */
public class FavouritesIOManager {
    private static FavouritesIOManager instance;
    private Context context;
    private Gson gson;
    private static final String FILENAME1 = "favcom.sav";
    private static final String FILENAME2 = "favthr.sav";

    private FavouritesIOManager(Context context) {
        this.context = context;
        this.gson = GsonHelper.getGson();
    }

    public static FavouritesIOManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesIOManager(context);
        }
        return instance;
    }

    public void serializeComments() {
        try {
            String json = gson.toJson(FavouritesLog.getInstance(context).getComments());
            FileOutputStream f = context.openFileOutput(FILENAME1, Context.MODE_PRIVATE);
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
        try {
            String json = gson.toJson(FavouritesLog.getInstance(context).getThreads());
            FileOutputStream f = context.openFileOutput(FILENAME2, Context.MODE_PRIVATE);
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

    public ArrayList<Comment> deSerializeComments() {
        ArrayList<Comment> list = new ArrayList<Comment>();
        try {
            FileInputStream f = context.openFileInput(FILENAME1);
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
            Type type = new TypeToken<ArrayList<Comment>>() {
            }.getType();
            list = gson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<ThreadComment> deSerializeThreads() {
        ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
        try {
            FileInputStream f = context.openFileInput(FILENAME2);
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
            Type type = new TypeToken<ArrayList<ThreadComment>>() {
            }.getType();
            list = gson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
