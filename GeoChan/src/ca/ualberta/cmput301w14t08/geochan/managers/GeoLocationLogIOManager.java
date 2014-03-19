package ca.ualberta.cmput301w14t08.geochan.managers;

public class GeoLocationLogIOManager {
    private static GeoLocationLogIOManager instance;
    private static final String FILENAME = "geolog.sav";

    private GeoLocationLogIOManager() {
    }

    public static GeoLocationLogIOManager getInstance() {
        if (instance == null) {
            instance = new GeoLocationLogIOManager();
        }
        return instance;
    }

    public void serializeLog() {

    }

    public void deserializeLog() {

    }
}
