package com.rbuys.android.mdb.movie_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rbuys.android.mdb.R;
import com.rbuys.android.mdb.utils.NetworkUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder>{

    private String[] mVideoInfo;

    private final ItemClickListener mItemClickListener;

    /**
     * The interface that receives on click listener
     */
    public interface ItemClickListener{
        void onItemClick(String trailerUrl);
    }

    public TrailerListAdapter(ItemClickListener clickListener){
        mItemClickListener = clickListener;
    }


    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_trailer_list,parent,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {

        //getting video details
        Log.i(TAG,"mVideoInfo: " + Arrays.toString(mVideoInfo));
        String videoInfo = mVideoInfo[position];
        String[] video = videoInfo.split(" , ");

        //setting play trailer text view

        int TYPE = 1;
        Log.i(TAG,"type: " + video[TYPE]);
        if (Objects.equals(video[TYPE], "Trailer")){
            int number = position + 1;
            holder.playTrailer.setText("Trailer" + " " + number );
        }

    }

    @Override
    public int getItemCount() {
        if (mVideoInfo == null) return 0;

        Log.i(TAG,"mVideoInfo: " + Arrays.toString(mVideoInfo));

        return mVideoInfo.length;


    }

    //Initializing the RecyclerView holders
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final Button playTrailer;

        TrailerViewHolder(View itemView) {
            super(itemView);

            playTrailer = (Button) itemView.findViewById(R.id.playTrailerTV);

            //onclick listener over imageView
            playTrailer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Onclick
            int adapterPosition = getAdapterPosition();
            String trailerDetails = mVideoInfo[adapterPosition];
            String[] trailer = trailerDetails.split(" , ");
            int KEY = 0;

            //get youtube url
            URL trailerLink = NetworkUtils.buildYoutubeUrl(trailer[KEY]);
            mItemClickListener.onItemClick(String.valueOf(trailerLink));

        }
    }

    /**
     * This method is used to set the details on a MoviesGridAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MoviesGridAdapter to display it.
     * @param trailerDetails
     */

    @SuppressWarnings("JavaDoc")
    void setTrailerData(String[] trailerDetails){

        mVideoInfo = trailerDetails;
        Log.i(TAG,"mVideoInfo: " + Arrays.toString(mVideoInfo));
        notifyDataSetChanged();

    }
}
