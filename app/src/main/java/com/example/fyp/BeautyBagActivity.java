package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapterBeautyBag;
import com.example.fyp.objects.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class BeautyBagActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerAdapterBeautyBag.RecyclerViewClickListener clickListener;
    private ArrayList<Product> productList = new ArrayList<Product>();
    private RecyclerAdapterBeautyBag adapter;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_bag);
        Intent intent = getIntent();


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.beautyBagRCV);
        dbRef = FirebaseDatabase.getInstance().getReference("Beauty Bag").child(user.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product p = child.getValue(Product.class);
                    productList.add(p);
                }
                setOnClickListener();
                adapter = new RecyclerAdapterBeautyBag(productList, clickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(BeautyBagActivity.this));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BeautyBagActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapterBeautyBag.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Product product = productList.get(position);
                Intent i = new Intent(BeautyBagActivity.this, ProductActivity.class);
                i.putExtra("product", (Serializable) product);
                startActivity(i);

            }
        };
    }
}