package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.Order;

import java.util.ArrayList;

public class RecyclerAdapterOrder extends RecyclerView.Adapter<RecyclerAdapterOrder.MyViewHolder> {

    private ArrayList<Order> orderList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapterOrder(ArrayList<Order> orderList, RecyclerViewClickListener listener){
        this.orderList = orderList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView orderNum;
        public TextView total;


        public MyViewHolder(View itemView) {
            super(itemView);

            orderNum = itemView.findViewById(R.id.orderNum);
            total = itemView.findViewById(R.id.orderPrice);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order current = orderList.get(position);

        holder.total.setText("Total: €" + String.valueOf(current.getTotal()));
        holder.orderNum.setText("Order Number: " + String.valueOf(current.getOrderNum()));
    }

    public int getItemCount() {
        return orderList.size();
    }


    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

}
