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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class ImageHelper {
    
    private Activity activity;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    
    public ImageHelper(Activity activity) {
        this.activity = activity;  ;
    }

    public void captureImage() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(activity.getApplicationContext());
        myAlertDialog.setTitle("Upload Pictures");
        myAlertDialog.setMessage("How do you want to set your picture?");

        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        
                        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/geochan";
                        File folderF = new File(folder);
                        if (!folderF.exists()) {
                            folderF.mkdir();
                        }
                        String imageFilePath = folder + "/" + String.valueOf(System.currentTimeMillis()) + "jpg";
                        File imageFile = new File(imageFilePath);
                        fileUri = Uri.fromFile(imageFile);
                        
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                        activity.startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                        
                        //Add new picture to android gallery
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(fileUri);
                        activity.sendBroadcast(mediaScanIntent);

                    }
                });

        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        

                    }
                });
        myAlertDialog.show();
    }

    
}
