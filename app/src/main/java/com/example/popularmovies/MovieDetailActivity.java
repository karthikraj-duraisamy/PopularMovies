package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.movieslist.MoviesListLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.popularmovies.MoviewReviewsActivity.MOVIE_EXTRA_ID;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener, LoaderManager.LoaderCallbacks<List<MovieTrailers>>{

    @BindView(R.id.iv_moview_thumbnail) ImageView iVMovieThumbnail;
    @BindView(R.id.tv_release_date) TextView tvReleaseDate;
    @BindView(R.id.tv_user_rating) TextView tvUserRating;
    @BindView(R.id.tv_movie_description) TextView tvMovieDescription;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView_trailers)
    RecyclerView trailerRecyclerView;

    @BindString(R.string.user_rating_average_postfix) String userRatingAvgPrefix;
    @BindString(R.string.poster_uri) String posterURIString;

    public static final String MOVIE_DATA = "movie_data";
    private ActionBar actionBar;
    private List<MovieTrailers> movieTrailersList;
    private Context mContext;
    private TrailerAdapter trailerAdapter;
    private String selectedMovieID;
    private RecyclerView.LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        mContext = this;

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        trailerRecyclerView.setLayoutManager(mLayoutManager);

        trailerAdapter = new TrailerAdapter(this, this);
        trailerRecyclerView.setHasFixedSize(true);
        trailerRecyclerView.setAdapter(trailerAdapter);
        movieTrailersList = new ArrayList<>();

        Intent activityintent = getIntent();
        if(activityintent.hasExtra(MOVIE_DATA)) {
            Movie receivedMovie = activityintent.getParcelableExtra(MOVIE_DATA);
            updateUI(receivedMovie);
            selectedMovieID = String.valueOf(receivedMovie.getId());
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTrailerData();
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

    @Override
    public void onTrailerClick(int itemPosition) {

        MovieTrailers movieTrailers = movieTrailersList.get(itemPosition);

        Intent youTubeAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + movieTrailers.getKey()));
        try {
            startActivity(youTubeAppIntent);
        } catch (ActivityNotFoundException ex) {
            Intent youTubeWebIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + movieTrailers.getKey()));
            startActivity(youTubeWebIntent);
        }
    }


    private void loadTrailerData() {
        if(InternetUtils.isConnected(MovieDetailActivity.this)) {
            if(movieTrailersList == null)
                movieTrailersList = new ArrayList<>();
            else
                movieTrailersList.clear();
            trailerAdapter.updateMovieDataSet(movieTrailersList);
            getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }


    //AsyncLoader Callbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new VideosListLoader(MovieDetailActivity.this, selectedMovieID);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieTrailers>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<List<MovieTrailers>> loader, List<MovieTrailers> data) {


        if(data == null)
            return;

        movieTrailersList = data;
        if (data.size() > 0) {
            trailerAdapter.updateMovieDataSet(movieTrailersList);
            trailerAdapter.notifyDataSetChanged();
        }
    }


    public void showReviewsOnClick(View v) {
        Intent reviewsActivityIntent = new Intent(MovieDetailActivity.this, MoviewReviewsActivity.class);
        reviewsActivityIntent.putExtra(MOVIE_EXTRA_ID, selectedMovieID);
        startActivity(reviewsActivityIntent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
