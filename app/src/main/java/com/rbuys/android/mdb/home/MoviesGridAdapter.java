package com.rbuys.android.mdb.home;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rbuys.android.mdb.R;
import com.rbuys.android.mdb.data.FavouriteContract;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

public class MoviesGridAdapter extends RecyclerView.Adapter<MoviesGridAdapter.MovieViewHolder>{

    private String[] mMoviesInfo;
    public static final String imageURL = "http://image.tmdb.org/t/p/w185/";

    private Cursor mCursor;
    private Context mContext;
    private final ItemClickListener mItemClickListener;

    /**
     * The interface that receives on click listener
     */
    interface ItemClickListener{
        void onItemClick(String movieDetails);
    }


    MoviesGridAdapter(ItemClickListener clickListener, Cursor cursor){
        mItemClickListener = clickListener;
        mCursor = cursor;
    }



    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_movies_list,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        if (mMoviesInfo != null){
            //getting movie details
            String movieInfo = mMoviesInfo[position];
            String[] movie = movieInfo.split(" , ");

            // setting Title in its text view
            int TITLE = 0;
            holder.movie_title.setText(movie[TITLE]);

            //setting movie poster image using picasso library

            int POSTER_PATH = 1;
            String imagePath = imageURL + movie[POSTER_PATH];
            Picasso.with(mContext).load(imagePath).into(holder.movie_poster_image);


        }else {
            if (!mCursor.moveToPosition(position)){
                return;
            }

            // setting Title in its text view
            String title = mCursor.getString(mCursor.getColumnIndex(
                                FavouriteContract.FavouriteEntry.COLUMN_TITLE));
            Log.i(TAG,"title: "+ title);
            holder.movie_title.setText(title);

            //setting movie poster image using picasso library
            String posterImage = mCursor.getString(
                                mCursor.getColumnIndex(
                                FavouriteContract.FavouriteEntry.COLUMN_IMAGE));
            String imagePath = imageURL + posterImage;
            Picasso.with(mContext).load(imagePath).into(holder.movie_poster_image);
        }

    }

    @Override
    public int getItemCount() {
        if (mMoviesInfo == null){
            if (mCursor != null){
                Log.i(TAG,"count: "+ mCursor.getCount());
                if (mCursor.getCount() == 0){
                    Toast.makeText(mContext, "No favourite movies", Toast.LENGTH_SHORT).show();
                }
                return mCursor.getCount();
            }
            return 0;
        }
        return mMoviesInfo.length;
    }

    //Initializing the RecyclerView holders
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView movie_poster_image;
        final TextView movie_title;

        MovieViewHolder(View itemView) {
            super(itemView);

            movie_poster_image = (ImageView) itemView.findViewById(R.id.movie_poster_image);
            movie_title = (TextView) itemView.findViewById(R.id.movie_title);

            //onclick listener over imageView
            movie_poster_image.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Onclick
            int adapterPosition = getAdapterPosition();
            Log.i(TAG,"adapterPosition: "+adapterPosition);
            if (mMoviesInfo != null) {
                String movieDetails = mMoviesInfo[adapterPosition];
                mItemClickListener.onItemClick(movieDetails);
            }else{
                mCursor.moveToPosition(adapterPosition);
                Log.i(TAG,"cursorPosition: "+mCursor.getPosition());
                String title = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_TITLE));
                String posterImage = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_IMAGE));
                String voteAverage = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_VOTE_AVERAGE));
                String releaseDate = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_RELEASE_DATE));
                String overview = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_OVERVIEW));
                String id = mCursor.getString(mCursor.getColumnIndex(
                        FavouriteContract.FavouriteEntry.COLUMN_ID));
                String movieDetails = title + " , " + posterImage + " , " + voteAverage +
                        " , " + releaseDate + " , " + overview + " , " + id;

                mItemClickListener.onItemClick(movieDetails);
            }


        }
    }

    /**
     * This method is used to set the details on a MoviesGridAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MoviesGridAdapter to display it.
     * @param moviesData
     */

    @SuppressWarnings("JavaDoc")
    void setMDBData(String[] moviesData){

        mMoviesInfo = moviesData;
        notifyDataSetChanged();

    }

    public void swapCursor(Cursor newCursor){
        // Always close the previous mCursor first
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }
}
