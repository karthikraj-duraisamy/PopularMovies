package com.example.popularmovies;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviewReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieReviews>>{

    private static final String TAG = MoviewReviewsActivity.class.getSimpleName();


    public static final String MOVIE_EXTRA_ID = "movie_extra_id";

    private MoviesReviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MovieReviews> movieArrayList;
    private String selectedMovieId = "";

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layout_error_message)
    LinearLayout errorViewLayout;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;

    @BindString(R.string.something_went_wrong) String somethingWentWrongMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moview_reviews);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Reviews");

        movieArrayList = new ArrayList<>();

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new MoviesReviewAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Snackbar.make(mRecyclerView, "No Movie passed to this activity", Snackbar.LENGTH_LONG).show();
        } else {
            selectedMovieId = String.valueOf(extras.get(MOVIE_EXTRA_ID));
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadReviewsData();
    }



    private void showReviewListView() {
        if(InternetUtils.isConnected(MoviewReviewsActivity.this)) {
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

    private void loadReviewsData() {
        if(InternetUtils.isConnected(MoviewReviewsActivity.this)) {
            if(movieArrayList == null)
                movieArrayList = new ArrayList<>();
            else
                movieArrayList.clear();
            mAdapter.updateReviewsDataSet(movieArrayList);
            getSupportLoaderManager().initLoader(0, null, this).forceLoad();
        } else {
            showErrorView();
        }
    }

    //AsyncLoader Callbacks
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new ReviewsListLoader(MoviewReviewsActivity.this, selectedMovieId);
    }

    @Override
    public void onLoaderReset(Loader<List<MovieReviews>> loader) {

    }

    @Override
    public void onLoadFinished(Loader<List<MovieReviews>> loader, List<MovieReviews> data) {

        progressBar.setVisibility(View.INVISIBLE);

        if(data == null)
            return;

        movieArrayList = data;
        if (data.size() > 0) {
            showReviewListView();
            mAdapter.updateReviewsDataSet(movieArrayList);
            mAdapter.notifyDataSetChanged();
        } else {
            Snackbar.make(mRecyclerView, somethingWentWrongMessage, Snackbar.LENGTH_LONG).show();
            showErrorView();
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
