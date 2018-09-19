package camera1.themaestrochef.com.cameraapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import camera1.themaestrochef.com.cameraapp.Activities.MainActivity;
import camera1.themaestrochef.com.cameraapp.Activities.ShowAppVideos;
import camera1.themaestrochef.com.cameraapp.Dialogs.ConfirmationDialogFragment;
import camera1.themaestrochef.com.cameraapp.Utilities.Model_Video;
import camera1.themaestrochef.com.cameraapp.Utilities.SharedPreferencesUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;

public class CaptureVideo extends AppCompatActivity {

    @BindView(R.id.camera)
    CameraView mCameraView;

    @BindView(R.id.switch_flash)
    ImageView flashIcon;

    @BindView(R.id.pinch_image)
    ImageView pinchIcon;

    @BindView(R.id.last_captured_video)
    ImageView lastImage;

    @BindView(R.id.pause_video)
    ImageView pauseVideo;

    @BindView(R.id.take_video)
    ImageView takeVideo;


    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_WRITE_STORAGE_PERMISSION = 2;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 3;
    private static final int REQUEST_USE_MICROPHONE_PERMISSION = 4;


    ArrayList al_video = new ArrayList<Model_Video>();

    private static final String FRAGMENT_DIALOG = "dialog";

    private Handler mBackgroundHandler;

    private static final Flash[] FLASH_OPTIONS = {
            Flash.OFF,
            Flash.ON
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    private int mCurrentFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_video);
        ButterKnife.bind(this);
        ActivityCompat.requestPermissions(MainActivity.activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE_PERMISSION);
        //Hide notificationBar
        UiUtilies.hideSystemBar(this);
        UiUtilies.hideToolBar(this);
        initIcons();
        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onVideoTaken(final File video) {
                    super.onVideoTaken(video);
                    Uri x = Uri.fromFile(video);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, x));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                final Model_Video modelVideo = fn_video();

                                if (modelVideo != null)
                                    lastImage.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Glide.with(CaptureVideo.this).load(modelVideo.getStr_thumb()).into(lastImage);
                                        }
                                    });
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }).start();

                }
            });
        }

    }

    private void initIcons() {
        mCurrentFlash = SharedPreferencesUtilities.getFlashIndex(this) % 2;
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
            case REQUEST_USE_MICROPHONE_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.camera_permission_not_granted,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Model_Video modelVideo = fn_video();
            if (modelVideo != null)
                Glide.with(this).load(modelVideo.getStr_thumb()).into(lastImage);
        }
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
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    public Model_Video fn_video() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        if (cursor.moveToFirst()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));

            return obj_model;

        }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }


    @OnClick(R.id.take_video)
    public void captureVideo() {
        takeVideo.setVisibility(View.INVISIBLE);
        pauseVideo.setVisibility(View.VISIBLE);

        if (mCameraView != null) {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/VID_" + System.currentTimeMillis() / 1000 + "_.mp4");
            f.setWritable(true);
            f.setReadable(true);
            mCameraView.startCapturingVideo(f);
        }
    }

    @OnClick(R.id.pause_video)
    public void stopVideo() {
        takeVideo.setVisibility(View.VISIBLE);
        pauseVideo.setVisibility(View.INVISIBLE);
        if (mCameraView != null) {
            mCameraView.stopCapturingVideo();

        }

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
        } else {
            mCameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
            pinchIcon.setImageResource(android.R.drawable.star_big_on);
            isPunchable = true;
        }
        SharedPreferencesUtilities.setPinch(this, isPunchable);
    }

    @OnClick(R.id.last_captured_video)
    public void showImages() {
        Intent intent = new Intent(this, ShowAppVideos.class);
        startActivity(intent);
    }

    @OnClick(R.id.imageView)
    public void openCamera(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
