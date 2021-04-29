package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapterShades;
import com.example.fyp.objects.Product;
import com.example.fyp.objects.Review;
import com.example.fyp.objects.Shade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView brandText, ratingTextView, productText, favouriteText, shadeText, cartText, priceTextView, descriptionTextview;
    private Button favouriteButton, createReviewButton, tryOnButton, cartButton;
    private ArrayList<Shade> shadeList = new ArrayList<Shade>();
    private RatingBar ratingBarReviewReview;
    private RecyclerAdapterShades.RecyclerViewClickListener clickListener;
    private Product product;
    private boolean found = false;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference beautyBagRef, reviewRef, cartRef;
    private int reviewCounter = 0;
    private double reviewTotal = 0;
    private RecyclerAdapterShades adapter;
    private Product selectedProduct;
    private Shade selectedShade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("product");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        beautyBagRef = FirebaseDatabase.getInstance().getReference("Beauty Bag").child(user.getUid());
        cartRef = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());

        RecyclerView recyclerView = findViewById(R.id.shadeRCV);
        ratingTextView = findViewById(R.id.ratingTextView);
        ratingBarReviewReview = findViewById(R.id.ratingBarReviewReview);
        productImage = findViewById(R.id.productImage1);
        productText = findViewById(R.id.productText);
        brandText = findViewById(R.id.productBrandText);
        favouriteButton = findViewById(R.id.favouriteButton);
        favouriteText = findViewById(R.id.favouriteTextView);
        createReviewButton = findViewById(R.id.createReviewButton);
        tryOnButton = findViewById(R.id.tryOnButton);
        shadeText = findViewById(R.id.textViewShade);
        cartText = findViewById(R.id.cartTextView);
        cartButton = findViewById(R.id.addToCartButton);
        priceTextView = findViewById(R.id.priceTextView);
        descriptionTextview = findViewById(R.id.descriptionTextView);

        if (product.getShades() != null) {
            setOnClickListener();
            LinearLayoutManager horizontalLayoutManager
                    = new LinearLayoutManager(ProductActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(horizontalLayoutManager);
            adapter = new RecyclerAdapterShades(this, shadeList, clickListener);
            recyclerView.setAdapter(adapter);

            for (Shade s : product.getShades()) {
                shadeList.add(s);
                adapter.notifyDataSetChanged();
            }
            shadeText.setText("Shades");

            if (shadeList.isEmpty()) {
                selectedProduct = product;
                shadeText.setText("");
                checkIfInBeautyBag();
            }
        } else {
            selectedProduct = product;
            selectedShade = product.getShade();
            if(product.getShade()!= null) {
                shadeText.setText("Shade:" + product.getShade().getName());
            }
            checkIfInBeautyBag();
        }


        reviewRef = FirebaseDatabase.getInstance().getReference("Reviews");
        reviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Review r = child.getValue(Review.class);
                    if (r.getProduct().getId() == product.getId()) {
                        reviewTotal += r.getReview();
                        reviewCounter++;
                    }
                }
                ratingBarReviewReview.setRating((float) (reviewTotal / reviewCounter));
                ratingTextView.setText("Avg Rating (Based on " + reviewCounter + " Reviews)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductActivity.this, "Error loading page", Toast.LENGTH_SHORT).show();
            }
        });


        Picasso.get().load(product.getImg()).into(productImage);
        productText.setText(product.getName());
        brandText.setText(product.getBrand());
        descriptionTextview.setText(product.getDescription());
        priceTextView.setText("€ " + String.valueOf(product.getPrice()));


        createReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProduct != null) {
                    Intent i = new Intent(ProductActivity.this, ReviewActivity.class);
                    i.putExtra("product", (Serializable) selectedProduct);
                    startActivity(i);
                } else
                    Toast.makeText(ProductActivity.this, "Please select a product shade", Toast.LENGTH_LONG).show();
            }
        });


        tryOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProduct != null) {
                    Intent i = new Intent(ProductActivity.this, TryOnMakeupActivity.class);
                    i.putExtra("product", (Serializable) selectedProduct);
                    startActivity(i);
                } else
                    Toast.makeText(ProductActivity.this, "Please select a product shade", Toast.LENGTH_LONG).show();

            }
        });

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProduct != null) {
                    if (favouriteText.getText().toString().equalsIgnoreCase("Added to Beauty Bag!")) {
                        beautyBagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Product p = child.getValue(Product.class);
                                    if (p.getId() == selectedProduct.getId()) {
                                        if (p.getShade() == null) {
                                            removeFromBeautyBag(child.getKey());
                                        } else if (p.getShade().getName().equalsIgnoreCase(selectedShade.getName())) {
                                            removeFromBeautyBag(child.getKey());
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ProductActivity.this, "Error loading page", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (favouriteText.getText().toString().equalsIgnoreCase("Add to Beauty Bag")) {
                        addToBeautyBag();

                    }
                } else
                    Toast.makeText(ProductActivity.this, "Please select a product shade", Toast.LENGTH_LONG).show();
            }
        });


        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProduct != null) {
                    String keyId = cartRef.push().getKey();
                    cartRef.child(keyId).setValue(selectedProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProductActivity.this, selectedProduct.getName() + " added to Cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    Toast.makeText(ProductActivity.this, "Please select a product shade", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void removeFromBeautyBag(String ref) {
        beautyBagRef.child(ref).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                favouriteButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                favouriteText.setText("Add to Beauty Bag");
                Toast.makeText(ProductActivity.this, "Product removed from Beauty Bag", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addToBeautyBag() {
        String keyId = beautyBagRef.push().getKey();
        beautyBagRef.child(keyId).setValue(selectedProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                favouriteButton.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                favouriteText.setText("Added to Beauty Bag!");
                Toast.makeText(ProductActivity.this, selectedProduct.getName() + " added to Beauty Bag", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkIfInBeautyBag() {
        beautyBagRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product p = child.getValue(Product.class);
                    if (p.getId() == selectedProduct.getId()) {
                        if (p.getShade() == null) {
                            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                            favouriteText.setText("Added to Beauty Bag!");
                        } else if (p.getShade().getName().equalsIgnoreCase(selectedShade.getName())) {
                            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                            favouriteText.setText("Added to Beauty Bag!");
                        } else {
                            favouriteButton.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                            favouriteText.setText("Add to Beauty Bag");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductActivity.this, "Error loading page", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setOnClickListener() {
        clickListener = new RecyclerAdapterShades.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                selectedShade = shadeList.get(position);
                selectedShade.setId(product.getId());
                selectedProduct = new Product(product.getBrand(), product.getName(), product.getDescription(), product.getProductType(), product.getImg(), selectedShade, product.getId(), product.getPrice());
                adapter.setCheckedPosition(position);
                adapter.notifyDataSetChanged();
                checkIfInBeautyBag();
            }
        };
    }
}




