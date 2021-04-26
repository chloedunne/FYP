package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.Product;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerAdapterBeautyBag extends RecyclerView.Adapter<RecyclerAdapterBeautyBag.MyViewHolder> {

    private ArrayList<Product> productList;
    private RecyclerAdapterBeautyBag.RecyclerViewClickListener listener;

    public RecyclerAdapterBeautyBag(ArrayList<Product> productList, RecyclerAdapterBeautyBag.RecyclerViewClickListener listener){
        this.productList = productList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView productNameTxt, brandNameTxt, shadeNameTxt;
        public ImageView productImg;
        public TextView priceTxt;


        public MyViewHolder(View itemView) {
            super(itemView);

            productNameTxt = itemView.findViewById(R.id.nameTextViewBB);
            brandNameTxt = itemView.findViewById(R.id.brandTextViewBB);
            shadeNameTxt = itemView.findViewById(R.id.shadeTextViewBB);
            productImg = itemView.findViewById(R.id.imageViewBB);
            priceTxt = itemView.findViewById(R.id.priceTextViewBB);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                return;
            }
            listener.onClick(v, getAdapterPosition());
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_bag_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(MyViewHolder holder, int position) {

        DecimalFormat df = new DecimalFormat("###.##");
        Product currentProduct = productList.get(position);

        holder.productNameTxt.setText(currentProduct.getName());
        holder.brandNameTxt.setText(currentProduct.getBrand());
        if(currentProduct.getShade()!=null) {
            holder.shadeNameTxt.setText(currentProduct.getShade().getName());
        }
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
