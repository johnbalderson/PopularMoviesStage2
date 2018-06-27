package com.johnbalderson.popularmoviesstage2.general_utils;

// JSON utilities to extract data from movie, review, and trailer

import android.util.Log;

import com.johnbalderson.popularmoviesstage2.data_models.Movie;
import com.johnbalderson.popularmoviesstage2.data_models.Review;
import com.johnbalderson.popularmoviesstage2.data_models.Trailer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    public static ArrayList<Movie> parseMovieJson(String json) {
        try {

            Movie movie;
            JSONObject object = new JSONObject(json);

            // use results to build JSON array
            JSONArray resultsArray = new JSONArray(object.optString("results",
                    "[\"\"]"));


            // build Array list and extract fields from JSON for movie data
            ArrayList<Movie> items = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                String current = resultsArray.optString(i, "");

                JSONObject movieJson = new JSONObject(current);

                String id = movieJson.optString("id", "0");
                String overview = movieJson.optString("overview", "Not Available");
                String original_title = movieJson.optString("original_title",
                        "Not Available");
                String poster_path = movieJson
                        .optString("poster_path", "Not Available");
                String release_date = movieJson.optString("release_date",
                        "Not Available");
                String vote_average = movieJson.optString("vote_average", "Not Available");
                movie = new Movie(id, original_title, overview,
                        poster_path, release_date, Double.parseDouble(vote_average), false);
                items.add(movie);

            }
            return items;

        } catch (Exception ex) {
            Log.e(TAG + "parseMovieJson", "Could not parse Movie Data JSON " + json);
            return null;
        }
    }


    public static List<Review> parseReviewJson(String json) {
        try {
            Review review;
            JSONObject object = new JSONObject(json);

            // use results to build JSON array
            JSONArray resultsArray = new JSONArray(object.optString("results",
                    "[\"\"]"));

            // build Array list and extract fields from JSON for movie review
            ArrayList<Review> items = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                String current = resultsArray.optString(i, "");
                JSONObject reviewJson = new JSONObject(current);

                String content = reviewJson.optString("content", "Not Available");
                String author = reviewJson.optString("author", "Not Available");

                review = new Review(author, content);
                items.add(review);
            }
            return items;

        } catch (Exception ex) {
            Log.i(TAG + "parseReviewJson", "Could not parse Movie Review JSON " + json);
            return null;
        }

    }

    public static List<Trailer> parseTrailerJson(String json) {
        try {
            Trailer trailer;
            JSONObject object = new JSONObject(json);

            // use results to build JSON array
            JSONArray resultsArray = new JSONArray(object.optString("results",
                    "[\"\"]"));

            // build Array list and extract fields from JSON for trailer
            ArrayList<Trailer> items = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                String current = resultsArray.optString(i, "");
                JSONObject trailerJson = new JSONObject(current);

                String id = trailerJson.optString("id", "Not Available");
                String key = trailerJson.optString("key", "Not Available");

                trailer = new Trailer(id, key);
                items.add(trailer);
            }
            return items;

        } catch (Exception ex) {
            Log.i(TAG + "parseTrailerJson", "Could not parse Movie Trailer JSON " + json);
            return null;
        }

    }
}
