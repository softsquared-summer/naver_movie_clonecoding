package com.example.mydictionary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class SubActivity extends AppCompatActivity {
    private String title = ""; //제목
    private String link = "";//링크
    private String image = "";//이미지
    private String subtitle = "";//부제목
    private int pubDate = 0;//출판년도
    private String director = "";//감독
    private String actor = "";//배우
    private float userRating = 0f;//평점

    ImageView iv_Image;
    TextView tv_title, tv_subtitle, tv_date, tv_actor, tv_director, tv_link, tv_rating;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity);

        //연결
        iv_Image = findViewById(R.id.detail_image);
        tv_title = findViewById(R.id.detail_title);
        tv_subtitle = findViewById(R.id.detail_subtitle);
        tv_date = findViewById(R.id.detail_date);
        tv_actor = findViewById(R.id.detail_actor);
        tv_director = findViewById(R.id.detail_director);
        tv_link = findViewById(R.id.detail_link);
        tv_rating = findViewById(R.id.detail_ranking);
        ratingBar = findViewById(R.id.detail_ratingbar);

        Intent intent = getIntent();
        title =intent.getStringExtra("Title");
        link =intent.getStringExtra("Link");
        image = intent.getStringExtra("Image");
        subtitle = intent.getStringExtra("SubTitle");
        pubDate =intent.getIntExtra("Date",0);
        director = intent.getStringExtra("Director");
        actor =intent.getStringExtra("Actor");
        userRating = Float.parseFloat(intent.getStringExtra("Rating"));

        Glide.with(this).load(image).override(1080,1920).into(iv_Image);
        tv_title.setText(title);
        tv_subtitle.setText(subtitle);
        tv_date.setText(pubDate+"");
        tv_actor.setText(actor);
        tv_director.setText(director);
        tv_link.setText(link);
        tv_rating.setText("("+userRating+")");
        ratingBar.setRating(userRating/2);

        System.out.println(userRating);
    }


}

