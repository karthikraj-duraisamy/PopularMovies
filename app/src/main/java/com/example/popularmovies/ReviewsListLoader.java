package com.example.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 30/10/17.
 */

public class ReviewsListLoader extends AsyncTaskLoader<List<MovieReviews>> {

    private List<MovieReviews> moviesList;
    private String selectedMovieId = "";

    public ReviewsListLoader(Context context, String selectedMovieIdParam) {
        super(context);
        selectedMovieId = selectedMovieIdParam;
    }

    /**
     * To load all data from network in background thread
     */
    @Override public List<MovieReviews> loadInBackground() {
        /* If there's no zip code, there's nothing to look up. */
        if (selectedMovieId.length() == 0) {
            return null;
        }
        moviesList = new ArrayList<>();
        URL requestUrl = NetworkUtils.buildReviewUrl(selectedMovieId);

        try {
            String jsonResponse = NetworkUtils
                    .getResponseStringFromHttpUrl(requestUrl);

            JSONObject reader = new JSONObject(jsonResponse);

            JSONArray jsonArray  = reader.getJSONArray("results");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject =  jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String author = jsonObject.getString("author");
                String content = jsonObject.getString("content");
                String url = jsonObject.getString("url");
                MovieReviews movieReviews = new MovieReviews();
                movieReviews.setId(id);
                movieReviews.setAuthor(author);
                movieReviews.setContent(content);
                movieReviews.setUrl(url);

                moviesList.add(movieReviews);
            }

            return moviesList;

        } catch (Exception e) {
            e.printStackTrace();
            return moviesList;
        }
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<MovieReviews> movies) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (movies != null) {
                onReleaseResources(movies);
            }
        }
        List<MovieReviews> loadedMovies = moviesList;
        moviesList = movies;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(movies);
        }

        // At this point we can release the resources associated with
        // 'loadedMovies' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (loadedMovies != null) {
            onReleaseResources(loadedMovies);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (moviesList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(moviesList);
        }

    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<MovieReviews> movies) {
        super.onCanceled(movies);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(movies);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (moviesList != null) {
            onReleaseResources(moviesList);
            moviesList = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<MovieReviews> movies) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}