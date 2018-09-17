package camera1.themaestrochef.com.cameraapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;


public class ImageHelper {
    public static void saveImageToFolder(Bitmap source, Context mContext) {
        try {
            File direct = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "IMG_" + System.currentTimeMillis() / 100 + ".png");
            FileOutputStream out = new FileOutputStream(direct);
            source.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Uri x = Uri.fromFile(direct);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, x));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
