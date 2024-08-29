package com.steelkiwi.cropiwa.sample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;
import com.steelkiwi.cropiwa.sample.data.CropGallery;

import java.io.File;

public class CropActivity extends AppCompatActivity {

    private static final String EXTRA_URI = "selected_uri";

    public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }

    private CropIwaView cropView;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_crop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        cropView = findViewById(R.id.crop_view);
        cropView.setImageUri(imageUri);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
            String randomSuffix = String.valueOf(imageUri.hashCode());
            File file = new File(App.getInstance().getFilesDir(), CropGallery.CROPPED_IMAGE_NAME + randomSuffix);
            Uri destination = Uri.fromFile(file);
            CropIwaSaveConfig cropConfig = new CropIwaSaveConfig.Builder(destination)
                    .setCompressFormat(Bitmap.CompressFormat.PNG)
                    .setQuality(100)
                    .setSize(200, 200)
                    .build();
            cropView.crop(cropConfig);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
