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

package ca.ualberta.cmput301w14t08.geochan;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.provider.Settings.Secure;

public class PreferencesFragment extends PreferenceFragment {

    //private String username;
    //private EditTextPreference username;
    private static String android_id;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setAndroid_id(Secure.getString(this.getActivity().getContentResolver(),
                Secure.ANDROID_ID));         
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        EditTextPreference name = (EditTextPreference) findPreference("username");
        name.setSummary(name.getText());

        name.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });

        Preference id = findPreference("device_id_tag");
        id.setDefaultValue(getAndroid_id());
        id.setSummary(getAndroid_id());
        
        Preference hash = findPreference("device_hash");
        //hash.setSummary(hashDeviceId());
        hash.setSummary(name.getText() + "#");
    }

    public static String getAndroid_id() {
        return android_id;
    }
    
    private static void setAndroid_id(String android_id) {
        PreferencesFragment.android_id = android_id;
    }
    
    // TODO
    private String hashDeviceId() {
        //String hash = Integer.toString( getAndroid_id().hashCode());
        //return hash;
        return null;
    }
}