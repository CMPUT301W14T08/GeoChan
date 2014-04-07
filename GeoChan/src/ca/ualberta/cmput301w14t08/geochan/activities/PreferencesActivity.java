/*
 * Copyright 2014 Artem Chikin
 * Copyright 2014 Artem Herasymchuk
 * Copyright 2014 Tom Krywitsky
 * Copyright 2014 Henry Pabst
 * Copyright 2014 Bradley Simons
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.ualberta.cmput301w14t08.geochan.activities;

import ca.ualberta.cmput301w14t08.geochan.R;
import ca.ualberta.cmput301w14t08.geochan.helpers.HashHelper;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

/**
 * Inflates our preferences and presents a preference activity
 * to the user to allow them to see and change settings in the app.
 * 
 * @author Artem Chikin
 * 
 */
public class PreferencesActivity extends PreferenceActivity {
    private EditTextPreference username;

    /**
     * Inflates the preferences XML and listens for changes in order
     * to update the user interface.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /*
         * Load the preferences from an XML resource.
         * 
         * Because we are using the support library for fragments, we must use the
         * deprecated method of inflating the preferences XML inside of the
         * PreferenceActivity since the support library's fragment manager does not
         * support the use of a PreferenceFragment.
         */
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
