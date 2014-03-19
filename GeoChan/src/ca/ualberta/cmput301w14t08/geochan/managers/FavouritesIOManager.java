package ca.ualberta.cmput301w14t08.geochan.managers;

/**
 * This class is responsible for persistency of favourite threads/comments
 * 
 */
public class FavouritesIOManager {
    private static FavouritesIOManager instance;
    private static final String FILENAME = "fav.sav";

    private FavouritesIOManager() {
    }

    public static FavouritesIOManager getInstance() {
        if (instance == null) {
            instance = new FavouritesIOManager();
        }
        return instance;
    }

    public void serializeComments() {

    }

    public void serializeThreads() {

    }

    public void deSerializeComments() {

    }

    public void deSerializeThreads() {

    }
}
