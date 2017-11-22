package com.rbuys.android.mdb.utils;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    //Api key
    private static final String API_KEY = "Enter your API key here";

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String REVIEWS = "reviews";
    private static final String VIDEOS = "videos";
    private final static String API_PARAM = "api_key";

    //youtube url values
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/";
    private static final String WATCH = "watch";
    private final static String YOUTUBE_QUERY_PARAM = "v";

    /**
     * Built youtube url to watch trailers
     */
    public static URL buildYoutubeUrl(String key){
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendPath(WATCH)
                .appendQueryParameter(YOUTUBE_QUERY_PARAM,key)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    /**
     * Build the URL used to talk to theMovieDB server using the sort options opted in spinner.
     */

    public static URL buildUrl(String sortOption){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortOption)
                .appendQueryParameter(API_PARAM,API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build URL to get trailers of a particular movie
     */

    public static URL buildVideosUrl(String id){
        Uri videoUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(VIDEOS)
                .appendQueryParameter(API_PARAM,API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(videoUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Build URL to get reviews of a particular movie
     */

    public static URL buildReviewUrl(String id){
        Uri videoUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(REVIEWS)
                .appendQueryParameter(API_PARAM,API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(videoUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Method to return entire json result from builtURL
     */

    public static String getResponseFromHttpUrl(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            }else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }
}
