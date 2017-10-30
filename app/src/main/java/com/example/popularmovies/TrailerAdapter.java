package com.example.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{


    private TrailerAdapter.TrailerClickListener listener;
    private Context mContext;
    private List<MovieTrailers> movieTrailersList;
    interface TrailerClickListener {
        void onTrailerClick(int itemPosition);
    }

    public TrailerAdapter(Context context, TrailerAdapter.TrailerClickListener listener) {
        this.listener = listener;
        this.mContext = context;
        movieTrailersList = new ArrayList<>();
    }

    void updateMovieDataSet(List<MovieTrailers> paramList) {
        movieTrailersList = paramList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return movieTrailersList.size();
    }

    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_trailer, parent, false);
        return new TrailerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.ViewHolder holder, int position) {
        //Update the trailer text description here with
        holder.trailerDescTextview.setText(movieTrailersList.get(position).getName());
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.tvTrailerDesc)
        TextView trailerDescTextview;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onTrailerClick(getAdapterPosition());
        }
    }


}
