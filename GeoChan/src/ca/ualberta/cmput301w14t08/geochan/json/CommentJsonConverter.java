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
import java.util.Arrays;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;
import ca.ualberta.cmput301w14t08.geochan.models.GeoLocation;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Handles the serialization and deserialization of Comment objects 
 * to and from JSON format.
 * 
 * @author Artem Herasymchuk
 * 
 */
public class CommentJsonConverter implements JsonSerializer<Comment>,
		JsonDeserializer<Comment> {

	/**
	 * Serializes a Comment object into JSON format.
	 * 
	 * @param comment
	 *            the Comment to serialize
	 * @param type
	 *            the Type
	 * @param context
	 *            the JSON serialization context
	 *            
	 * @return A JsonElement representing the serialized Comment.
	 */
	@Override
	public JsonElement serialize(Comment comment, Type type,
			JsonSerializationContext context) {

		JsonObject object = new JsonObject();

		object.addProperty("commentDate", comment.getCommentDate().getTime());

		if (comment.getLocation() != null) {
			object.addProperty("location", comment.getLocation().getLatitude()
					+ "," + comment.getLocation().getLongitude());
			if (comment.getLocation().getLocationDescription() != null) {
				object.addProperty("locationDescription", comment.getLocation()
						.getLocationDescription());
			}
		} else {
			object.addProperty("location", "-999,-999");
		}

		object.addProperty("user", comment.getUser());
		object.addProperty("hash", comment.getHash());
		object.addProperty("id", comment.getId());

		object.addProperty("textPost", comment.getTextPost());

		object.addProperty("hasImage", comment.hasImage());
		if (comment.hasImage()) {
			/*
			 * http://stackoverflow.com/questions/9224056/android-bitmap-to-base64
			 * -string
			 * 
			 * Serialize just the thumbnail as the image is serialized
			 * separately
			 */
			Bitmap bitmapThumb = comment.getImageThumb();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmapThumb.compress(Bitmap.CompressFormat.JPEG, 90,
					byteArrayOutputStream);
			byte[] byteThumbArray = byteArrayOutputStream.toByteArray();
			String encodedThumb = Base64.encodeToString(byteThumbArray,
					Base64.NO_WRAP);
			object.addProperty("imageThumbnail", encodedThumb);
		}

		object.addProperty("depth", comment.getDepth());
		if (comment.getParent() != null) {
			object.addProperty("parent", comment.getParent().getId());
		}

		return object;
	}

	/**
	 * Deserializes a Comment object from JSON format.
	 * @param json
	 *            the JSON element to deserialize
	 * @param type
	 *            the Type
	 * @param context
	 *            the JSON deserialization context
	 *            
	 * @return The deserialized Comment.
	 * 
	 * @throws JsonParseException
	 */
	@Override
	public Comment deserialize(JsonElement json, Type type,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		
		long commentDate = object.get("commentDate").getAsLong();
		
		String locationString = object.get("location").getAsString();
		List<String> locationEntries = Arrays.asList(locationString.split(","));
		double latitude = Double.parseDouble(locationEntries.get(0));
		double longitude = Double.parseDouble(locationEntries.get(1));
		String locationDescription = null;
		if (object.get("locationDescription") != null) {
			locationDescription = object.get("locationDescription")
					.getAsString();
		}
		GeoLocation location = new GeoLocation(latitude, longitude);
		location.setLocationDescription(locationDescription);
		
		String user = object.get("user").getAsString();
		String hash = object.get("hash").getAsString();
		String id = object.get("id").getAsString();
		
		String textPost = object.get("textPost").getAsString();
		
		Bitmap thumbnail = null;
		boolean hasImage = object.get("hasImage").getAsBoolean();
		if (hasImage) {
			/*
			 * http://stackoverflow.com/questions/20594833/convert-byte-array-or-
			 * bitmap-to-picture
			 *
			 * http://stackoverflow.com/a/5878773
			 * Sando's workaround for running out of memory on decoding bitmaps.
			 * 
			 * Only deserialize the thumbnail as the full image is downloaded
			 * and deserialized separately.
			 */
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inDither = false;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			opts.inTempStorage = new byte[32 * 1024];
			String encodedThumb = object.get("imageThumbnail").getAsString();
			byte[] thumbArray = Base64.decode(encodedThumb, Base64.NO_WRAP);
			thumbnail = BitmapFactory.decodeByteArray(thumbArray, 0,
					thumbArray.length, opts);
		}
		
		int depth = object.get("depth").getAsInt();
		
		final Comment comment = new Comment(textPost, null, location, null);
		comment.getCommentDate().setTime(commentDate);
		comment.setUser(user);
		comment.setHash(hash);
		comment.setDepth(depth);
		comment.setId(Long.parseLong(id));
		if (hasImage) {
			comment.setImageThumb(thumbnail);
		}
		
		return comment;
	}

}
