/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.johnbalderson.popularmoviesstage2.general_utils;


import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    // base URL for poster image
    private final static String BASE_URL = "http://image.tmdb.org/t/p/";
    // base URL to extract movies (videos) or reviews
    private final static String BASE_URL_MOVIE = "http://api.themoviedb.org/3/movie/";
    // to show image in recycler view
    private final static String BASE_URL_TRAILER_IMAGE = "http://img.youtube.com/vi/";
    // for building URL for actual Youtube trailer
    private final static String BASE_URL_TRAILER_VIDEO = "https://www.youtube.com/watch?v=";
    // for building URL directly from YouTube
    private final static String BASE_URL_YOUTUBE = "vnd.youtube:";

    //The width of the poster
    private final static String WIDTH = "w185";

    private final static String API_KEY_PARAM = "?api_key=";

    // Builds the URL to fetch poster image.

    public static String buildPosterUrl(String poster) {
        return BASE_URL + WIDTH  + poster;
    }

    @Nullable
    private static URL getUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // build URL for movies (videos) or reviews
    public static URL buildMovieDataUrl(String id, String dataKey, String apiKey) {

        String finalPath = BASE_URL_MOVIE + id + "/" + dataKey + API_KEY_PARAM + apiKey;
        Uri builtUri = Uri.parse(finalPath);
        return getUrl(builtUri);
    }

    // build URL to show trailer image to click on
    public static String trailerImageURL(String key) {
        return BASE_URL_TRAILER_IMAGE + key + "/0.jpg";
    }

    // build URL to sort display
    public static URL buildDataURL(String apiKey, String sort) {
        String finalPath = BASE_URL_MOVIE + sort + API_KEY_PARAM + apiKey;
        Uri builtUri = Uri.parse(finalPath);
        return getUrl(builtUri);
    }

    // build URL for YouTube trailers from browser
    public static Uri buildVideoURL(String videoKey) {
        String browser_path = BASE_URL_TRAILER_VIDEO + videoKey;
        return Uri.parse(browser_path);
    }

    // build URL for YouTube trailers from YouTube
    public static Uri buildYouTubeURL(String videoKey) {
        String youtube_path = BASE_URL_YOUTUBE + videoKey;
        return Uri.parse(youtube_path);
    }

    // Return result from HTTP response

    public static String getResponseFromHttpURL(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10000); //sets connection timeout to 10 seconds
        urlConnection.setReadTimeout(20000); //sets read time out to 20 seconds
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                scanner.close();
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}