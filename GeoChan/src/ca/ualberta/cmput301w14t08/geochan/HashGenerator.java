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

import android.content.Context;
import android.provider.Settings.Secure;

public class HashGenerator {
    private Context context;
    private String android_id;
    private static final String hash = null;
    
    public HashGenerator(Context context) {
        this.context = context;
        android_id = Secure.getString(getContext().getContentResolver(),Secure.ANDROID_ID); 
    }

    public static String getHash() {
        if(hash == null) {
            return "12345";
        } else {
            return hash;
        }
    }

    public String getAndroid_id() {
        return android_id;
    }

    public Context getContext() {
        return context;
    }
}