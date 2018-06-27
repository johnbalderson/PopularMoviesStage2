package com.johnbalderson.popularmoviesstage2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.johnbalderson.popularmoviesstage2.R;
import com.johnbalderson.popularmoviesstage2.data_models.Trailer;
import com.johnbalderson.popularmoviesstage2.general_utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.VideoViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mItemList; //holds the trailer items
    private final Context mContext;
    final private ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void OnListItemClick(Trailer movieItem);
    }

    public TrailerAdapter(List<Trailer> movieTrailerList,
                          Context context, ListItemClickListener listener ) {
        mItemList = movieTrailerList;
        mContext = context;
        mOnClickListener = listener;

    }


    // inflate layout for movie trailer
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    public void setTrailerData(List<Trailer> movieReviewItem) {
        mItemList = movieReviewItem;
        notifyDataSetChanged();

    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView youtubeTrailer;

        VideoViewHolder(View view) {
            super(view);
            youtubeTrailer = view.findViewById(R.id.iv_trailer_youtube);
            view.setOnClickListener(this);
        }

        void bind(int listIndex) {

            Trailer item = mItemList.get(listIndex);
            String trailerImageUrl = NetworkUtils.trailerImageURL(item.getKey());

            // use Picasso to display trailer image to be clicked on
            try {
                Picasso.get()
                        .load(trailerImageUrl)
                        .fit().centerCrop()
                        .placeholder(R.drawable.tmdb)
                        .error(R.drawable.tmdb)
                        .into(youtubeTrailer);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }

        }

        // get position of individual trailer image clicked on
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.OnListItemClick(mItemList.get(clickedPosition));
        }
    }

}
