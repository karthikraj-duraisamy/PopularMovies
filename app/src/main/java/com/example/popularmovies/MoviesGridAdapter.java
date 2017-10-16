package com.example.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;

/**
 * Created by karthik on 2/7/17.
 */

public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.MovieViewHolder>{

    @BindString(R.string.poster_uri) String posterURIString;

    private MovieAdapterListener listener;
    private Context mContext;
    private List<Movie> movieArrayList;
    interface MovieAdapterListener {
        void onMovieClick(int itemPosition);
    }

    public MoviesGridAdapter(Context context, MovieAdapterListener listener) {
        this.listener = listener;
        this.mContext = context;
        movieArrayList = new ArrayList<>();
    }

    void updateMovieDataSet(List<Movie> paramList) {
        movieArrayList = paramList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return movieArrayList.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieArrayList.get(position);
        String posterURI = posterURIString + movie.getPoster_path();
        Uri imageUri = Uri.parse(posterURI);
        Picasso.with(mContext).load(imageUri).into(holder.moviePosterImage);
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePosterImage;
        public MovieViewHolder(View itemView) {
            super(itemView);

            moviePosterImage = (ImageView) itemView.findViewById(R.id.ivMovieListItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onMovieClick(getAdapterPosition());
        }
    }


}
