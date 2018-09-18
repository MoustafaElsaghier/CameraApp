package camera1.themaestrochef.com.cameraapp.Activiteis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.Dialogs.ConfirmationDialogFragment;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.CapturePhotoUtils;
import camera1.themaestrochef.com.cameraapp.Utilities.ImageHelper;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView mCameraView;

    @BindView(R.id.switch_flash)
    ImageView flashIcon;

    @BindView(R.id.pinch_image)
    ImageView pinchIcon;

    @BindView(R.id.last_captured_image)
    ImageView lastImage;

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 2;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 3;

    private static final String FRAGMENT_DIALOG = "dialog";

    private Handler mBackgroundHandler;

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
        ButterKnife.bind(this);

        //Hide notificationBar
        UiUtilies.hideSystemBar(this);

        initIcons();

        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(final byte[] jpeg) {
                    super.onPictureTaken(jpeg);
//                    saveImg(jpeg);
//                    new PhotoProcessor(jpeg, MainActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            saveImg(jpeg);
                        }
                    }).start();
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
        final Handler handler = new Handler();
        CameraUtils.decodeBitmap(jpeg, new CameraUtils.BitmapCallback() {
            @Override
            public void onBitmapReady(final Bitmap bitmap) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
                                Glide.with(MainActivity.this).load(imgPath).into(lastImage);
                            } else {
                                String imgPath = CapturePhotoUtils.insertImage(getContentResolver(), bitmap, "Captured Image", "Image Description");
                                Glide.with(MainActivity.this).load(imgPath).into(lastImage);
                            }
                    }
                }, 20);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //get last captured image
        Bitmap bitmap = ImageHelper.getLastTakenImage(this);
        if (bitmap != null)
            lastImage.setImageBitmap(bitmap);
        else
            lastImage.setVisibility(View.GONE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mCameraView.start();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ConfirmationDialogFragment
                    .newInstance(R.string.camera_permission_confirmation,
                            new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CAMERA_PERMISSION,
                            R.string.camera_permission_not_granted)
                    .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBackgroundHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundHandler.getLooper().quitSafely();
            } else {
                mBackgroundHandler.getLooper().quit();
            }
            mBackgroundHandler = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                // No need to start camera here; it is handled by onResume
                break;
            case REQUEST_WRITE_STORAGE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_READ_STORAGE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;


        }
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @OnClick(R.id.take_picture)
    public void capturePic() {
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
        if (mCameraView != null) {
            Facing facing = mCameraView.getFacing();
//            if (facing == Facing.BACK) {
//                mCameraView.setFacing(Facing.FRONT);
//                mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE); // Pinch to zoom!
//            } else {
//                mCameraView.setFacing(Facing.BACK);
//                mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
//            }
            mCameraView.setFacing(facing == Facing.FRONT ?
                    Facing.BACK : Facing.FRONT);
        }
    }

    boolean isPunchable;

    @OnClick(R.id.pinch_image)
    public void switchPinch() {
        if (isPunchable) {
            pinchIcon.setImageResource(android.R.drawable.star_big_off);

            mCameraView.mapGesture(Gesture.PINCH, GestureAction.NONE); // Pinch to zoom!
            isPunchable = false;
            SharedPreferencesUtilities.setPinch(this, isPunchable);
        } else {
            mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
            pinchIcon.setImageResource(android.R.drawable.star_big_on);
            isPunchable = true;
            SharedPreferencesUtilities.setPinch(this, isPunchable);
        }
    }

    @OnClick(R.id.last_captured_image)
    public void showImages() {
        Intent intent = new Intent(this, ShowAppImages.class);
        startActivity(intent);
    }
}
