package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.fyp.objects.Order;

import java.text.DecimalFormat;

public class OrderCompleteActivity extends AppCompatActivity {

    private Order order;
    private TextView orderNumber, orderAddress, orderTotal;
    private Button profileButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
        Intent i = getIntent();
        order = (Order) i.getSerializableExtra("order");

        orderNumber = findViewById(R.id.orderCompleteNumber);
        orderAddress = findViewById(R.id.orderCompleteAddress);
        orderTotal = findViewById(R.id.orderCompleteTotal);
        profileButton = findViewById(R.id.homeButton);


        DecimalFormat df = new DecimalFormat("###.##");

        orderNumber.setText("Order Number:  " + order.getOrderNum());
        orderAddress.setText("Shipping Address:  " + order.getAddress());
        orderTotal.setText("Order Total:  €" + String.valueOf(df.format(order.getTotal())));

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderCompleteActivity.this,ProfileActivity.class);
                startActivity(i);
            }
        });
    }
}