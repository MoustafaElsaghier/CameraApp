package camera1.themaestrochef.com.cameraapp.Utilities;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import camera1.themaestrochef.com.cameraapp.Activities.MainActivity;

import static camera1.themaestrochef.com.cameraapp.Utilities.Constants.REQUEST_CAMERA_PERMISSION;


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
//            Uri x = Uri.fromFile(direct);
//            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, x));

            MediaScannerConnection.scanFile(mContext,
                    new String[]{direct.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getLastTakenImage(Context context) {
        // Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

// Put it in the image view
        if (cursor != null && cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                return BitmapFactory.decodeFile(imageLocation);
            }
        }
        return null;
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmapImage) {
//        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory =
                new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/VID_" + System.currentTimeMillis() / 1000 + "_.png");
//        cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(directory);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

}
