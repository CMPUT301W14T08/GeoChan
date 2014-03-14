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

package ca.ualberta.cmput301w14t08.geochan.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ca.ualberta.cmput301w14t08.geochan.elasticsearch.ElasticSearchClient;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class ThreadCommentDeserializer implements JsonDeserializer<ThreadComment> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement,
     * java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
     */
    @Override
    public ThreadComment deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        
        String title = object.get("title").getAsString();
        long threadDate = object.get("threadDate").getAsLong();
        boolean hasImage = object.get("hasImage").getAsBoolean();
        String locationString = object.get("location").getAsString();
        String user = object.get("user").getAsString();
        String hash = object.get("hash").getAsString();
        String id = object.get("id").getAsString();
        String textPost = object.get("textPost").getAsString();
        String jsonComments = object.get("comments").getAsString();
        
        Gson gson = ElasticSearchClient.getInstance().getGson();
        Type t = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> comments = gson.fromJson(jsonComments, t);
        if (comments == null) {
            comments = new ArrayList<String>();
        }
        
        if (hasImage) {
            // TODO: Implement decoding of images
        }
        
        List<String> locationEntries = Arrays.asList(locationString.split(","));
        double latitude = Double.parseDouble(locationEntries.get(0));
        double longitude = Double.parseDouble(locationEntries.get(1));
        GeoLocation location = new GeoLocation(latitude, longitude);
        
        final Comment c = new Comment(textPost, location);
        c.getCommentDate().setTime(threadDate);
        c.setUser(user);
        c.setHash(hash);
        c.setId(Long.parseLong(id));
        c.setCommentIds(comments);

        final ThreadComment comment = new ThreadComment(c, title);
        comment.setThreadDate(new Date(threadDate));
        comment.setId(Long.parseLong(id));
        // TODO: Set image
        
        return comment;
    }
}
