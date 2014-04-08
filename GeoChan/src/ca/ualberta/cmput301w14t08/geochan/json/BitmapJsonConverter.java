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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Handles the serialization and deserialization of Bitmap objects
 * to and from base64 JSON strings.
 * 
 * @author Artem Chikin
 * @author Artem Herasymchuk
 * 
 */
public class BitmapJsonConverter implements JsonSerializer<Bitmap>,
		JsonDeserializer<Bitmap> {

	/**
	 * Deserializes a Bitmap from a base64 JSON string.
	 * 
	 * @param jsonElement
	 *            the JSON element to deserialize
	 * @param type
	 *            the Type
	 * @param jsc
	 *            the JSON deserialization context
	 * @return The deserialized Bitmap.
	 * 
	 * @throws JsonParseException
	 */
	@Override
	public Bitmap deserialize(JsonElement jsonElement, Type type,
			JsonDeserializationContext jsc) throws JsonParseException {	
		Bitmap image = null;
		JsonObject object = jsonElement.getAsJsonObject();
		String encodedImage = object.get("image").getAsString();
		byte[] byteArray = Base64.decode(encodedImage, Base64.NO_WRAP);
		
		/*
		 * http://stackoverflow.com/a/5878773
		 * Sando's workaround for running out of memory on decoding bitmaps.
		 */
		BitmapFactory.Options opts = new BitmapFactory.Options();	
		opts.inDither = false;
		opts.inPurgeable = true; 
		opts.inInputShareable = true;	
		opts.inTempStorage = new byte[32 * 1024];	
		image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,
				opts);
		
		return image;
	}

	/**
	 * Serializes a Bitmap to a base64 JSON string.
	 * 
	 * @param bitmap
	 *            the Bitmap to serialize
	 * @param type
	 *            the type
	 * @param jsc
	 *            the JSON serialization context
	 *            
	 * @return a JsonElement representing the serialized Bitmap.
	 */
	@Override
	public JsonElement serialize(Bitmap bitmap, Type type,
			JsonSerializationContext jsc) {
		JsonObject object = new JsonObject();
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);	
		object.addProperty("image", encoded);
		
		return object;	
	}
}
