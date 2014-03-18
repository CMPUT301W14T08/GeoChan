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
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.util.Base64;
import ca.ualberta.cmput301w14t08.geochan.models.Comment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Handles the serialization of Comment objects into JSON format.
 * 
 */
public class CommentSerializer implements JsonSerializer<Comment> {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.JsonSerializer#serialize(java.lang.Object,
     * java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
     */
    /**
     * Serializes a Comment object into JSON format.
     */
    @Override
    public JsonElement serialize(Comment comment, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("commentDate", comment.getCommentDate().getTime());
        object.addProperty("hasImage", comment.hasImage());
        if (comment.getLocation() != null) {
            object.addProperty("location", comment.getLocation().getLatitude() + ","
                    + comment.getLocation().getLongitude());
        } else {
            object.addProperty("location", "-999,-999");
        }
        object.addProperty("user", comment.getUser());
        object.addProperty("hash", comment.getHash());
        object.addProperty("id", comment.getId());
        object.addProperty("textPost", comment.getTextPost());
        if (comment.hasImage()) {
            Picture picture = comment.getImage();
            Picture pictureThumb = comment.getImageThumb();
            /*
             * http://stackoverflow.com/questions/15563021/how-to-convert-a-picture-object-into-a-bitmap-object-android
             */
            PictureDrawable drawable = new PictureDrawable(picture);
            PictureDrawable drawableThumb = new PictureDrawable(pictureThumb);
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
            Bitmap bitmapThumb = Bitmap.createBitmap(drawableThumb.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);
            Canvas c = new Canvas(bitmap);
            Canvas cT = new Canvas(bitmapThumb);
            picture.draw(c);
            pictureThumb.draw(cT);
            /*
             * http://stackoverflow.com/questions/9224056/android-bitmap-to-base64
             * -string
             */
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ByteArrayOutputStream streamThumb = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            bitmapThumb.compress(Bitmap.CompressFormat.JPEG, 90, streamThumb);
            byte[] byteArray = stream.toByteArray();
            byte[] byteArrayThumb = streamThumb.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            String encodedThumb = Base64.encodeToString(byteArrayThumb, Base64.NO_WRAP);
            object.addProperty("image", encoded);
            object.addProperty("imageThumbnail", encodedThumb);
        }
        object.addProperty("depth", comment.getDepth());
        if (comment.getParent() != null) {
            object.addProperty("parent", comment.getParent().getId());
        }
        return object;
    }

}
