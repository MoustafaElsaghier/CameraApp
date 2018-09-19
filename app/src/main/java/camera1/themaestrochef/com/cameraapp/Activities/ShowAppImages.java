package camera1.themaestrochef.com.cameraapp.Activities;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.Adapters.AppImagesAdapter;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.AdsUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class ShowAppImages extends AppCompatActivity {

    @BindView(R.id.app_images)
    RecyclerView appImages;
    AppImagesAdapter adapter;

    @BindView(R.id.adView)
    AdView mAdView;

    private Cursor externalCursor;
    private Cursor internalCursor;
    private int column_index_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_app_images);
        ButterKnife.bind(this);
        UiUtilise.hideToolBar(this);
        UiUtilise.hideSystemBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // call in onResume so that if he deletes image from gallery
        initAppImages();
        AdsUtilities.initAds(mAdView);

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

        if (PermissionUtilities.checkAndRequestPermissions(this))
            loadImages();
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
    protected void onStop() {
        super.onStop();
        mAdView.setVisibility(View.GONE);
    }
}
