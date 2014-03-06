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

package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;

public class HashGenerator {
    private static Context context;
    private static HashGenerator instance;
    private static String android_id;

    private HashGenerator(Context context) {
        HashGenerator.context = context;
        android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
    
    public static void generateInstance(Context context) {
        if (instance == null) {
            instance = new HashGenerator(context);
        }
    }
    
    public static HashGenerator getInstance() {
        return instance;
    }

    public static String getHash() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String user = pref.getString("username", "Anon");
        int id = android_id.hashCode();
        int temp = (id + id + id * 3 + user.hashCode()) / 42;
        return Integer.toHexString(temp + id);
    }
    
    public static String getHash(String string) {
        int id = android_id.hashCode();
        int temp = (id + id + id * 3 + string.hashCode()) / 42;
        return Integer.toHexString(temp + id);
    }

    public String getAndroid_id() {
        return android_id;
    }

}
