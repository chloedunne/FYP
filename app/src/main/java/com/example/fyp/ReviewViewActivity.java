package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.fyp.objects.Profile;
import com.example.fyp.objects.Review;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ReviewViewActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference userReference, rReference;
    private RecyclerView recyclerView;
    private String userID, noteID;
    private ArrayList<Review> reviewList;
    private TextView welcome;
    private Review review;
    private Profile currentProfile;
    private TextView usernameTextViewReview, descriptionTextView, productText, productBrandText, shadeText;
    private RatingBar ratingBarReviewReview;
    private ImageView productImage1, profilePic;
    private Button moreReviewsBtn;
    private StorageReference storageReference, profileReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_view);
        Intent intent = getIntent();
        review = (Review) intent.getSerializableExtra("review");

        productText = findViewById(R.id.productText);
        shadeText = findViewById(R.id.shadeTextView);
        productBrandText = findViewById(R.id.productBrandText);
        usernameTextViewReview = findViewById(R.id.usernameTextViewReview);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        ratingBarReviewReview = findViewById(R.id.ratingBarReviewReview);
        productImage1 = findViewById(R.id.productImage1);
        profilePic = findViewById(R.id.imageViewProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rReference = FirebaseDatabase.getInstance().getReference("Reviews");
        userReference =  FirebaseDatabase.getInstance().getReference("Profiles").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();
        profileReference = storageReference.child(user.getUid() + ".jpg");

        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        });
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentProfile = snapshot.getValue(Profile.class);
                usernameTextViewReview.setText("Reviewed by " + currentProfile.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(review.getProduct().getShade()!=null){
            shadeText.setText("Shade: " + review.getProduct().getShade().getName());
        }
        productText.setText(review.getProduct().getName());
        productBrandText.setText(review.getProduct().getBrand());
        descriptionTextView.setText(review.getDescription());
        ratingBarReviewReview.setRating((float) review.getReview());
        Picasso.get().load(review.getProduct().getImg()).into(productImage1);

    }

}
