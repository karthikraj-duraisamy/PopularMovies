package com.example.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.data.FavMoviesContract;
import com.example.popularmovies.movieslist.MoviesListLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.popularmovies.MoviewReviewsActivity.MOVIE_EXTRA_ID;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerClickListener, LoaderManager.LoaderCallbacks{

    @BindView(R.id.iv_moview_thumbnail) ImageView iVMovieThumbnail;
    @BindView(R.id.btnMarksAsfav)
    Button markAsFavButton;
    @BindView(R.id.tv_release_date) TextView tvReleaseDate;
    @BindView(R.id.tv_user_rating) TextView tvUserRating;
    @BindView(R.id.tv_movie_description) TextView tvMovieDescription;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recyclerView_trailers)
    RecyclerView trailerRecyclerView;
    private Movie receivedMovie;
    @BindString(R.string.user_rating_average_postfix) String userRatingAvgPrefix;
    @BindString(R.string.poster_uri) String posterURIString;


    private static final int CURSOR_LOADER = 1;
    private static final int TRAILER_LOADER = 0;


    public static final String MOVIE_DATA = "movie_data";
    private ActionBar actionBar;
    private List<MovieTrailers> movieTrailersList;
    private Context mContext;
    private TrailerAdapter trailerAdapter;
    private String selectedMovieID;
    private boolean isFavorited;
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
            receivedMovie = activityintent.getParcelableExtra(MOVIE_DATA);
            updateUI(receivedMovie);
            selectedMovieID = String.valueOf(receivedMovie.getId());
       }
        getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this).forceLoad();
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
            getSupportLoaderManager().initLoader(TRAILER_LOADER, null, this).forceLoad();
        }
    }


    //AsyncLoader Callbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == CURSOR_LOADER) {
            return new CursorLoader(MovieDetailActivity.this,
                    FavMoviesContract.FavMoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } else {
            return new VideosListLoader(MovieDetailActivity.this, selectedMovieID);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {


        if(data == null)
            return;

        if(loader.getId() == CURSOR_LOADER) {
            Cursor cursor = (Cursor) data;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                if(selectedMovieID.equals(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.MOVIE_ID)))) {
                    isFavorited = true;
                    if(Build.VERSION.SDK_INT >= 23) {
                        markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));
                    } else {
                        markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
                    markAsFavButton.setText("Remove From Favourites");
                }
                cursor.moveToNext();
            }

        } else {
            movieTrailersList = (List<MovieTrailers>) data;
            if (movieTrailersList.size() > 0) {
                trailerAdapter.updateMovieDataSet(movieTrailersList);
                trailerAdapter.notifyDataSetChanged();
            }
        }
    }


    public void showReviewsOnClick(View v) {
        Intent reviewsActivityIntent = new Intent(MovieDetailActivity.this, MoviewReviewsActivity.class);
        reviewsActivityIntent.putExtra(MOVIE_EXTRA_ID, selectedMovieID);
        startActivity(reviewsActivityIntent);
    }


    // insert favorite movies into database
    public void markAsFavorite(View v) {

        if(!isFavorited) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavMoviesContract.FavMoviesEntry.MOVIE_ID, receivedMovie.getId());
            contentValues.put(FavMoviesContract.FavMoviesEntry.VOTE_AVERAGE, receivedMovie.getVote_average());
            contentValues.put(FavMoviesContract.FavMoviesEntry.POSTER_PATH, receivedMovie.getPoster_path());
            contentValues.put(FavMoviesContract.FavMoviesEntry.ORIGINAL_TITLE, receivedMovie.getOriginal_title());
            contentValues.put(FavMoviesContract.FavMoviesEntry.RELEASE_DATE, receivedMovie.getRelease_date());
            contentValues.put(FavMoviesContract.FavMoviesEntry.OVERVIEW, receivedMovie.getOverview());
            getContentResolver().insert(FavMoviesContract.FavMoviesEntry.CONTENT_URI,
                    contentValues);
            Snackbar.make(trailerRecyclerView, "Marked as Favorite", Snackbar.LENGTH_LONG).show();
            isFavorited = true;
            if (Build.VERSION.SDK_INT >= 23) {
                markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorAccent, null));
            } else {
                markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
            markAsFavButton.setText("REMOVE FROM FAVOURITES");
        } else {
            // Defines selection criteria for the rows you want to delete
            String mSelectionClause = FavMoviesContract.FavMoviesEntry.MOVIE_ID + " LIKE ?";
            String[] mSelectionArgs = {selectedMovieID};
            // Defines a variable to contain the number of rows deleted
            int mRowsDeleted = 0;
            // Deletes the words that match the selection criteria
            mRowsDeleted = getContentResolver().delete(
                    FavMoviesContract.FavMoviesEntry.CONTENT_URI,   // the user dictionary content URI
                    mSelectionClause,                    // the column to select on
                    mSelectionArgs                      // the value to compare to
            );

            if(mRowsDeleted > 0) {
                Snackbar.make(trailerRecyclerView, "Removed from Favorite", Snackbar.LENGTH_LONG).show();
                isFavorited = false;
                if (Build.VERSION.SDK_INT >= 23) {
                    markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                } else {
                    markAsFavButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                markAsFavButton.setText("ADD TO FAVOURITES");

            }
        }

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
