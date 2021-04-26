package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fyp.objects.Profile;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import org.jetbrains.annotations.NotNull;

public class UpdateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseUser user;
    private DatabaseReference reference;
    private StorageReference storageReference, profileReference;

    private Button update;

    private ImageView profilePic;

    private Uri imageUri;

    private EditText editTextUsername, editTextPassword, editTextFirstname, editTextLastname, editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Profiles");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String profileID = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        profileReference = storageReference.child(user.getUid() + ".jpg");

        profilePic = findViewById(R.id.updateProfileImage);
        editTextUsername = (EditText) findViewById(R.id.updateUsernameEditText);
        editTextPassword = (EditText) findViewById(R.id.updateEditTextTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.updateEditTextTextPassword2);
        editTextFirstname = (EditText) findViewById(R.id.updateFirstNameEditText);
        editTextLastname = (EditText) findViewById(R.id.updateSurnameEditText);
        update = (Button) findViewById(R.id.updateDetailsBtn);

        reference.child(profileID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile userProfile = snapshot.getValue(Profile.class);
                if (userProfile != null) {
                    editTextFirstname.setText(userProfile.getFirstName());
                    editTextPassword.setText(userProfile.getPassword());
                    editTextUsername.setText(userProfile.getUsername());
                    editTextLastname.setText(userProfile.getLastName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    public void selectPicture() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);

            uploadPicture();
        }
    }

    public void uploadPicture() {

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
                        Toast.makeText(UpdateActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage(progressPercent + "%");
            }
        });
    }


    public void updateProfile() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Profiles").child(userID);

        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String firstname = editTextFirstname.getText().toString().trim();
        String lastname = editTextLastname.getText().toString().trim();


        if (firstname.isEmpty()) {
            editTextFirstname.setError("Name is required");
            editTextFirstname.requestFocus();
            return;
        }

        if (lastname.isEmpty()) {
            editTextLastname.setError("Surname is required");
            editTextLastname.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Confirm Password is required");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!password.equalsIgnoreCase(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords must match");
            editTextPassword.requestFocus();
            editTextConfirmPassword.requestFocus();
            return;
        }

        FirebaseDatabase.getInstance().getReference("Profiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Profile p = child.getValue(Profile.class);
                    if (!child.getKey().equalsIgnoreCase(user.getUid())) {
                        if (p.getUsername().equalsIgnoreCase(username)) {
                            editTextUsername.setError("Username is already taken");
                            editTextUsername.requestFocus();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(UpdateActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        reference.child("firstName").setValue(firstname).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(UpdateActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        });
        reference.child("lastName").setValue(lastname).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(UpdateActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        });
        reference.child("username").setValue(username).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(UpdateActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        });
        reference.child("password").setValue(password).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(UpdateActivity.this, "Error updating details", Toast.LENGTH_SHORT).show();
            }
        });

        Intent i = new Intent(UpdateActivity.this, SettingsActivity.class);
        startActivity(i);

    }
}