package com.johnbalderson.popularmoviesstage2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.databinding.DataBindingUtil;


import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.johnbalderson.popularmoviesstage2.adapters.ReviewAdapter;
import com.johnbalderson.popularmoviesstage2.adapters.TrailerAdapter;
import com.johnbalderson.popularmoviesstage2.db_utils.MovieDBLayout;
import com.johnbalderson.popularmoviesstage2.data_models.Movie;
import com.johnbalderson.popularmoviesstage2.data_models.Review;
import com.johnbalderson.popularmoviesstage2.data_models.Trailer;
import com.johnbalderson.popularmoviesstage2.general_utils.JsonUtils;
import com.johnbalderson.popularmoviesstage2.general_utils.NetworkUtils;

import com.squareup.picasso.Picasso;



import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.johnbalderson.popularmoviesstage2.databinding.MovieDetailsBinding;


// display detailed activity of movies including favorites selection, trailer, and reviews


public class MovieDetailsActivity extends AppCompatActivity
        implements TrailerAdapter.ListItemClickListener

{
    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    public static final String EXTRA_INDEX = "extra_index";

    private String mApiKey = "";
    private String saveTitle = "";
    private MovieDetailsBinding mBinding;

    private List<Review> movieReviewItems;
    private ReviewAdapter movieReviewAdapter;

    private List<Trailer> trailerItems;
    private TrailerAdapter trailerAdapter;

    private ImageButton buttonStar;

    private Movie movieItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /**
         *  concept of data binding (mBinding) adapted from here:
         *
         *  https://www.bignerdranch.com/blog/descent-into-databinding/
         *
         * */
        
        mBinding = DataBindingUtil.setContentView(this, R.layout.movie_details);

        // get API key from BuildConfig
        mApiKey = BuildConfig.API_KEY;

        buttonStar =  findViewById(R.id.star);

        // allow for back arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError("Intent is null");
        }

        // if no data passed from main activity, close with "no movie data" error
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            closeOnError(getString(R.string.no_movie_data));
            return;
        }
        movieItem = extras.getParcelable("movieItem");
        if (movieItem == null) {
            closeOnError(getString(R.string.no_movie_data));
        } else {

            // set up adapter for reviews
            RecyclerView mMovieReviewList = findViewById(R.id.rv_reviews);
            LinearLayoutManager layoutManagerReview = new LinearLayoutManager(this);
            mMovieReviewList.setLayoutManager(layoutManagerReview);
            mMovieReviewList.setHasFixedSize(false);
            movieReviewAdapter = new ReviewAdapter(movieReviewItems, this);
            mMovieReviewList.setAdapter(movieReviewAdapter);

            // set up adapter for trailers
            RecyclerView trailerList = findViewById(R.id.rv_trailers);
            LinearLayoutManager layoutManagerVideo = new LinearLayoutManager(this);
            trailerList.setLayoutManager(layoutManagerVideo);
            trailerList.setHasFixedSize(false);
            trailerAdapter = new TrailerAdapter(trailerItems, this, this);
            trailerList.setAdapter(trailerAdapter);



            // get reviews and trailers on background thread
            GetReviewsAndTrailers();
            // populate screen with data and option to set favorite
            populateUI();
        }
    }

    private void GetReviewsAndTrailers() {

        // get reviews on background thread
        new MovieDetailsActivity.NetworkQueryTask()
                .execute(new NetworkQueryTaskParameters(movieItem.getMovieId(),
                        getText(R.string.data_key_review).toString(),mApiKey));

        // get trailers on background thread
        new MovieDetailsActivity.NetworkQueryTask()
                .execute(new NetworkQueryTaskParameters(movieItem.getMovieId(),
                        getText(R.string.data_key_videos).toString(),mApiKey));

    }

    // build the video URL
    @Override
    public void OnListItemClick(Trailer trailer) {
        this.startActivity(new Intent(Intent.ACTION_VIEW, NetworkUtils.buildVideoURL(trailer.getKey())));
    }


    private static class NetworkQueryTaskParameters {
        final String dataKey;
        final URL searchUrl;

        NetworkQueryTaskParameters(String id, String dataKey, String apiKey) {
            this.dataKey = dataKey;
            searchUrl = NetworkUtils.buildMovieDataUrl(id, dataKey, apiKey);
        }
    }


    @SuppressLint("StaticFieldLeak")
    class NetworkQueryTask extends AsyncTask<NetworkQueryTaskParameters, Void, String> {

        String datakey;

        @Override
        protected String doInBackground(NetworkQueryTaskParameters... params) {
            URL searchUrl = params[0].searchUrl;
            datakey = params[0].dataKey;
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpURL(searchUrl);

            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {

                //we are going to load either review or trailer based on data_key passed

                if (datakey.equals(getText(R.string.data_key_review).toString())) {
                    movieReviewItems = JsonUtils.parseReviewJson(searchResults);
                    movieReviewAdapter.setMovieReviewData(movieReviewItems);

               } else { //must be equal to "videos" (trailers)
                    trailerItems = JsonUtils.parseTrailerJson(searchResults);
                    trailerAdapter.setTrailerData(trailerItems);
                }
            }
        }
    }


     private void populateUI() {

     // get title
        mBinding.tvOriginalTitle.setText(movieItem.getOriginalTitle());
        saveTitle = movieItem.getOriginalTitle();

     // get release date
        mBinding.tvReleaseDate.setText(Html.fromHtml(formatDate(movieItem.getReleaseDate())));

     // get plot (synopsis)
        mBinding.tvSynopsis.setText(movieItem.getPlotSynopsis());

     // get user rating
        mBinding.tvUserRating.setText(String.valueOf(movieItem.getUserRating() + " /10"));

     // when star is clicked, process favorite selection/de-selection
        buttonStar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
        // if movie is already a favorite, tell user it has been removed from DB when star is clicked on
              if(movieItem.isMovieFavorite()) {
                 movieItem.setMovieFavorite(false);
                 deleteFavorites();
                 Toast.makeText(MovieDetailsActivity.this, saveTitle + " " +
                                MovieDetailsActivity.this.getString(R.string.removed_from_favorites),
                 Toast.LENGTH_LONG).show();
              }
               else {
               // set star on to indicate favorite, and add to DB
                 movieItem.setMovieFavorite(true);
                 buttonStar.setImageDrawable(ContextCompat.
                            getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                 addFavorites();
                 Toast.makeText(MovieDetailsActivity.this, saveTitle + " " +
                               MovieDetailsActivity.this.getString(R.string.added_to_favorites),
                 Toast.LENGTH_LONG).show();
                 }
                 // set the color of the star
                 setFavoriteBackgroundColor();

                }
            });

            setFavoriteBackgroundColor();

            // get the URL of the poster
            String posterPathURL = NetworkUtils.buildPosterUrl(movieItem.getPosterPath());
            String movieTitle = movieItem.getOriginalTitle();

            // use Picasso to display the poster
            try {
                Picasso.get()
                        .load(posterPathURL)
                        .placeholder(R.drawable.tmdb)
                        .error(R.drawable.tmdb)
                        .into(mBinding.ivPoster);

                // set movie title on top of poster
                mBinding.tvOriginalTitle.setText(movieTitle);

            } catch (Exception ex) {
                Log.i(TAG, ex.getMessage());
            }

         }

    // set the color of the favorite star on or off
    private void setFavoriteBackgroundColor() {
        final ImageButton ButtonStar = findViewById(R.id.star);
        // if it's a favorite, set the star color on
        if (movieItem.isMovieFavorite()) {
            ButtonStar.setImageDrawable(ContextCompat.
                    getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
        } else {
         // de-select color on favorite (turn off)
            ButtonStar.setImageDrawable(ContextCompat.
                    getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
        }
    }

    // add favorites to DB
    private void addFavorites() {
        ContentValues cv = new ContentValues();
        cv.put(MovieDBLayout.MovieTable.COLUMN_ID, movieItem.getMovieId());
        cv.put(MovieDBLayout.MovieTable.COLUMN_ORIGINAL_TITLE, movieItem.getOriginalTitle());
        saveTitle = movieItem.getOriginalTitle();
        cv.put(MovieDBLayout.MovieTable.COLUMN_SYNOPSIS, movieItem.getPlotSynopsis());
        cv.put(MovieDBLayout.MovieTable.COLUMN_POSTER_PATH, movieItem.getPosterPath());
        cv.put(MovieDBLayout.MovieTable.COLUMN_RELEASE_DATE, movieItem.getReleaseDate());
        cv.put(MovieDBLayout.MovieTable.COLUMN_VOTE_AVERAGE, movieItem.getUserRating());
        Uri uri = getContentResolver().insert(MovieDBLayout.MovieTable.CONTENT_URI, cv);
        Log.i(TAG, "Added Row: " + uri);
    }

    // remove favorites from DB
    private void deleteFavorites() {
        Uri uri = MovieDBLayout.MovieTable.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieItem.getMovieId()).build();
        int rowCount = getContentResolver().delete(uri, null, null);
        Log.i(TAG, "Removed Rows: " + rowCount );
    }


    public boolean onOptionsItemSelected(MenuItem item) {
            // check to see if back arrow pressed
            int id = item.getItemId();
            if (id == android.R.id.home) {
                // restore title back to app name
                setTitle(R.string.app_name);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                return true;
            }

    return super.onOptionsItemSelected(item);
        }

    private void closeOnError(String msg) {
                 finish();
                 Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * formatDate
     *
     * Input parameter - default format from themoviedb as yyyy-mm-dd
     * Output parameter - "normal" format of MMMM dd, yyyy such as July 4, 2018
     *
     */
    private String formatDate(String dateStr){
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MMMM d, yyyy";
        SimpleDateFormat simpleDateInputFormat, simpleDateOutputFormat;
        try{
            simpleDateInputFormat = new SimpleDateFormat(inputPattern, Locale.US);
            Date date = simpleDateInputFormat.parse(dateStr);
            simpleDateOutputFormat = new SimpleDateFormat(outputPattern, Locale.US);
            dateStr = simpleDateOutputFormat.format(date);
            return dateStr;
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        return null;
    }
}