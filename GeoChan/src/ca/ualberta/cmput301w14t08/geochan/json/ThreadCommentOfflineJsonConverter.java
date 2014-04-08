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

package ca.ualberta.cmput301w14t08.geochan.json;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import ca.ualberta.cmput301w14t08.geochan.helpers.GsonHelper;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Handles the serialization of a ThreadComment object into JSON format for
 * offline caching.
 * 
 * @author Artem Chikin
 * @author Artem Herasymchuk
 */
public class ThreadCommentOfflineJsonConverter implements JsonSerializer<ThreadComment>,
        JsonDeserializer<ThreadComment> {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
     * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
     */
    /**
     * Serializes a ThreadComment object into JSON format.
     * 
     * @param thread The ThreadComment to serialize.
     * @param type The Type.
     * @param context The JsonSerializationContext
     * 
     * @return A JsonElement representing the serialized ThreadComment.
     */
    @Override
    public JsonElement serialize(ThreadComment thread, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("title", thread.getTitle());
        object.addProperty("threadDate", thread.getThreadDate().getTime());
        object.addProperty("hasImage", thread.getBodyComment().hasImage());
        object.addProperty("id", thread.getId());
        if (thread.getBodyComment().getLocation() != null) {
            object.addProperty("location", thread.getBodyComment().getLocation().getLatitude()
                    + "," + thread.getBodyComment().getLocation().getLongitude());
            if (thread.getBodyComment().getLocation().getLocationDescription() != null) {
                object.addProperty("locationDescription", thread.getBodyComment().getLocation()
                        .getLocationDescription());
            }
        } else {
            object.addProperty("location", "-999,-999");
        }
        object.addProperty("user", thread.getBodyComment().getUser());
        object.addProperty("hash", thread.getBodyComment().getHash());
        object.addProperty("textPost", thread.getBodyComment().getTextPost());

        if (thread.getBodyComment().hasImage()) {
            Bitmap bitmapThumb = thread.getBodyComment().getImageThumb();

            /*
             * http://stackoverflow.com/questions/9224056/android-bitmap-to-base64
             * -string
             */
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapThumb.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] byteThumbArray = byteArrayOutputStream.toByteArray();
            String encodedThumb = Base64.encodeToString(byteThumbArray, Base64.NO_WRAP);
            object.addProperty("imageThumbnail", encodedThumb);
        }
        recursiveSerialize(object, thread.getBodyComment(), thread.getBodyComment().getChildren());
        
        // Serialize all the images in the thread.
        return object;
    }

    /**
     * Deserializes a ThreadComment object from JSON format.
     * 
     * @param json The JsonElement to deserialize.
     * @param type The Type.
     * @param context The JsonDeserializationContext.
     * 
     * @return The deserialized ThreadComment.
     */
    @Override
    public ThreadComment deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String title = object.get("title").getAsString();
        long threadDate = object.get("threadDate").getAsLong();
        boolean hasImage = object.get("hasImage").getAsBoolean();
        String locationString = object.get("location").getAsString();
        List<String> locationEntries = Arrays.asList(locationString.split(","));
        double latitude = Double.parseDouble(locationEntries.get(0));
        double longitude = Double.parseDouble(locationEntries.get(1));
        String user = object.get("user").getAsString();
        String hash = object.get("hash").getAsString();
        String id = object.get("id").getAsString();
        String textPost = object.get("textPost").getAsString();
        String locationDescription = null;
        if (object.get("locationDescription") != null) {
            locationDescription = object.get("locationDescription").getAsString();
        }
        ArrayList<Comment> topList = new ArrayList<Comment>();
        recursiveDeserialize(object, id, topList);
        Bitmap thumbnail = null;
        if (hasImage) {
            /*
             * http://stackoverflow.com/questions/20594833/convert-byte-array-or-
             * bitmap-to-picture
             */
            String encodedThumb = object.get("imageThumbnail").getAsString();
            byte[] thumbArray = Base64.decode(encodedThumb, Base64.NO_WRAP);
            thumbnail = BitmapFactory.decodeByteArray(thumbArray, 0, thumbArray.length);
        }
        GeoLocation location = new GeoLocation(latitude, longitude);
        location.setLocationDescription(locationDescription);
        final Comment c = new Comment(textPost, null, location, null);
        c.getCommentDate().setTime(threadDate);
        c.setUser(user);
        c.setHash(hash);
        c.setId(Long.parseLong(id));
        c.setChildren(topList);
        if (hasImage) {
            c.setImageThumb(thumbnail);
        }
        final ThreadComment comment = new ThreadComment(c, title);
        comment.setThreadDate(new Date(threadDate));
        comment.setId(Long.parseLong(id));
        return comment;
    }

    private void recursiveSerialize(JsonObject object, Comment parent, ArrayList<Comment> list) {
        object.addProperty(parent.getId(), GsonHelper.getOfflineGson().toJson(list));
        for (Comment comment : list) {
            recursiveSerialize(object, comment, comment.getChildren());
        }
    }

    private void recursiveDeserialize(JsonObject object, String id, ArrayList<Comment> list) {
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(object.get(id).getAsString()).getAsJsonArray();
        for (int i = 0; i < array.size(); ++i) {
            list.add(GsonHelper.getOfflineGson().fromJson(array.get(i), Comment.class));
        }
        for (Comment comment : list) {
            ArrayList<Comment> childList = new ArrayList<Comment>();
            recursiveDeserialize(object, comment.getId(), childList);
            comment.setChildren(childList);
        }
    }
}
