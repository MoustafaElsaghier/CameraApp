package camera1.themaestrochef.com.cameraapp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.CapturePhotoUtils;
import camera1.themaestrochef.com.cameraapp.Utilities.ImageHelper;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

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
    boolean isPunchable;

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.ON,
            Flash.AUTO
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
            R.drawable.ic_flash_auto
    };

    private int mCurrentFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        ButterKnife.bind(this);

        //Hide notificationBar
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
                if (mCameraView.getFacing() == Facing.FRONT)
                    bitmap = ImageHelper.flipImage(bitmap);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (PermissionUtilities.checkAndRequestPermissions(MainActivity.this)) {

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

        mCameraView.start();
        if (PermissionUtilities.checkAndRequestPermissions(this)) {
            Bitmap bitmap = ImageHelper.getLastTakenImage(MainActivity.this);
            if (bitmap != null)
                lastImage.setImageBitmap(bitmap);
            else
                lastImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtilities.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
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
