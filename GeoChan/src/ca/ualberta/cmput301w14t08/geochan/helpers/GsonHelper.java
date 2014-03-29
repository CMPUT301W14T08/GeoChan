package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.location.Location;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;
import ca.ualberta.cmput301w14t08.geochan.serializers.CommentDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.CommentSerializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.LocationDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.LocationSerializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentDeserializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentDeserializerOffline;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentSerializer;
import ca.ualberta.cmput301w14t08.geochan.serializers.ThreadCommentSerializerOffline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

    private static Gson offlineGson = null;
    private static Gson onlineGson = null;
    private static GsonHelper instance = null;

    private GsonHelper() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentSerializer());
        builder.registerTypeAdapter(Comment.class, new CommentDeserializer());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentSerializer());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentDeserializer());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(Location.class, new LocationDeserializer());
        onlineGson = builder.create();
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentSerializer());
        builder.registerTypeAdapter(Comment.class, new CommentDeserializer());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentSerializerOffline());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentDeserializerOffline());
        builder.registerTypeAdapter(Location.class, new LocationSerializer());
        builder.registerTypeAdapter(Location.class, new LocationDeserializer());
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
