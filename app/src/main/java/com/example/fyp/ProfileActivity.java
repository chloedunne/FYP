package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.objects.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private ImageButton browseBtn, beautyBagBtn, reviewBtn, shadeMatchBtn;
    private Uri imageUri;
    private TextView usernameText;
    private DatabaseReference userReference;
    private StorageReference storageReference, profileReference;
    private FirebaseUser user;
    private Profile current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();

        profilePic = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        browseBtn = findViewById(R.id.browseProductsBtn);
        beautyBagBtn = findViewById(R.id.BeautyBagBtn);
        reviewBtn = findViewById(R.id.reviewBtn);
        shadeMatchBtn = findViewById(R.id.shadeMatchBtn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference =  FirebaseDatabase.getInstance().getReference("Profiles").child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference();
        profileReference = storageReference.child(user.getUid() + ".jpg");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                current = snapshot.getValue(Profile.class);
                usernameText.setText( current.getUsername());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error unable to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profilePic);
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        browseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, BrowseProductsActivity.class);
                startActivity(intent);
            }
        });

        beautyBagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, BeautyBagActivity.class);
                startActivity(intent);
            }
        });

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ViewReviewActivity.class);
                startActivity(intent);
            }
        });

        shadeMatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, ShadeMatchActivity.class);
                startActivity(intent);
            }
        });
    }

    public void selectPicture(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data.getData() != null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);

            uploadPicture();
        }
    }

    public void  uploadPicture() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        StorageReference imgRef = storageReference.child(user.getUid() + ".jpg");

        imgRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Image Uploaded", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                progressDialog.setMessage(progressPercent + "%");
            }
        });
    }
}