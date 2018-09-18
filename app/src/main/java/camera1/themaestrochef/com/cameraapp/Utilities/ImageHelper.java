package camera1.themaestrochef.com.cameraapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

import camera1.themaestrochef.com.cameraapp.R;


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


}
