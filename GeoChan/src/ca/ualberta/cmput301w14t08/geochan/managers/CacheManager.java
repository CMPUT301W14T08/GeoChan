package ca.ualberta.cmput301w14t08.geochan.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;

public class CacheManager {
    private static CacheManager instance;
    private Context context;
    private Gson gson;
    private static final String EXTENSION = ".sav";

    private CacheManager(Context context) {
        this.context = context;
        this.gson = GsonHelper.getOfflineGson();
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }
    
    /**
     * Serialize a threadComment into a file with the file's name being the
     * threadComment's id.
     * @param thread
     */
    public void serializeThreadById(ThreadComment thread) {
        try {
            String json = gson.toJson(thread);
            FileOutputStream f = context.openFileOutput(thread.getId()+EXTENSION, Context.MODE_PRIVATE);
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
    
    /**
     * Read a ThreadComment object from json from the file with tread's id
     * as filename. 
     * @param id
     * @return
     */
    public ThreadComment deserializeThreadById(String id) {
        ThreadComment thread = new ThreadComment();
        try {
            FileInputStream f = context.openFileInput(id+EXTENSION);
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
            thread = gson.fromJson(json, ThreadComment.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thread;
    }
}
