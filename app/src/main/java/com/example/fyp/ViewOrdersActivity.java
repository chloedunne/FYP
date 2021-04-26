package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fyp.adapters.RecyclerAdapterOrder;
import com.example.fyp.objects.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ViewOrdersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private RecyclerAdapterOrder.RecyclerViewClickListener clickListener;
    private RecyclerAdapterOrder adapter;
    private ArrayList<Order> orderList = new ArrayList<Order>();
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);
        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.orderHistoryRCV);

        setOnClickListener();
        adapter = new RecyclerAdapterOrder(orderList, clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewOrdersActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()){
                    Order o = child.getValue(Order.class);
                    if(o.getProfile().equalsIgnoreCase(user.getUid())){
                        orderList.add(o);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ViewOrdersActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener() {
        clickListener = new RecyclerAdapterOrder.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Order order = orderList.get(position);

                Intent i = new Intent(ViewOrdersActivity.this, OrderActivity.class);
                i.putExtra("order", (Serializable) order);
                startActivity(i);

            }
        };
    }
}