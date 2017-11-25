package com.rbuys.android.mdb.data;


import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteContract {

    //The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.rbuys.android.mdb";

    //public static final Uri = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //Path for the notes directory
    public static final String PATH_TASKS = "FavouriteMoviesTable";

    public static final class FavouriteEntry implements BaseColumns{

        //TaskEntry content URI = base content uri + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        //Table Name
        public static final String TABLE_NAME = "favouriteMovies";

        //Table Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "ImagePath";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ID = "id";
    }
}
