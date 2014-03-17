package ca.ualberta.cmput301w14t08.geochan.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class PreferencesManager {
    private static Context context;
    private static SharedPreferences preferences;
    private static PreferencesManager instance = null;
    
    protected PreferencesManager(Context _context) {
        context = _context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public static void generateInstance(Context _context) {
        instance = new PreferencesManager(_context);
    }
    
    public static PreferencesManager getInstance() {
        return instance;
    }
    
    public String getUser() {
        return preferences.getString("username", "Anon");
    }
    
    public String getId() {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
}
