package camera1.themaestrochef.com.cameraapp.Utilities;

import android.app.Activity;
import android.view.WindowManager;

public class UiUtilies {
    public static void hideSystemBar(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
