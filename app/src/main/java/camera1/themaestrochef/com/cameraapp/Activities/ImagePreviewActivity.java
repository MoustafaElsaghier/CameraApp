package camera1.themaestrochef.com.cameraapp.Activities;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.Adapters.ViewPageAdapter;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.AdsUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.PermissionUtilities;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class ImagePreviewActivity extends AppCompatActivity {

//    @BindView(R.id.app_image)
//    ImageView imageView;

    @BindView(R.id.imageViewer)
    ViewPager pager;

    ViewPageAdapter adapter;

    @BindView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
        ButterKnife.bind(this);

        String mPath = getIntent().getStringExtra("imagePath");
//        Glide.with(this).load(mPath).into(imageView);
        AdsUtilities.initAds(mAdView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> imagesPaths = getAllShownImagesPath();
        adapter = new ViewPageAdapter(imagesPaths, this);
        pager.setAdapter(adapter);

    }

    final ArrayList<String> listOfAllImages = new ArrayList<>();

    // for getting images in order of newer ones at front of gallery.
    String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

    private ArrayList<String> getAllShownImagesPath() {
        if (PermissionUtilities.checkAndRequestPermissions(this))
            loadImages();
        return listOfAllImages;
    }


    private void loadImages() {
        Cursor externalCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, orderBy);
        Cursor internalCursor = getContentResolver().query(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                null, null, null, orderBy);
        int column_index_data = externalCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (externalCursor.moveToNext()) {
            listOfAllImages.add(externalCursor.getString(column_index_data));
        }
        while (internalCursor.moveToNext()) {
            listOfAllImages.add(internalCursor.getString(column_index_data));
        }
    }

}
