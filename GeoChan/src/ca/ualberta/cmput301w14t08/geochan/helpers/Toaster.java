package ca.ualberta.cmput301w14t08.geochan.helpers;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    
    private Context context;
    private static Toaster instance;
    
    private Toaster(Context context) {
        this.context = context;
    }
    
    public static void generateInstance(Context context) {
        instance = new Toaster(context);
    }
    
    public static void toastShort(String message) {
        Toast.makeText(instance.context, message, Toast.LENGTH_SHORT).show();
    }
    
    public static void toastLong(String message) {
        Toast.makeText(instance.context, message, Toast.LENGTH_LONG).show();
    }

}
