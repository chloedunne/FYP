package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Product> productList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter(ArrayList<Product> productList, RecyclerViewClickListener listener){
        this.productList = productList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView productNameTxt;
        public TextView brandNameTxt;
        public TextView priceTxt;
        public ImageView productImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            productNameTxt = itemView.findViewById(R.id.productNameTxt);
            brandNameTxt = itemView.findViewById(R.id.brandNameTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            productImg = itemView.findViewById(R.id.productImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        DecimalFormat df = new DecimalFormat("###.##");

        holder.productNameTxt.setText(currentProduct.getName());
        holder.brandNameTxt.setText(currentProduct.getBrand());
        holder.priceTxt.setText("€" + String.valueOf(df.format(currentProduct.getPrice())));
        Picasso.get().load(currentProduct.getImg()).into(holder.productImg);
    }

    public int getItemCount() {
        return productList.size();
    }


    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
}

}