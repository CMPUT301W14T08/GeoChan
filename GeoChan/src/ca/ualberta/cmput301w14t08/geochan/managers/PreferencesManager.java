package ca.ualberta.cmput301w14t08.geochan.managers;

import ca.ualberta.cmput301w14t08.geochan.helpers.SortTypes;
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
    
    public int getThreadSort() {
        return preferences.getInt("thread_sort", SortTypes.SORT_DATE_NEWEST);
    }
    
    public int getCommentSort() {
        return preferences.getInt("comment_sort", SortTypes.SORT_DATE_NEWEST);
    }
}
