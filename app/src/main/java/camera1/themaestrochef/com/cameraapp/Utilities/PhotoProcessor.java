package camera1.themaestrochef.com.cameraapp.Utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;

import com.otaliastudios.cameraview.CameraUtils;

import camera1.themaestrochef.com.cameraapp.MainActivity;

public class PhotoProcessor extends AsyncTask<Void, Void, String> {
    private byte[] jpeg;
    @SuppressLint("StaticFieldLeak")
    private MainActivity activity;

    public PhotoProcessor(byte[] jpeg, MainActivity activity) {
        this.jpeg = jpeg;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... voids) {
        CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)
                        CapturePhotoUtils.insertImage(activity.getContentResolver(), bitmap, "Captured Image", "Image Description");
                    else
                        CapturePhotoUtils.insertImage(activity.getContentResolver(), bitmap, "Captured Image", "Image Description");
            }
        });
        return "";
    }

//    @Override
//    protected String doInBackground(Byte[]... bytes) {
//
////        OutputStream fos = null;
////         String path;
////        try {
////            if (uri != null) {
////               path =  uri.path;
////            } else {
////                activity.getOutputMediaFile(true, setpath, nameIm);
////            }
////
////            if (path.isEmpty()) {
////                return "";
////            }
////
////            Byte[] data = bytes[0];
////            File photoFile = new File(path);
////            if (activity.needsStupidWritePermissions(path)) {
////                if (activity.config.treeUri.isEmpty()) {
////                    activity.runOnUiThread {
////                        activity.showToast(R.string.save_error_internal_storage.toString());
////                    }
////                    activity.config.savePhotosFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
////                    return "";
////                }
////                var document = activity.getFileDocument(path);
////                document = document ?.createFile("", path.substring(path.lastIndexOf('/') + 1));
////                fos = activity.contentResolver.openOutputStream(document ?.uri);
////            } else {
////                fos = FileOutputStream(photoFile);
////            }
////
////            var image = BitmapFactory.decodeByteArray(data, 0, data.size);
//////            val exif = ExifInterface(photoFile.toString())
////
//////            val deviceRot = deviceOrientation.compensateDeviceRotation(currCameraId)
//////            val previewRot = activity.getPreviewRotation(currCameraId)
//////            var imageRot = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
//////                ExifInterface.ORIENTATION_ROTATE_90 -> 90
//////                ExifInterface.ORIENTATION_ROTATE_180 -> 180
//////                ExifInterface.ORIENTATION_ROTATE_270 -> 270
//////                else -> 0
//////            }
//////
//////            if (previewRot == 0)
//////                imageRot = 180
////
////            val previewRot = activity.getPreviewRotation1(currCameraId);
////            image = rotate(image, (previewRot) % 360) ?:return "";
////
////            //            image = rotate(image, (imageRot + deviceRot + previewRot) % 360) ?: return ""
////            image.compress(Bitmap.CompressFormat.PNG, 75, fos);
////            fos ?.close();
////            return photoFile.absolutePath
////        } catch (e:Exception){
////            e.printStackTrace();
////            Log.e(TAG, "PhotoProcessor file not found: $e";
////        } finally{
////            try {
////                fos ?.close();
////            } catch (e:IOException){
////                e.printStackTrace();
////                Log.e(TAG, "PhotoProcessor close ioexception $e");
////            }
////        }
////
////        return "";
//    }

    private Bitmap rotate(Bitmap bitmap, int degree) {
        if (degree == 0)
            return bitmap;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError ignored) {
        }
        return null;
    }
}
