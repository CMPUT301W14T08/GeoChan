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

import android.graphics.Bitmap;
import android.location.Location;
import ca.ualberta.cmput301w14t08.geochan.json.BitmapJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.CommentJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.CommentOfflineJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.LocationJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.ThreadCommentJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.json.ThreadCommentOfflineJsonConverter;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Genereates and returns Gson objects with the required type converters set.
 * 
 * @author Artem Herasymchuk
 */
public class GsonHelper {

    private static Gson offlineGson = null;
    private static Gson onlineGson = null;
    private static Gson exposeGson = null;
    private static GsonHelper instance = null;

    private GsonHelper() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentJsonConverter());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentJsonConverter());
        builder.registerTypeAdapter(Bitmap.class, new BitmapJsonConverter());
        builder.registerTypeAdapter(Location.class, new LocationJsonConverter());
        onlineGson = builder.create();
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Comment.class, new CommentOfflineJsonConverter());
        builder.registerTypeAdapter(ThreadComment.class, new ThreadCommentOfflineJsonConverter());
        builder.registerTypeAdapter(Location.class, new LocationJsonConverter());
        offlineGson = builder.create();
        builder = new GsonBuilder();
        exposeGson = builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    public static GsonHelper getInstance() {
        if (instance == null) {
            instance = new GsonHelper();
        }
        return instance;
    }

    public static Gson getOnlineGson() {
        if (onlineGson == null) {
            getInstance();
        }
        return onlineGson;
    }

    public static Gson getOfflineGson() {
        if (offlineGson == null) {
            getInstance();
        }
        return offlineGson;
    }

    public static Gson getExposeGson() {
        if (exposeGson == null) {
            getInstance();
        }
        return exposeGson;
    }

}
