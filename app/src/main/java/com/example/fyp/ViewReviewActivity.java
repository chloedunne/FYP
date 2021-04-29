package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapter;
import com.example.fyp.adapters.RecyclerAdapterReview;
import com.example.fyp.objects.Product;
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

    private DatabaseReference rReference;
    private RecyclerView recyclerView;
    private ArrayList<Review> reviewList = new ArrayList<Review>();
    private ArrayList<Review> filteredList = new ArrayList<Review>();
    private RecyclerAdapterReview.RecyclerViewClickListener clickListener;
    private RecyclerAdapterReview adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        Intent intent = getIntent();

        recyclerView = findViewById(R.id.reviewsRCV);
        searchView = findViewById(R.id.searchReviews);

        rReference = FirebaseDatabase.getInstance().getReference("Reviews");

        setOnClickListener();
        adapter = new RecyclerAdapterReview(reviewList, clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewReviewActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        rReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Review review = child.getValue(Review.class);
                    reviewList.add(review);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewReviewActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredList.clear();

                for (Review r : reviewList) {
                    if (r.getProduct().getName().toLowerCase().contains(newText.toLowerCase()) || r.getProduct().getBrand().toLowerCase().contains(newText.toLowerCase())) {
                        filteredList.add(r);
                    }

                    RecyclerAdapterReview filteredAdapter = new RecyclerAdapterReview(filteredList, clickListener);
                    recyclerView.setAdapter(filteredAdapter);
                }
                return false;
            }
        });


    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapterReview.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Review r;
                if(filteredList.isEmpty()) {
                    r = reviewList.get(position);
                }else{
                    r = filteredList.get(position);
                }
                Intent intent = new Intent(ViewReviewActivity.this, ReviewViewActivity.class);
                intent.putExtra("review", (Serializable) r);
                startActivity(intent);
            }
        };
    }
}