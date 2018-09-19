package camera1.themaestrochef.com.cameraapp.Activities;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.Adapters.AppImagesAdapter;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilies;
import rx.functions.Action1;

public class ShowAppImages extends AppCompatActivity implements Action1<PermissionsResult> {

    @BindView(R.id.app_images)
    RecyclerView appImages;
    AppImagesAdapter adapter;

    private Cursor externalCursor;
    private Cursor internalCursor;
    private int column_index_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_app_images);
        ButterKnife.bind(this);
        UiUtilies.hideToolBar(this);
        UiUtilies.hideSystemBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call in onResume so that if he deletes image from gallery
        initAppImages();
    }

    private void initAppImages() {
        ArrayList<String> imagesPaths = getAllShownImagesPath();
        adapter = new AppImagesAdapter(this, imagesPaths);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        appImages.setLayoutManager(layoutManager);
        appImages.setAdapter(adapter);
    }

    final ArrayList<String> listOfAllImages = new ArrayList<>();

    String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

    private ArrayList<String> getAllShownImagesPath() {

        PermissionsManager.get().requestStoragePermission().subscribe(this);
        return listOfAllImages;
    }

    private void loadImages() {
        externalCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, orderBy);
        internalCursor = getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                null, null, null, orderBy);
        column_index_data = externalCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (externalCursor.moveToNext()) {
            listOfAllImages.add(externalCursor.getString(column_index_data));
        }
        while (internalCursor.moveToNext()) {
            listOfAllImages.add(internalCursor.getString(column_index_data));
        }
    }

    @Override
    public void call(PermissionsResult permissionsResult) {
        if (permissionsResult.isGranted()) { // always true pre-M
            loadImages();
        }

        if (permissionsResult.hasAskedForPermissions()) {// false if pre-M
            if (!permissionsResult.isGranted()) {
                Toast.makeText(ShowAppImages.this, "Permission Must be Granted", Toast.LENGTH_SHORT).show();
                PermissionsManager.get().requestStoragePermission().subscribe(this);
            } else if (PermissionsManager.get()
                    .neverAskForStorage(ShowAppImages.this)) {
                // go to setting to enable te permission again
                PermissionsManager.get()
                        .intentToAppSettings(ShowAppImages.this);
            }
            loadImages();
        }
    }
}
