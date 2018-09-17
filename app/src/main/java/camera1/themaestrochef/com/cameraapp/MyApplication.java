package camera1.themaestrochef.com.cameraapp;

import android.app.Application;

import net.ralphpina.permissionsmanager.PermissionsManager;

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        PermissionsManager.init(this);
    }
}
