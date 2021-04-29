package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.fyp.adapters.RecyclerAdapterBeautyBag;
import com.example.fyp.objects.Order;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private Order order;
    private TextView orderNumber, orderAddress, orderTotal;
    private RecyclerView recyclerView;
    private RecyclerAdapterBeautyBag adapter;
    private ArrayList productList;
    private RecyclerAdapterBeautyBag.RecyclerViewClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Intent intent = getIntent();
        order = (Order) intent.getSerializableExtra("order");
        productList = order.getProducts();

        orderNumber = findViewById(R.id.orderNumber);
        orderAddress = findViewById(R.id.orderAddress);
        orderTotal = findViewById(R.id.orderTotal);
        recyclerView = findViewById(R.id.rcvOrderDetails);

        DecimalFormat df = new DecimalFormat("###.##");

        orderNumber.setText("Order Number:  " + order.getOrderNum());
        orderAddress.setText("Shipping Address:  " + order.getAddress());
        orderTotal.setText("Order Total:  €" + String.valueOf(df.format(order.getTotal())));

        adapter = new RecyclerAdapterBeautyBag(productList, clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }
}