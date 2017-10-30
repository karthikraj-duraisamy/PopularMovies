package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthik on 30/10/17.
 */

public class MoviesReviewAdapter extends RecyclerView.Adapter<MoviesReviewAdapter.ViewHolder>{


    private Context mContext;
    private List<MovieReviews> movieReviewsList;

    public MoviesReviewAdapter(Context context) {
        this.mContext = context;
        movieReviewsList = new ArrayList<>();
    }

    void updateReviewsDataSet(List<MovieReviews> paramList) {
        movieReviewsList = paramList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return movieReviewsList.size();
    }

    @Override
    public MoviesReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_reviews, parent, false);
        return new MoviesReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesReviewAdapter.ViewHolder holder, int position) {
        MovieReviews movieReviews = movieReviewsList.get(position);
        holder.reviewAuthorName.setText(movieReviews.getAuthor());
        holder.reviewDescription.setText(Html.fromHtml(movieReviews.getContent()));
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvReviewDesc) TextView reviewDescription;
        @BindView(R.id.tvAuthorName)
        TextView reviewAuthorName;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }


}
