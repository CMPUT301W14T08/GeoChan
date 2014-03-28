package ca.ualberta.cmput301w14t08.geochan.test;

import java.io.File;
import java.io.IOException;

import ca.ualberta.cmput301w14t08.geochan.activities.MainActivity;
import ca.ualberta.cmput301w14t08.geochan.helpers.ImageHelper;
import android.test.ActivityInstrumentationTestCase2;

public class ImageHelperTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ImageHelperTest() {
        super(MainActivity.class);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testConstruction() {
        ImageHelper imageHelper = new ImageHelper();
        assertNotNull(imageHelper);
    }

    public void testFileCreation() {
        try {
            File file = ImageHelper.createImageFile();
            assertNotNull(file);
        } catch (IOException e) {
            fail("IO Exception");
        }
    }
    
    public void testUniqueFileNames() {
        try {
            File file = ImageHelper.createImageFile();
            try {
                //Image file names are unique when generated > 1 second apart
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            File file2 = ImageHelper.createImageFile();
            String filename1 = file.getName();
            String filename2 = file2.getName();
            
            assertFalse(filename1.equals(filename2));
        } catch (IOException e) {
            // Ignore
        }
    }
    
}