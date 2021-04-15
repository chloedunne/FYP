package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.fyp.objects.Product;
import com.example.fyp.objects.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    private Product product;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button submitReview;
    private DatabaseReference dbRef;
    private RatingBar ratingBar;
    private EditText reviewDescription;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("product");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userID = user.getUid();


        submitReview = findViewById(R.id.submitReviewButton);
        ratingBar = findViewById(R.id.rating_bar);
        reviewDescription = findViewById(R.id.editTextReview);


        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double rating = ratingBar.getRating();
                String text = reviewDescription.getText().toString().trim();
                Review review = new Review(product, userID, text, rating);

                dbRef = FirebaseDatabase.getInstance().getReference("Reviews");
                String keyId = dbRef.push().getKey();
                dbRef.child(keyId).setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ReviewActivity.this, "Review submitted", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ReviewActivity.this, ProfileActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

    }
}