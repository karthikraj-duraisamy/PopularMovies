package com.example.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity implements MoviesGridAdapter.MovieAdapterListener {

    private RecyclerView mRecyclerView;
    private MoviesGridAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private LinearLayout errorViewLayout;
    private ProgressBar progressBar;

    private List<Movie> movieArrayList;
    private String SORT_BY = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movieArrayList = new ArrayList<>();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_moviewlist);
        mAdapter = new MoviesGridAdapter(this, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        /*
        By default we set to pull popular movies
        */
        SORT_BY = "popular";

        int mNoOfColumns = calculateNoOfColumns(getApplicationContext());

        layoutManager = new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        errorViewLayout = (LinearLayout) findViewById(R.id.layout_error_message);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovieData();

    }

    private int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        noOfColumns = noOfColumns > 2 ? noOfColumns : 2;
        return noOfColumns;
    }


    private boolean internetConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void showMovieListView() {
        if(internetConnection()) {
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
        if(internetConnection()) {
            if(movieArrayList == null)
                movieArrayList = new ArrayList<>();
            else
                movieArrayList.clear();

            mAdapter.updateMovieDataSet(movieArrayList);

            new FetchMoviesTask().execute(SORT_BY);
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


    class FetchMoviesTask extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String[] params) {

             /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            String sortBy = params[0];
            URL requestUrl = NetworkUtils.buildUrl(sortBy);

            try {
                String jsonResponse = NetworkUtils
                        .getResponseStringFromHttpUrl(requestUrl);

                JSONObject reader = new JSONObject(jsonResponse);

                JSONArray jsonArray  = reader.getJSONArray("results");

                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject =  jsonArray.getJSONObject(i);
                    Double vote_average = jsonObject.getDouble("vote_average");
                    String posterPath = jsonObject.getString("poster_path");
                    String originalTitle = jsonObject.getString("original_title");
                    String releaseDate = jsonObject.getString("release_date");
                    String overview = jsonObject.getString("overview");
                    Movie movie = new Movie();
                    movie.setVote_average(vote_average);
                    movie.setPoster_path(posterPath);
                    movie.setRelease_date(releaseDate);
                    movie.setOriginal_title(originalTitle);
                    movie.setOverview(overview);
                    Log.v("MOVIE_PARSE", movie.getVote_average() +" "+ movie.getPoster_path() +" "+ movie.getOriginal_title() +" "+ movie.getOverview());

                    movieArrayList.add(movie);
                }

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean status) {
            progressBar.setVisibility(View.INVISIBLE);

            if (status) {
                mAdapter.updateMovieDataSet(movieArrayList);
                showMovieListView();
            } else {
                Snackbar.make(mRecyclerView, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                showErrorView();
            }
            super.onPostExecute(status);
        }
    }

}
