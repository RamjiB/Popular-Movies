package com.rbuys.android.mdb.movie_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rbuys.android.mdb.R;

import static android.content.ContentValues.TAG;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>{

    private String[] mReviewInfo;

    private Context mContext;

    public ReviewListAdapter(Context context){
        mContext = context;
    }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_review,parent,false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        //getting review details
        String reviewInfo = mReviewInfo[position];
        String[] review = reviewInfo.split(" , ");

        Log.i(TAG,"authorname: "+ review[0]);
        Log.i(TAG,"authorcontent: "+ review[1]);

        try{
            //setting author name

            int AUTHOR = 0;
            holder.authorName.setText(review[AUTHOR]);

            //setting author content

            int CONTENT = 1;
            holder.authorContent.setText(review[CONTENT]);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if (mReviewInfo == null) return 0;
        return mReviewInfo.length;
    }

    //Initializing the RecyclerView holders
    class ReviewViewHolder extends RecyclerView.ViewHolder{

        final TextView authorName,authorContent;

        ReviewViewHolder(View itemView) {
            super(itemView);

            authorName = (TextView) itemView.findViewById(R.id.author_name);
            authorContent = (TextView) itemView.findViewById(R.id.author_content);

        }


    }

    /**
     * This method is used to set the details on a MoviesGridAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MoviesGridAdapter to display it.
     * @param reviewDetails
     */

    @SuppressWarnings("JavaDoc")
    void setReviewData(String[] reviewDetails){

        mReviewInfo = reviewDetails;
        notifyDataSetChanged();

    }
}
