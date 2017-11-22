package com.rbuys.android.mdb.data;


import android.provider.BaseColumns;

public class FavouriteContract {

    public static final class FavouriteEntry implements BaseColumns{

        public static final String TABLE_NAME = "favouriteMovies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "ImagePath";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_ID = "id";
    }
}
