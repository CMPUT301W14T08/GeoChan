package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.graphics.Bitmap;
import android.location.Location;
import ca.ualberta.cmput301w14t08.geochan.json.BitmapJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.CommentJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.CommentOfflineJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.LocationJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.ThreadCommentJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.ThreadCommentOfflineJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    private static Gson offlineGson = null;
    private static Gson onlineGson = null;
    private static GsonHelper instance = null;

    private GsonHelper() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentJsonConverter());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentJsonConverter());
        builder.registerTypeAdapter(Bitmap.class, new BitmapJsonConverter());
        builder.registerTypeAdapter(Location.class, new LocationJsonConverter());
        onlineGson = builder.create();
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentOfflineJsonConverter());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentOfflineJsonConverter());
        builder.registerTypeAdapter(Location.class, new LocationJsonConverter());
        offlineGson = builder.create();
    }

    public static GsonHelper getInstance() {
        if (instance == null) {
            instance = new GsonHelper();
        }
        return instance;
    }

    public static Gson getOnlineGson() {
        if (onlineGson == null) {
            getInstance();
        }
        return onlineGson;
    }

    public static Gson getOfflineGson() {
        if (offlineGson == null) {
            getInstance();
        }
        return offlineGson;
    }

}
