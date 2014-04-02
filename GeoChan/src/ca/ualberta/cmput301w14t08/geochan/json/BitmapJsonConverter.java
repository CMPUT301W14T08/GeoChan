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

public class BitmapJsonConverter implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {

    @Override
    public Bitmap deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsc)
            throws JsonParseException {
        Bitmap image = null;
        JsonObject object = jsonElement.getAsJsonObject();
        String encodedImage = object.get("image").getAsString();
        byte[] byteArray = Base64.decode(encodedImage, Base64.NO_WRAP);
        // http://stackoverflow.com/a/5878773
        // Sando's workaround for running out of memory on decoding bitmaps.
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
                                 // memory, the Bitmap can be cleared
        opts.inInputShareable = true; // Which kind of reference will be
                                      // used to recover the Bitmap data
                                      // after being clear, when it will be
                                      // used in the future
        opts.inTempStorage = new byte[32 * 1024];
        image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
        return image;
    }

    @Override
    public JsonElement serialize(Bitmap bitmap, Type type, JsonSerializationContext jsc) {
        JsonObject object = new JsonObject();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        object.addProperty("image", encoded);
        return object;
    }
}
