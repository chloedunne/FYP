package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapterBeautyBag;
import com.example.fyp.objects.Product;
import com.example.fyp.objects.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private RecyclerAdapterBeautyBag.RecyclerViewClickListener clickListener;
    private ArrayList<Product> productList = new ArrayList<Product>();
    private RecyclerAdapterBeautyBag adapter;
    private DatabaseReference dbRef;
    private double total;
    private TextView totalTextView;
    private Button checkout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = getIntent();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.cartRCV);
        totalTextView = findViewById(R.id.totalPriceTextView);
        dbRef = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());
        checkout = findViewById(R.id.checkoutButton);

        setOnClickListener();
        adapter = new RecyclerAdapterBeautyBag(productList, clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(CartActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        DecimalFormat df = new DecimalFormat("###.##");


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product p = child.getValue(Product.class);
                    productList.add(p);
                    total = total + p.getPrice();
                    totalTextView.setText("Total: " + String.valueOf(df.format(total)));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, CheckoutActivity.class);
                i.putExtra("total", total);
                startActivity(i);
            }
        });

    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapterBeautyBag.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Product product = productList.get(position);
                Intent i = new Intent(CartActivity.this, ProductActivity.class);
                i.putExtra("product", (Serializable) product);
                startActivity(i);

            }
        };
    }
}
