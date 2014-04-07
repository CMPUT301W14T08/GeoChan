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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.os.Environment;

/**
 * Creates a file to store an image and returns it.
 * 
 * @author Tom
 */
public class ImageHelper {

    public static final int REQUEST_GALLERY = 101;
    public static final int REQUEST_CAMERA = 102;

    public static File createImageFile() throws IOException {
        Locale locale = Locale.getDefault();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", locale).format(new Date());
        String imageFileName = "geochan_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File photo = File.createTempFile(imageFileName, ".jpg", storageDir);
        return photo;
    }

}