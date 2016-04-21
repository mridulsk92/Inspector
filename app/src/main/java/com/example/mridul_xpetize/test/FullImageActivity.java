package com.example.mridul_xpetize.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class FullImageActivity extends AppCompatActivity {

    ImageView full_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        //Initialise
        full_img = (ImageView) findViewById(R.id.imageView_full);

        //Get Intent
        Intent i = getIntent();
        String type = i.getStringExtra("type");

        //If path
        if (type.equals("path")) {

            String path = i.getStringExtra("path");
            File imgFile = new File(path);
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                full_img.setImageBitmap(myBitmap);

            }
        } else {

            //If url
            String url = i.getStringExtra("image");
            Picasso.with(FullImageActivity.this)
                    .load(url)
                    .into(full_img);
        }
    }
}
