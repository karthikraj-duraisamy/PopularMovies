package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.data.FavMoviesContract;
import com.example.popularmovies.movieslist.MoviesListLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements MoviesGridAdapter.MovieAdapterListener, LoaderManager.LoaderCallbacks {

    private MoviesGridAdapter mAdapter;
    private List<Movie> movieArrayList;
    private String SORT_BY = "";

    private static final int CURSOR_LOADER = 1;
    private static final int MOVIE_LOADER = 0;


    @BindView(R.id.tv_error_message_display)
    TextView errorMessageTextView;
    @BindView(R.id.recyclerview_moviewlist) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.layout_error_message) LinearLayout errorViewLayout;
    @BindView(R.id.pb_loading_indicator) ProgressBar progressBar;

    @BindString(R.string.something_went_wrong) String somethingWentWrongMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        movieArrayList = new ArrayList<>();
        mAdapter = new MoviesGridAdapter(this, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        /*
        By default we set to pull popular movies
        */
        SORT_BY = "popular";
        int mNoOfColumns = calculateNoOfColumns(getApplicationContext());
        GridLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this).forceLoad();

    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        noOfColumns = noOfColumns > 2 ? noOfColumns : 2;
        return noOfColumns;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void showMovieListView() {
        if(InternetUtils.isConnected(MovieListActivity.this)) {
            errorViewLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorView();
        }
    }

    private void showErrorView() {
        errorViewLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void loadMovieData() {
        if(InternetUtils.isConnected(MovieListActivity.this)) {
            if(movieArrayList == null)
                movieArrayList = new ArrayList<>();
            else
                movieArrayList.clear();
            mAdapter.updateMovieDataSet(movieArrayList);
            if(SORT_BY.equals("Favourites")) {
                getSupportLoaderManager().initLoader(CURSOR_LOADER, null, this).forceLoad();
            } else {
                getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, this).forceLoad();
            }
        } else {
            showErrorView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_by_popular) {
            SORT_BY = "popular";
            loadMovieData();
            return true;
        } else if (id == R.id.action_sort_by_top_rated) {
            SORT_BY = "top_rated";
            loadMovieData();
            return true;
        } else if (id == R.id.action_sort_by_favourites) {
            SORT_BY = "Favourites";
            loadMovieData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieClick(int itemPosition) {
        Movie movie = movieArrayList.get(itemPosition);
        openDetailView(movie);
    }

    private void openDetailView(Movie movie) {
        Intent detailViewIntent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
        detailViewIntent.putExtra(MovieDetailActivity.MOVIE_DATA, movie);
        startActivity(detailViewIntent);
    }

    //AsyncLoader Callbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == CURSOR_LOADER) {
            return new CursorLoader(MovieListActivity.this,
                    FavMoviesContract.FavMoviesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        } else {
            return new MoviesListLoader(MovieListActivity.this, SORT_BY);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

        progressBar.setVisibility(View.INVISIBLE);
        if(data == null)
            return;

        if(movieArrayList != null)
            movieArrayList.clear();

        if(loader.getId() == CURSOR_LOADER) {
            Cursor cursor = (Cursor) data;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                Movie newMoview = new Movie();
                newMoview.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.MOVIE_ID))));
                newMoview.setVote_average(Double.valueOf(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.VOTE_AVERAGE))));
                newMoview.setPoster_path(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.POSTER_PATH)));
                newMoview.setOriginal_title(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.ORIGINAL_TITLE)));
                newMoview.setRelease_date(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.RELEASE_DATE)));
                newMoview.setOverview(cursor.getString(cursor.getColumnIndex(FavMoviesContract.FavMoviesEntry.OVERVIEW)));
                movieArrayList.add(newMoview); //add the item
                cursor.moveToNext();
            }

        } else {
            movieArrayList = (List<Movie>)data;
        }

        if (movieArrayList.size() > 0) {
            showMovieListView();
            mAdapter.updateMovieDataSet(movieArrayList);
            mAdapter.notifyDataSetChanged();
        } else {
            errorMessageTextView.setText(R.string.msg_no_data_found);
            showErrorView();
        }

    }



}