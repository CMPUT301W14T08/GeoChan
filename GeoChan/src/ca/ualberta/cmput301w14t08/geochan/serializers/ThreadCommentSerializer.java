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

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.util.Base64;
import ca.ualberta.cmput301w14t08.geochan.models.ThreadComment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ThreadCommentSerializer implements JsonSerializer<ThreadComment> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
	 * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	@Override
	public JsonElement serialize(ThreadComment thread, Type type,
			JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("title", thread.getTitle());
		object.addProperty("threadDate", thread.getThreadDate().getTime());
		object.addProperty("hasImage", thread.getBodyComment().hasImage());
		object.addProperty("id", thread.getId());
		if (thread.getBodyComment().getLocation() != null) {
			object.addProperty("location", thread.getBodyComment()
					.getLocation().getLatitude()
					+ ","
					+ thread.getBodyComment().getLocation().getLongitude());
		} else {
			object.addProperty("location", "-999,-999");
		}
		object.addProperty("user", thread.getBodyComment().getUser());
		object.addProperty("hash", thread.getBodyComment().getHash());
		object.addProperty("textPost", thread.getBodyComment().getTextPost());
		if (thread.getBodyComment().hasImage()) {
			Picture picture = thread.getBodyComment().getImage();
			Bitmap bitmap = Bitmap.createBitmap(picture.getWidth(),
					picture.getHeight(), Bitmap.Config.RGB_565);
			/*
			 * http://stackoverflow.com/questions/9224056/android-bitmap-to-base64
			 * -string
			 */
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90,
					byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
			object.addProperty("image", encoded);
			object.addProperty("imageThumbnail", encoded);
		}
		return object;
	}

}
