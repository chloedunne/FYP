package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapterReview;
import com.example.fyp.objects.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewReviewActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference uReference, rReference;
    private RecyclerView recyclerView;
    private String userID, noteID;
    private ArrayList<Review> reviewList;
    private TextView welcome;
    private RecyclerAdapterReview.RecyclerViewClickListener clickListener;
    private RecyclerAdapterReview adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        Intent intent = getIntent();

        recyclerView = findViewById(R.id.reviewsRCV);

        reviewList = new ArrayList<Review>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        rReference = FirebaseDatabase.getInstance().getReference("Reviews");
        userID = user.getUid();


        rReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Review review = child.getValue(Review.class);
                    reviewList.add(review);
                }
                setOnClickListener();
                adapter = new RecyclerAdapterReview(reviewList, clickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewReviewActivity.this));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewReviewActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });



    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapterReview.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Review r = reviewList.get(position);
                Intent intent = new Intent(ViewReviewActivity.this, ReviewViewActivity.class);
                intent.putExtra("review", (Serializable) r);
                startActivity(intent);
            }
        };
    }
}