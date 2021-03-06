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

import java.util.Date;
import java.util.Random;

import ca.ualberta.cmput301w14t08.geochan.managers.PreferencesManager;

/**
 * Helper class. Generates hashes used in identifying Comments and users.
 * @author Artem Chikin
 * 
 */
public class HashHelper {
    /**
     * Returns a hashcode calculated based on the user's Android ID and
     * username.
     * 
     * @return The hashcode as a String.
     */
    public static String getHash() {
        String user = PreferencesManager.getInstance().getUser();
        int id = PreferencesManager.getInstance().getId().hashCode();
        int temp = (id + id + id * 3 + user.hashCode()) / 42;
        return Integer.toHexString(temp + id);
    }

    /**
     * This method generates a hash hex string from the username and android_id
     * 
     * @param string
     *            The username.
     */
    public static String getHash(String string) {
        int id = PreferencesManager.getInstance().getId().hashCode();
        int temp = (id + id + id * 3 + string.hashCode()) / 42;
        return Integer.toHexString(temp + id);
    }

    /**
     * Generates an ID for a Comment.
     * 
     * @return the ID
     */
    public static long getCommentIdHash() {
        Date date = new Date();
        Random random = new Random();
        random.setSeed(date.getTime());
        return (date.getTime() + random.nextLong());
    }
}
