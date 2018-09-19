package camera1.themaestrochef.com.cameraapp.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.CaptureVideo;
import camera1.themaestrochef.com.cameraapp.Dialogs.ConfirmationDialogFragment;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.CapturePhotoUtils;
import camera1.themaestrochef.com.cameraapp.Utilities.ImageHelper;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;
import rx.functions.Action1;

import static camera1.themaestrochef.com.cameraapp.Utilities.Constants.REQUEST_CAMERA_PERMISSION;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView mCameraView;

    @BindView(R.id.switch_flash)
    ImageView flashIcon;

    @BindView(R.id.pinch_image)
    ImageView pinchIcon;

    @BindView(R.id.last_captured_image)
    ImageView lastImage;

    public static Activity activity;

    public static final String FRAGMENT_DIALOG = "dialog";

    private static final Flash[] FLASH_OPTIONS = {
            Flash.AUTO,
            Flash.OFF,
            Flash.ON
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private int mCurrentFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ButterKnife.bind(this);

        //Hide notificationBar
        UiUtilies.hideSystemBar(this);
        UiUtilies.hideToolBar(this);
        initIcons();

        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(final byte[] jpeg) {
                    super.onPictureTaken(jpeg);
                    saveImg(jpeg);
                }
            });
        }
    }

    private void initIcons() {
        mCurrentFlash = SharedPreferencesUtilities.getFlashIndex(this);
        flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
        mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);

        isPunchable = SharedPreferencesUtilities.getPinchValue(this);
        if (mCameraView != null) {
            if (isPunchable) {
                mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM);
                pinchIcon.setImageResource(android.R.drawable.star_big_on);
            } else {
                mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE);
                pinchIcon.setImageResource(android.R.drawable.star_big_off);
            }
        }
    }

    private void saveImg(final byte[] jpeg) {
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                String path = ImageHelper.saveToInternalStorage(MainActivity.this, bitmap);
////                Glide.with(MainActivity.this).load(path).into(lastImage);
////                        String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
////                        Glide.with(MainActivity.this).load(imgPath).into(lastImage);
//                    } else {
//                String path = ImageHelper.saveToInternalStorage(MainActivity.this, bitmap);
//                Glide.with(MainActivity.this).load(path).into(lastImage);
//                        String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
////                        Glide.with(MainActivity.this).load(imgPath).into(lastImage);
//                    }
//            }
//        }, 100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {

                        //                        String path = ImageHelper.saveToInternalStorage(MainActivity.this, bitmap);
                        //                Glide.with(MainActivity.this).load(path).into(lastImage);
                        final String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
                        if (imgPath != null)
                            lastImage.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(MainActivity.this).load(imgPath).into(lastImage);
                                }
                            });
                    }
                } else {
                    final String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
                    if (imgPath != null)
                        lastImage.post(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(MainActivity.this).load(imgPath).into(lastImage);
                            }
                        });
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PermissionsManager.get().requestStoragePermission().subscribe(new Action1<PermissionsResult>() {

            @Override
            public void call(PermissionsResult permissionsResult) {

                // replace order by with null to get them reversed order
                String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

                if (permissionsResult.isGranted()) { // always true pre-M
                    Bitmap bitmap = ImageHelper.getLastTakenImage(MainActivity.this);
                    if (bitmap != null)
                        lastImage.setImageBitmap(bitmap);
                    else
                        lastImage.setVisibility(View.GONE);
                }

                if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
                    if (permissionsResult.isGranted()) {
                        Bitmap bitmap = ImageHelper.getLastTakenImage(MainActivity.this);
                        if (bitmap != null)
                            lastImage.setImageBitmap(bitmap);
                        else
                            lastImage.setVisibility(View.GONE);
                    } else {
                        PermissionsManager.get().requestStoragePermission().subscribe(this);
                    }
                }
            }
        });


        //get last captured image
//        if (ContextCompat.checkSelfPermission
//                (this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Bitmap bitmap = ImageHelper.getLastTakenImage(this);
//            if (bitmap != null)
//                lastImage.setImageBitmap(bitmap);
//            else
//                lastImage.setVisibility(View.GONE);
//        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_GRANTED) {
        mCameraView.start();
//        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                Manifest.permission.CAMERA)) {
//            ConfirmationDialogFragment
//                    .newInstance(R.string.camera_permission_confirmation,
//                            new String[]{Manifest.permission.CAMERA,
//                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                    Manifest.permission.READ_EXTERNAL_STORAGE},
//                            REQUEST_CAMERA_PERMISSION,
//                            R.string.camera_permission_not_granted)
//                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
//                    REQUEST_CAMERA_PERMISSION);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    long bb;

    @OnClick(R.id.take_picture)
    public void capturePic() {
        bb = System.currentTimeMillis();
        if (mCameraView != null)
            mCameraView.capturePicture();
    }

    @OnClick(R.id.switch_flash)
    public void switchFlash() {
        if (mCameraView != null) {
            mCurrentFlash = (mCurrentFlash + 1) % FLASH_OPTIONS.length;
            flashIcon.setImageResource(FLASH_ICONS[mCurrentFlash]);
            mCameraView.setFlash(FLASH_OPTIONS[mCurrentFlash]);
            SharedPreferencesUtilities.setFlash(this, mCurrentFlash);
        }
    }

    @OnClick(R.id.switch_camera)
    public void switchCamera() {
        mCameraView.toggleFacing();
    }

    boolean isPunchable;

    @OnClick(R.id.pinch_image)
    public void switchPinch() {
        if (isPunchable) {
            pinchIcon.setImageResource(android.R.drawable.star_big_off);
            mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE); // Pinch to zoom!
            isPunchable = false;
        } else {
            pinchIcon.setImageResource(android.R.drawable.star_big_on);
            mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
            isPunchable = true;
        }
        SharedPreferencesUtilities.setPinch(this, isPunchable);
    }

    @OnClick(R.id.last_captured_image)
    public void showImages() {
        Intent intent = new Intent(this, ShowAppImages.class);
        startActivity(intent);
    }

    @OnClick(R.id.imageView)
    public void openVideo() {
        Intent intent = new Intent(this, CaptureVideo.class);
        startActivity(intent);
        finish();
    }
}
