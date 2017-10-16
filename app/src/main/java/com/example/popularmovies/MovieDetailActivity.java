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

import butterknife.BindString;
import butterknife.BindView;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_moview_thumbnail) ImageView iVMovieThumbnail;
    @BindView(R.id.tv_release_date) TextView tvReleaseDate;
    @BindView(R.id.tv_user_rating) TextView tvUserRating;
    @BindView(R.id.tv_movie_description) TextView tvMovieDescription;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindString(R.string.user_rating_average_postfix) String userRatingAvgPrefix;
    @BindString(R.string.poster_uri) String posterURIString;

    public static final String MOVIE_DATA = "movie_data";
    private ActionBar actionBar;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mContext = this;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

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
        String posterURI = posterURIString + movie.getPoster_path();
        Uri imageUri = Uri.parse(posterURI);
        Picasso.with(mContext).load(imageUri).into(iVMovieThumbnail);

        tvReleaseDate.setText(movie.getRelease_date());
        String userRating = String.valueOf(movie.getVote_average()) + userRatingAvgPrefix;
        tvUserRating.setText(userRating);
        tvMovieDescription.setText(movie.getOverview());
    }
}
