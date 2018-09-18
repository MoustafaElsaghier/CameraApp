package camera1.themaestrochef.com.cameraapp.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.Adapters.VideoAdapter;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.Model_Video;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;
import rx.functions.Action1;


public class ShowAppVideos extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    @BindView(R.id.app_videos)
    RecyclerView appVideo;
    VideoAdapter adapter;

    ArrayList al_video = new ArrayList<Model_Video>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_app_videos);
        ButterKnife.bind(this);
        UiUtilies.hideToolBar(this);
        UiUtilies.hideSystemBar(this);
        init();
    }

    private void init() {

        RecyclerView.LayoutManager recyclerViewLayoutManager
                = new GridLayoutManager(getApplicationContext(), 4);
        appVideo.setLayoutManager(recyclerViewLayoutManager);

        fn_checkpermission();

    }

    private void fn_checkpermission() {
        /*RUN TIME PERMISSIONS*/

        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(ShowAppVideos.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(ShowAppVideos.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(ShowAppVideos.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            Log.e("Else", "Else");
            fn_video();
        }
    }



    public void fn_video() {




        PermissionsManager.get().requestStoragePermission().subscribe(new Action1<PermissionsResult>() {

            @Override
            public void call(PermissionsResult permissionsResult) {

                // replace order by with null to get them reversed order
                String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

                if (permissionsResult.isGranted()) { // always true pre-M

                }

                if (permissionsResult.hasAskedForPermissions()) { // false if pre-M

                }
            }
        });


        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name, column_id, thum;

        String absolutePathOfImage = null;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.e("Column", absolutePathOfImage);
            Log.e("Folder", cursor.getString(column_index_folder_name));
            Log.e("column_id", cursor.getString(column_id));
            Log.e("thum", cursor.getString(thum));

            Model_Video obj_model = new Model_Video();
            obj_model.setBoolean_selected(false);
            obj_model.setStr_path(absolutePathOfImage);
            obj_model.setStr_thumb(cursor.getString(thum));

            al_video.add(obj_model);

        }


        adapter = new VideoAdapter(this, al_video);
        appVideo.setAdapter(adapter);

    }

}
