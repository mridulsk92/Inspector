package com.example.mridul_xpetize.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class worker_tab1 extends Fragment {

    ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_worker_tab1, container, false);

        img = (ImageView)view.findViewById(R.id.imageView2);
        final String image_url = "https://www.google.co.in/images/branding/googleg/1x/googleg_standard_color_128dp.png";
        Picasso.with(getActivity())
                .load(image_url)
//                .placeholder(R.drawable.no_image)
                .into(img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),FullImageActivity.class);
                i.putExtra("image",image_url);
                i.putExtra("type","url");
                startActivity(i);
            }
        });

    return view;
    }
}
