package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerAdapterReview extends RecyclerView.Adapter<RecyclerAdapterReview.MyViewHolder> {

    private ArrayList<Review> reviewList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapterReview(ArrayList<Review> reviewList, RecyclerViewClickListener listener){
        this.reviewList = reviewList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView productTextView;
        public TextView brandTextView;
        public TextView shadeNameTextView;
        public ImageView reviewImage;
        public RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);

            productTextView = itemView.findViewById(R.id.textViewProductNameRCV);
            brandTextView = itemView.findViewById(R.id.textViewBrandNameRCV);
            reviewImage = itemView.findViewById(R.id.imageViewReviewRCV);
            ratingBar = itemView.findViewById(R.id.ratingBarReview);
            shadeNameTextView = itemView.findViewById(R.id.shadeNameTextViewRCV);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public RecyclerAdapterReview.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(RecyclerAdapterReview.MyViewHolder holder, int position) {
        Review currentReview = reviewList.get(position);

        holder.productTextView.setText(currentReview.getProduct().getName());
        holder.brandTextView.setText(currentReview.getProduct().getBrand());
        holder.ratingBar.setRating((float) currentReview.getReview());
        Picasso.get().load(currentReview.getProduct().getImg()).into(holder.reviewImage);
        if(currentReview.getProduct().getShade()!= null)
        holder.shadeNameTextView.setText(currentReview.getProduct().getShade().getName());
    }

    public int getItemCount() {
        return reviewList.size();
    }

    public void addReview(Review review){
        reviewList.add(review);
        notifyItemInserted(reviewList.size());
    }
    public void remove(int position){
        reviewList.remove(position);
        notifyItemRemoved(position);
    }

    public void update(Review review, int position){
        reviewList.set(position, review);
        notifyItemChanged(position);
    }

public interface RecyclerViewClickListener{
        void onClick(View v, int position);
}

}