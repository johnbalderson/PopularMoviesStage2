package com.johnbalderson.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.johnbalderson.popularmoviesstage2.R;

import com.johnbalderson.popularmoviesstage2.data_models.Movie;
import com.johnbalderson.popularmoviesstage2.general_utils.NetworkUtils;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mMovieItemList;


    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void OnListItemClick(Movie movieItem);
    }

    public MovieAdapter(List<Movie> movieItemList, ListItemClickListener listener) {

        mMovieItemList = movieItemList;
        mOnClickListener = listener;

    }

    // inflate layout for movie item consisting of image and movie title

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@SuppressWarnings("NullableProblems") MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieItemList == null ? 0 : mMovieItemList.size();
    }

    public void setMovieData(List<Movie> movieItemList) {
        mMovieItemList = movieItemList;
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView listMovieItemView;
        TextView titleTextView;
        String title;

        MovieViewHolder(View itemView) {
            super(itemView);

            listMovieItemView = itemView.findViewById(R.id.iv_item_poster);

            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            Movie movieItem = mMovieItemList.get(listIndex);
            listMovieItemView = itemView.findViewById(R.id.iv_item_poster);
            title = movieItem.getOriginalTitle();
            String posterPathURL = NetworkUtils.buildPosterUrl(movieItem.getPosterPath());

            // use Picasso to show movie poster
            try {
                Picasso.get()
                        .load(posterPathURL)
                        .placeholder(R.drawable.tmdb)
                        .error(R.drawable.tmdb)
                        .into(listMovieItemView);

            // display movie title text on top of poster
            titleTextView = itemView.findViewById(R.id.tv_original_title);
            titleTextView.setText(title);

            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        }

        // get reference to movie clicked on
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.OnListItemClick(mMovieItemList.get(clickedPosition));

        }
    }
}
