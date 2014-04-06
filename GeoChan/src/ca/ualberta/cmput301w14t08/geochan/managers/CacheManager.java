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
import android.graphics.Bitmap;
import android.util.Log;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CacheManager {
    private static CacheManager instance = null;
    private Context context;
    private Gson offlineGson;
    private Gson onlineGson;
    private static final String EXTENSION = ".sav";
    private static final String FILENAME = "threads.sav";
    private static final String IMAGE = "IMG";

    private CacheManager(Context context) {
        this.context = context;
        this.offlineGson = GsonHelper.getOfflineGson();
        // thread Gson is same as online - all the data except for the comments.
        this.onlineGson = GsonHelper.getOnlineGson();
    }

    public static CacheManager getInstance() {
        return instance;
    }
    
    public static void generateInstance(Context context) {
        instance = new CacheManager(context);
    }
    
    public void serializeImage(Bitmap image, String id) {
        try {
            String json = onlineGson.toJson(image);
            FileOutputStream f = context.openFileOutput(IMAGE + id + EXTENSION, Context.MODE_PRIVATE);
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
    
    public Bitmap deserializeImage(String id) {
        Bitmap image = null;
        try {
            FileInputStream f = context.openFileInput(IMAGE + id + EXTENSION);
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
            image = onlineGson.fromJson(json, Bitmap.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Serialize the list of threads with all the data with the exception of all
     * the comment children of the Thread body comment.
     * 
     * @param list
     */
    public void serializeThreadList(ArrayList<ThreadComment> list) {
        try {
            String json = onlineGson.toJson(list);
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
     * 
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
            list = onlineGson.fromJson(json, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Serialize ThreadComment object into a file with the file's name
     * being ThreadComment's id;
     * @param thread
     */
    public void serializeThreadCommentById(ThreadComment thread) {
        try {
            String json = offlineGson.toJson(thread);
            FileOutputStream f = context.openFileOutput(thread.getId() + EXTENSION,
                    Context.MODE_PRIVATE);
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(f));
            w.write(json);
            w.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("Thread", "Serialized");
    }
    
    /**
     * Retrieve ThreadComment object by id and return the children
     * of its body comment. This is done because we already have ThreadComment
     * object and its BodyComment from the ThreadComment Deserializers,
     * This deserializer runs when a thread is opened and we need to retrieve comments
     * only from the cache.
     * 
     * @param id
     * @return
     */
    public ArrayList<Comment> deserializeThreadCommentById(String id) {
        ThreadComment thread = null;
        try {
            FileInputStream f = context.openFileInput(id + EXTENSION);
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
            thread = offlineGson.fromJson(json, ThreadComment.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (thread != null) {
            Log.e("Thread", "Deserialized");
            return thread.getBodyComment().getChildren();
        } else {
        	return null;
        }
    }
}
