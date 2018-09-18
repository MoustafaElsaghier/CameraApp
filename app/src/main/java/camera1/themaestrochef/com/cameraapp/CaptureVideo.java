package camera1.themaestrochef.com.cameraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.Dialogs.ConfirmationDialogFragment;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;

public class CaptureVideo extends AppCompatActivity {

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
    private static final int REQUEST_USE_MICROPHONE_PERMISSION = 4;


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
        //Hide notificationBar
        UiUtilies.hideSystemBar(this);
        if (mCameraView != null) {
            mCameraView.addCameraListener(new CameraListener() {
                @Override
                public void onVideoTaken(final File video) {
                    super.onVideoTaken(video);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //saveVideo(video);
                        }
                    }).start();
                }
            });
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraView.destroy();
    }
}
