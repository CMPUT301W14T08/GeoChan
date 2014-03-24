package ca.ualberta.cmput301w14t08.geochan.activities;

import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;


/**
 * NEED COMMENT HERE
 * @author 
 *
 */
public class PreferencesActivity extends PreferenceActivity {
    private EditTextPreference username;

    /*
     * Because we are using the support library for fragments, we must use the
     * deprecated method of inflating the preferences XML inside of the
     * PreferenceActivity since the support library's fragment manager does not
     * support the use of a PreferenceFragment.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        username = (EditTextPreference) findPreference("username");
        username.setSummary(username.getText());

        username.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                Preference hash = findPreference("device_hash");
                hash.setSummary((String) newValue + " #" + HashHelper.getHash((String) newValue));
                return true;
            }
        });

        Preference hash = findPreference("device_hash");
        hash.setSummary(username.getText() + " #" + HashHelper.getHash());
    }
}
