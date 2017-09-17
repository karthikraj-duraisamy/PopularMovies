package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE_DATA = "movie_data";
    private ImageView ivMoviewThumbnail;
    private TextView tvReleaseDate, tvUserRating, tvMovieDescription;

    private ActionBar actionBar;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        ivMoviewThumbnail = (ImageView) findViewById(R.id.iv_moview_thumbnail);
        tvReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        tvUserRating = (TextView) findViewById(R.id.tv_user_rating);
        tvMovieDescription = (TextView) findViewById(R.id.tv_movie_description);

        Intent activityintent = getIntent();
        if(activityintent.hasExtra(MOVIE_DATA)) {
            Movie receivedMovie = activityintent.getParcelableExtra(MOVIE_DATA);
            updateUI(receivedMovie);
        }
    }

    private void setTitle(String title) {
        if(actionBar != null)
            actionBar.setTitle(title);
    }

    private void updateUI(Movie movie) {

        setTitle(movie.getOriginal_title());
        String posterURI = getString(R.string.poster_uri) + movie.getPoster_path();
        Uri imageUri = Uri.parse(posterURI);
        Picasso.with(mContext).load(imageUri).into(ivMoviewThumbnail);

        tvReleaseDate.setText(movie.getRelease_date());
        String userRating = String.valueOf(movie.getVote_average()) + getString(R.string.user_rating_average_postfix);
        tvUserRating.setText(userRating);
        tvMovieDescription.setText(movie.getOverview());
    }
}
