package camera1.themaestrochef.com.cameraapp;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.ralphpina.permissionsmanager.PermissionsManager;
import net.ralphpina.permissionsmanager.PermissionsResult;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class ShowAppImages extends AppCompatActivity {

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
        initAppImages();
    }

    private void initAppImages() {
        ArrayList<String> imagesPaths = getAllShownImagesPath();
        adapter = new AppImagesAdapter(this, imagesPaths);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        appImages.setLayoutManager(layoutManager);
        appImages.setAdapter(adapter);
    }

    private ArrayList<String> getAllShownImagesPath() {
        final ArrayList<String> listOfAllImages = new ArrayList<>();

        PermissionsManager.get().requestStoragePermission().subscribe(new Action1<PermissionsResult>() {

            @Override
            public void call(PermissionsResult permissionsResult) {

                // replace order by with null to get them reversed order
                String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

                if (permissionsResult.isGranted()) { // always true pre-M
                    externalCursor = getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null, null, null, orderBy);

                    internalCursor = getContentResolver().query(
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            orderBy);

                    column_index_data = externalCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

                    while (externalCursor.moveToNext()) {
                        listOfAllImages.add(externalCursor.getString(column_index_data));
                    }

                    while (internalCursor.moveToNext()) {
                        listOfAllImages.add(internalCursor.getString(column_index_data));
                    }

                }

                if (permissionsResult.hasAskedForPermissions()) { // false if pre-M
                    externalCursor = getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            orderBy);

                    internalCursor = getContentResolver().query(
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                            null,
                            null,
                            null,
                            orderBy);

                    column_index_data = externalCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);


                    while (externalCursor.moveToNext()) {
                        listOfAllImages.add(externalCursor.getString(column_index_data));
                    }

                    while (internalCursor.moveToNext()) {
                        listOfAllImages.add(internalCursor.getString(column_index_data));
                    }
                }
            }
        });

        return listOfAllImages;
    }

}
