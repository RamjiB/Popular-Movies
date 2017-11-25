package com.rbuys.android.mdb.utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MoviesJsonUtils {

    /**
     * Get Movie titles and store it in an array
     * @param movieDBJson
     * @return
     * @throws JSONException
     */

    @SuppressWarnings("JavaDoc")
    public static String[] getMovieDetails(String movieDBJson)
            throws JSONException{

        final String MDB_RESULTS = "results";
        final String MDB_TITLE = "original_title";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_MOVIE_ID = "id";

        String[] movieDetails;

        JSONObject movieDB = new JSONObject(movieDBJson);

        //getting movies details from json object "results"

        JSONArray moviesResultsArray = movieDB.getJSONArray(MDB_RESULTS);

        movieDetails = new String[moviesResultsArray.length()];

        //getting movie details and store it in an array
        for (int i = 0; i < moviesResultsArray.length(); i++){

            JSONObject movieDetail = moviesResultsArray.getJSONObject(i);

            // get movie title
            String title = movieDetail.getString(MDB_TITLE);

            // get movie poster image path
            String moviePosterImagePaths = movieDetail.getString(MDB_POSTER_PATH);

            //get movie vote average
            String voteAverage = movieDetail.getString(MDB_VOTE_AVERAGE) + "/10";

            //get movie overview
            String overview = movieDetail.getString(MDB_OVERVIEW);

            //get movie release date
            String releaseDate = movieDetail.getString(MDB_RELEASE_DATE);

            //get movie id(needed for trailer url)
            String id = movieDetail.getString(MDB_MOVIE_ID);


            movieDetails[i] = title + " , " + moviePosterImagePaths + " , " + voteAverage +
                                    " , " + releaseDate + " , " + overview + " , " + id;
        }

        return movieDetails;
    }

    public static String[] getVideoDetails(String videoJson)throws JSONException{

        final String MOVIE_RESULTS = "results";
        final String MOVIE_KEY = "key";
        final String MOVIE_TYPE = "type";

        JSONObject videoDB = new JSONObject(videoJson);

        //getting movies details from json object "results"

        JSONArray videoResultsArray = videoDB.getJSONArray(MOVIE_RESULTS);

        String[] videoDetails = new String[videoResultsArray.length()];

        //getting trailer details and store it in an array
        for (int i = 0; i < videoResultsArray.length(); i++){

            JSONObject videoDetail = videoResultsArray.getJSONObject(i);

            //get trailer key
            String key = videoDetail.getString(MOVIE_KEY);

            //get video type
            String type = videoDetail.getString(MOVIE_TYPE);

            videoDetails[i] = key + " , " + type;

        }
        return videoDetails;
    }

    public static String[] getReviewDetails(String reviewJson)throws JSONException{

        final String REVIEW_RESULTS = "results";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";

        JSONObject reviewDB = new JSONObject(reviewJson);

        //getting movies details from json object "results"

        JSONArray reviewResultsArray = reviewDB.getJSONArray(REVIEW_RESULTS);

        String[] reviewDetails = new String[reviewResultsArray.length()];

        //getting trailer details and store it in an array
        for (int i = 0; i < reviewResultsArray.length(); i++){

            JSONObject reviewDetail = reviewResultsArray.getJSONObject(i);

            //get review author
            String author = reviewDetail.getString(REVIEW_AUTHOR);

            //get review content
            String content = reviewDetail.getString(REVIEW_CONTENT);

            reviewDetails[i] = author + " , " + content;

        }
        return reviewDetails;
    }
}
