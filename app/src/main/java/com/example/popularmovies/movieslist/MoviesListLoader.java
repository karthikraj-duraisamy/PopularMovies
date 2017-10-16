package com.example.popularmovies.movieslist;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.popularmovies.Movie;
import com.example.popularmovies.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom Loader that loads movies list from API endpoint.
 */
public class MoviesListLoader extends AsyncTaskLoader<List<Movie>> {

    private List<Movie> moviesList;
    private String searchQuery = "";

    public MoviesListLoader(Context context, String searchQueryParam) {
        super(context);
        searchQuery = searchQueryParam;
    }

    /**
     * To load all data from network in background thread
     */
    @Override public List<Movie> loadInBackground() {
        /* If there's no zip code, there's nothing to look up. */
        if (searchQuery.length() == 0) {
            return null;
        }
        moviesList = new ArrayList<>();
        String sortBy = searchQuery;
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

                moviesList.add(movie);
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
    @Override public void deliverResult(List<Movie> movies) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (movies != null) {
                onReleaseResources(movies);
            }
        }
        List<Movie> loadedMovies = moviesList;
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
    @Override public void onCanceled(List<Movie> movies) {
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
    protected void onReleaseResources(List<Movie> movies) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}