package ca.ualberta.cmput301w14t08.geochan.managers;

import android.content.Context;

/**
 * This class is responsible for persistency of favourite threads/comments
 * 
 */
public class FavouritesIOManager {
    private static Context context;
    private static FavouritesIOManager instance;
    private static final String FILENAME = "fav.sav";

    private FavouritesIOManager(Context context) {
        FavouritesIOManager.context = context;
    }

    public static void generateInstance(Context context) {
        if (instance == null) {
            instance = new FavouritesIOManager(context);
        }
    }

    public static FavouritesIOManager getInstance() {
        return instance;
    }

    public static void serializeComments() {

    }

    public static void serializeThreads() {

    }

    public static void deSerializeComments() {

    }

    public static void deSerializeThreads() {

    }
}
