package camera1.themaestrochef.com.cameraapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import camera1.themaestrochef.com.cameraapp.R;
import camera1.themaestrochef.com.cameraapp.Utilities.UiUtilise;

public class ImagePreviewActivity extends AppCompatActivity {

    @BindView(R.id.app_image)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        UiUtilise.hideSystemBar(this);
        UiUtilise.hideToolBar(this);
        ButterKnife.bind(this);

        String mPath = getIntent().getStringExtra("imagePath");
        Glide.with(this).load(mPath).into(imageView);
    }
}
