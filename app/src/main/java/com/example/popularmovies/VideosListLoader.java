package com.example.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 30/10/17.
 */

public class VideosListLoader extends AsyncTaskLoader<List<MovieTrailers>> {

    List<MovieTrailers> movieTrailersList;
    private long movieId;

    public VideosListLoader(Context context, long movieIdParam) {
        super(context);
        movieId = movieIdParam;
    }

    /**
     * To load all data from network in background thread
     */
    @Override public List<MovieTrailers> loadInBackground() {
        /* If there's no zip code, there's nothing to look up. */
        movieTrailersList = new ArrayList<>();
        long movieIdToLoad = movieId;
        URL requestUrl = NetworkUtils.buildVideoUrl(movieIdToLoad);

        try {
            String jsonResponse = NetworkUtils
                    .getResponseStringFromHttpUrl(requestUrl);

            JSONObject reader = new JSONObject(jsonResponse);

            JSONArray jsonArray  = reader.getJSONArray("results");

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject =  jsonArray.getJSONObject(i);
                String trailerName = jsonObject.getString("name");
                String trailerWebSite = jsonObject.getString("site");
                Integer trailerSize = jsonObject.getInt("size");
                String trailerType = jsonObject.getString("type");
                String trailerVideoKey = jsonObject.getString("key");
                MovieTrailers movieTrailers = new MovieTrailers();
                movieTrailers.setKey(trailerVideoKey);
                movieTrailers.setName(trailerName);
                movieTrailers.setSite(trailerWebSite);
                movieTrailers.setSize(trailerSize);
                movieTrailers.setType(trailerType);
                movieTrailersList.add(movieTrailers);
            }

            return movieTrailersList;

        } catch (Exception e) {
            e.printStackTrace();
            return movieTrailersList;
        }
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<MovieTrailers> movieTrailerses) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (movieTrailerses != null) {
                onReleaseResources(movieTrailerses);
            }
        }
        List<MovieTrailers> loadedMovies = movieTrailersList;
        movieTrailersList = movieTrailerses;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(movieTrailerses);
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
        if (movieTrailersList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(movieTrailersList);
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
    @Override public void onCanceled(List<MovieTrailers> movieTrailers) {
        super.onCanceled(movieTrailers);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(movieTrailers);
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
        if (movieTrailersList != null) {
            onReleaseResources(movieTrailersList);
            movieTrailersList = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<MovieTrailers> movies) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}