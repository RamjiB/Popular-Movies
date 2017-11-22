package com.rbuys.android.mdb.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rbuys.android.mdb.data.FavouriteContract;
import com.rbuys.android.mdb.data.FavouriteDbHelper;
import com.rbuys.android.mdb.movie_detail.MovieDetailsActivity;
import com.rbuys.android.mdb.R;
import com.rbuys.android.mdb.utils.MoviesJsonUtils;
import com.rbuys.android.mdb.utils.NetworkUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
                                                                MoviesGridAdapter.ItemClickListener{

    private static final String TAG = "HomeActivity";

    private ProgressBar mLoadingIndicator;
    private TextView errorMessage;
    public static MoviesGridAdapter mMoviesGridAdapter;
    private String itemSelectedFromSpinner;
    public static SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.i(TAG,"onCreate");

        //Error Message
        errorMessage = (TextView) findViewById(R.id.error_message);

        //Initializing toolbar as titlebar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.action_toolbar);
        mToolbar.setTitle("MDB");
        setSupportActionBar(mToolbar);

        //Loading Indicator
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Create a DB helper (this will create the DB if run for the first time)
        FavouriteDbHelper dbHelper = new FavouriteDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding favourite movies

        db = dbHelper.getWritableDatabase();


        /*
         * setting up Spinner with sorting list
         */

        Spinner sortSpinner = (Spinner) findViewById(R.id.spinner_sorting);
        sortSpinner.setOnItemSelectedListener(this);

        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_list,R.layout.spinner_item);

        //Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Apply the adapter to the spinner
        sortSpinner.setAdapter(spinnerAdapter);

    }

    public static Cursor getAllMovies() {
        return db.query(FavouriteContract.FavouriteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * onItem selected from spinner
     * @param parent
     * @param view
     * @param position
     * @param id
     */

    @SuppressWarnings("JavaDoc")
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG,"onItemSelected");

        itemSelectedFromSpinner = parent.getItemAtPosition(position).toString();
        Log.i(TAG,"itemSelectedFromSpinner: "+ itemSelectedFromSpinner);

        loadMoviesData(itemSelectedFromSpinner);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void loadMoviesData(String sortOption) {

        Log.i(TAG,"loadMoviesData");
        if (Objects.equals(sortOption, "favourite")){

            // Get all movies info from the database and save in a cursor
            Cursor cursor = getAllMovies();

            /*
             * Setting up RecyclerView to hold favourite movies list
             */

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_adapter);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                Log.i(TAG,"PORTRAIT");
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,2)); //number of columns = 2
            }else{
                Log.i(TAG,"LANDSCAPE");
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,3)); //number of columns = 3
            }

            mRecyclerView.setHasFixedSize(true);
            mMoviesGridAdapter = new MoviesGridAdapter(this,cursor);
            mRecyclerView.setAdapter(mMoviesGridAdapter);

            Log.i(TAG,"favourite list adapted");

        }else{
            new FetchContentTask().execute(sortOption);

        /*
         * Setting up RecyclerView to hold movies list
         */

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_adapter);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                Log.i(TAG,"PORTRAIT");
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,2)); //number of columns = 2
            }else{
                Log.i(TAG,"LANDSCAPE");
                mRecyclerView.setLayoutManager(new GridLayoutManager(this,3)); //number of columns = 3
            }

            mRecyclerView.setHasFixedSize(true);

            mMoviesGridAdapter = new MoviesGridAdapter(this,null);
            mRecyclerView.setAdapter(mMoviesGridAdapter);
        }

    }



    @Override
    public void onItemClick(String movieDetails) {

        Intent intent = new Intent(HomeActivity.this,MovieDetailsActivity.class);
        intent.putExtra("MovieDetails", movieDetails);
        startActivity(intent);
    }

    private class FetchContentTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            errorMessage.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0){
                return null;
            }
            String sortOption = params[0];
            URL moviesDBRequestUrl = NetworkUtils.buildUrl(sortOption);
            Log.i(TAG,"moviesDBRequestUrl: "+moviesDBRequestUrl);
            try {

                String jsonMDBResponse = NetworkUtils.getResponseFromHttpUrl(moviesDBRequestUrl);
                Log.i(TAG,"jsonMDBResponse: "+jsonMDBResponse);

                String[] moviesDetails = MoviesJsonUtils.getMovieDetails(jsonMDBResponse);
                Log.i(TAG,"moviesDetails: "+ Arrays.toString(moviesDetails));
                return moviesDetails;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] moviesDetails) {

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (moviesDetails != null){
                mMoviesGridAdapter.setMDBData(moviesDetails);
            }else{
                errorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh){

            loadMoviesData(itemSelectedFromSpinner);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
