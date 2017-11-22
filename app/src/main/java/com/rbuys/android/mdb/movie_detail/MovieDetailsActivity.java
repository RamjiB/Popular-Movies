package com.rbuys.android.mdb.movie_detail;


import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rbuys.android.mdb.R;
import com.rbuys.android.mdb.data.FavouriteContract;
import com.rbuys.android.mdb.home.HomeActivity;
import com.rbuys.android.mdb.utils.MoviesJsonUtils;
import com.rbuys.android.mdb.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static com.rbuys.android.mdb.home.HomeActivity.db;
import static com.rbuys.android.mdb.home.HomeActivity.getAllMovies;
import static com.rbuys.android.mdb.home.MoviesGridAdapter.imageURL;

public class MovieDetailsActivity extends AppCompatActivity implements TrailerListAdapter.ItemClickListener {
    private static final String TAG = "MovieDetailsActivity";

    //Variables
    private TextView mTitle;
    private TextView mReleaseDate,mReleasedOn;
    private TextView mVoteAverage;
    private TextView mOverviewText;
    private TextView mReview;
    private ImageView mPosterImage;
    private ImageView heartWhite,heartRed,mStartImage;

    private ProgressBar mLoadingIndicator;
    private TextView errorMessage;

    private String[] movieInfo;
    private long rowId;

    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;
    private RecyclerView reviewRecyclerView;

    private final int TITLE = 0;
    private final int POSTER_PATH = 1;
    private final int VOTE_AVERAGE = 2;
    private final int RELEASE_DATE = 3;
    private final int OVERVIEW = 4;
    private final int ID = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().orientation){
            case Configuration.ORIENTATION_PORTRAIT:
                setContentView(R.layout.activity_movie_detail);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                setContentView(R.layout.activity_movie_detail_landscape);
                break;
        }

        //Error Message
        errorMessage = (TextView) findViewById(R.id.error_message);

        //Loading Indicator
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        //Initializing toolbar as titlebar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.action_toolbar);
        mToolbar.setTitle("MDB");
        setSupportActionBar(mToolbar);

        //Get a support ActionBAr corresponding to this toolbar
         ActionBar actionBar = getSupportActionBar();

        //Enable the up button
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Initializing the variables
        mTitle = (TextView) findViewById(R.id.movie_title);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mReleasedOn = (TextView) findViewById(R.id.released_on);
        mVoteAverage = (TextView) findViewById(R.id.vote_average);
        mOverviewText = (TextView) findViewById(R.id.overview_text);
        mReview = (TextView) findViewById(R.id.review);
        mPosterImage = (ImageView) findViewById(R.id.movie_poster_image);
        heartWhite = (ImageView) findViewById(R.id.heart_white);
        heartRed = (ImageView) findViewById(R.id.heart_red);
        mStartImage = (ImageView) findViewById(R.id.starImage);

        /*
         * Setting up RecyclerView to hold trailers list
         */

        RecyclerView trailerRecyclerView = (RecyclerView) findViewById(R.id.trailer_rv);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trailerRecyclerView.setHasFixedSize(true);

        mTrailerListAdapter = new TrailerListAdapter(this);
        trailerRecyclerView.setAdapter(mTrailerListAdapter);

        /*
         * Setting up RecyclerView to hold reviews list
         */

        reviewRecyclerView = (RecyclerView) findViewById(R.id.review_rv);
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setHasFixedSize(true);

        mReviewListAdapter = new ReviewListAdapter(this);
        reviewRecyclerView.setAdapter(mReviewListAdapter);

        //getting intent from home activity
        Intent intent = getIntent();
        if (intent != null){
            if (intent.hasExtra("MovieDetails")){
                String movieDetails = intent.getStringExtra("MovieDetails");
                Log.i(TAG,"movieDetails: "+ movieDetails);
                setUpViews(movieDetails);
            }
        }

    }

    private void setUpViews(String movieDetails) {

        movieInfo = movieDetails.split(" , ");

        mTitle.setText(movieInfo[TITLE]);
        mVoteAverage.setText(movieInfo[VOTE_AVERAGE]);
        mReleaseDate.setText(movieInfo[RELEASE_DATE]);
        mOverviewText.setText(movieInfo[OVERVIEW]);

        //setting movie poster image using picasso library
        String mImagePath = imageURL + movieInfo[POSTER_PATH];
        Picasso.with(this).load(mImagePath).into(mPosterImage);

        //set trailer button

        new trailerContentTask().execute(movieInfo[ID]);
        new reviewContentTask().execute(movieInfo[ID]);

        Cursor cursor = getAllMovies();
        cursor.moveToFirst();
        while ( !cursor.isAfterLast()) {
            String title = cursor.getString(cursor.getColumnIndex(
                    FavouriteContract.FavouriteEntry.COLUMN_TITLE));
            if (Objects.equals(title, mTitle.getText().toString())) {

                heartWhite.setVisibility(View.INVISIBLE);
                heartRed.setVisibility(View.VISIBLE);

                // get row id
                rowId = cursor.getLong(cursor.getColumnIndex(FavouriteContract.FavouriteEntry._ID));
                break;
            }
            cursor.moveToNext();
        }
    }

    @Override
    public void onItemClick(String trailerURL) {
        Log.i(TAG,"trailerURL: "+trailerURL);

        Uri trailer = Uri.parse(trailerURL);
        Intent intent = new Intent(Intent.ACTION_VIEW,trailer);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }

    }

    /**
     * add the movie to favourite movies database
     */
    public void AddToFavourite(@SuppressWarnings("UnusedParameters") View view) {

        heartWhite.setVisibility(View.INVISIBLE);
        heartRed.setVisibility(View.VISIBLE);

        // Insert values into the database
        ContentValues cv = new ContentValues();
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_TITLE,movieInfo[TITLE]);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_IMAGE,movieInfo[POSTER_PATH]);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_VOTE_AVERAGE,movieInfo[VOTE_AVERAGE]);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_RELEASE_DATE,movieInfo[RELEASE_DATE]);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_OVERVIEW,movieInfo[OVERVIEW]);
        cv.put(FavouriteContract.FavouriteEntry.COLUMN_ID,movieInfo[ID]);
        db.insert(FavouriteContract.FavouriteEntry.TABLE_NAME,null,cv);

        //update the cursor in the adapter
        HomeActivity.mMoviesGridAdapter.swapCursor(getAllMovies());
    }

    /**
     * remove movie from favourite movies database
     */
    public void RemoveFromFavourite(@SuppressWarnings("UnusedParameters") View view) {

        heartWhite.setVisibility(View.VISIBLE);
        heartRed.setVisibility(View.INVISIBLE);

        //remove data from database
        db.delete(FavouriteContract.FavouriteEntry.TABLE_NAME, FavouriteContract.FavouriteEntry._ID + "=" + rowId,null);

        //update the cursor in the adapter
        HomeActivity.mMoviesGridAdapter.swapCursor(getAllMovies());

    }

    /**
     * get trailers and display it in its view
     */

    private class trailerContentTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            URL videoUrl = NetworkUtils.buildVideosUrl(params[0]);
            Log.i(TAG,"videoUrl: "+ videoUrl);

            try {
                String videoJson = NetworkUtils.getResponseFromHttpUrl(videoUrl);
                Log.i(TAG,"videoJson: "+ videoJson);

                String[] videoDetails = MoviesJsonUtils.getVideoDetails(videoJson);
                Log.i(TAG,"videoDetails: "+ Arrays.toString(videoDetails));

                //get trailer count

                int trailerCount = 0;
                for (String videoDetail : videoDetails) {

                    String[] videoType = videoDetail.split(" , ");
                    int TYPE = 1;

                    if (videoType[TYPE].equals("Trailer")) {
                        trailerCount++;
                    }
                }

                Log.i(TAG,"trailerCount: "+trailerCount);

                // get trailer details
                String[] trailerDetails = new String[trailerCount];
                int a = 0;
                for (String trailerDetail : videoDetails) {

                    Log.i(TAG, "trailerDetail: " + trailerDetail);
                    String[] videoType = trailerDetail.split(" , ");
                    int TYPE = 1;

                    if (videoType[TYPE].equals("Trailer")) {
                        trailerDetails[a] = trailerDetail;
                        a++;
                    }

                }
                Log.i(TAG,"trailerDetails: "+ Arrays.toString(trailerDetails));
                return trailerDetails;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] trailerDetails) {

            if (trailerDetails != null){
                mTrailerListAdapter.setTrailerData(trailerDetails);
            }else{
                errorMessage.setVisibility(View.VISIBLE);

                //set invisibility

                mTitle.setVisibility(View.INVISIBLE);
                mPosterImage.setVisibility(View.INVISIBLE);
                mReleaseDate.setVisibility(View.INVISIBLE);
                mReview.setVisibility(View.INVISIBLE);
                mOverviewText.setVisibility(View.INVISIBLE);
                mVoteAverage.setVisibility(View.INVISIBLE);
                heartRed.setVisibility(View.INVISIBLE);
                heartWhite.setVisibility(View.INVISIBLE);
                reviewRecyclerView.setVisibility(View.INVISIBLE);
                mStartImage.setVisibility(View.INVISIBLE);
                mReleasedOn.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * get reviews and display it in its respective view
     */

    private class reviewContentTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected String[] doInBackground(String... params) {
            URL reviewUrl = NetworkUtils.buildReviewUrl(params[0]);
            Log.i(TAG,"reviewUrl: "+ reviewUrl);

            try {
                String reviewJson = NetworkUtils.getResponseFromHttpUrl(reviewUrl);
                Log.i(TAG,"reviewJson: "+ reviewJson);

                String[] reviewDetails = MoviesJsonUtils.getReviewDetails(reviewJson);
                Log.i(TAG,"reviewDetails: "+ Arrays.toString(reviewDetails));
                return reviewDetails;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] reviewDetails) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (reviewDetails.length != 0){
                mReview.setText(getString(R.string.reviews));
                mReviewListAdapter.setReviewData(reviewDetails);
            }else{
                Log.i(TAG,"review details is null");
                reviewRecyclerView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
