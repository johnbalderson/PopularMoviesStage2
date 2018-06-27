package com.johnbalderson.popularmoviesstage2.adapters;

import android.content.Context;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.johnbalderson.popularmoviesstage2.R;
import com.johnbalderson.popularmoviesstage2.data_models.Review;


import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MovieReviewHolder> {

    private static final String TAG = ReviewAdapter.class.getSimpleName();

    // save review items
    private List<Review> mItemList;
    private final Context mContext;


    public ReviewAdapter(List<Review> movieReviewItemList,
                         Context context) {
        mItemList = movieReviewItemList;
        mContext = context;
    }

    // inflate layout for review item
    public MovieReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.MovieReviewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void setMovieReviewData(List<Review> movieReviewItem) {
        mItemList = movieReviewItem;
        notifyDataSetChanged();
    }

    class MovieReviewHolder extends RecyclerView.ViewHolder {

        final TextView authorView;
        final TextView contentView;

        MovieReviewHolder(View view) {
            super(view);

            authorView = view.findViewById(R.id.tv_review_author);
            contentView = view.findViewById(R.id.tv_review_content);

        }

        void bind(int listIndex) {

            // set text for author and content
            Review review = mItemList.get(listIndex);
            authorView.setText(review.getAuthor());
            contentView.setText(review.getContent());
        }
    }
}
