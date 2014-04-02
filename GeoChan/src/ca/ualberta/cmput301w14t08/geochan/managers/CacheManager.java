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
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CacheManager {
    private static CacheManager instance;
    private Context context;
    private Gson commentGson;
    private Gson threadGson;
    private static final String EXTENSION = ".sav";
    private static final String FILENAME = "threads.sav";


    private CacheManager(Context context) {
        this.context = context;
        this.commentGson = GsonHelper.getOfflineGson();
        // thread Gson is same as online - all the data except for the comments.
        this.threadGson = GsonHelper.getOnlineGson();
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }
    
    /**
     * Serialize the list of threads with all the data with the exception of all
     * the comment children of the Thread body comment.
     * @param list
     */
    public void serializeThreadList(ArrayList<ThreadComment> list) {
        try {
            String json = threadGson.toJson(list);
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

    /**
     * Deserialise a list of ThreadComment objects without comments.
     * @return
     */
    public ArrayList<ThreadComment> deserializeThreadList() {
        ArrayList<ThreadComment> list = new ArrayList<ThreadComment>();
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
            Type type = new TypeToken<ArrayList<ThreadComment>>() {
            }.getType();
            list = threadGson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Serialize threadComment child comments into a file with the file's name being the
     * threadComment's id.
     * @param thread
     */
    public void serializeThreadCommentsById(ThreadComment thread) {
        try {
            String json = commentGson.toJson(thread.getBodyComment().getChildren());
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
     * Read ThreadComment child comments from json from the file with tread's id
     * as filename. 
     * @param id
     * @return
     */
    public ArrayList<Comment> deserializeThreadCommentsById(String id) {
        ArrayList<Comment> list = new ArrayList<Comment>();
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
            Type type = new TypeToken<ArrayList<Comment>>() {
            }.getType();
            list = commentGson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
