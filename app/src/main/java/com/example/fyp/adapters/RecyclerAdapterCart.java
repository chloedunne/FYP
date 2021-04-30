package com.example.fyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecyclerAdapterCart extends RecyclerView.Adapter<RecyclerAdapterCart.MyViewHolder>{

    private ArrayList<Product> productList;
    private RecyclerAdapterCart.RecyclerViewClickListener listener;
    private RecyclerAdapterCart.RecyclerViewOnLongClickListener longClickListener;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Cart").child(user.getUid());

    public RecyclerAdapterCart(ArrayList<Product> productList, RecyclerAdapterCart.RecyclerViewClickListener listener, RecyclerAdapterCart.RecyclerViewOnLongClickListener longClickListener){
        this.productList = productList;
        this.listener = listener;
        this.longClickListener = longClickListener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
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
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            longClickListener.onLongClick(v, getAdapterPosition());
            return true;
        }
    }

    public RecyclerAdapterCart.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_bag_layout, parent, false);
        RecyclerAdapterCart.MyViewHolder viewHolder = new RecyclerAdapterCart.MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(RecyclerAdapterCart.MyViewHolder holder, int position) {

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

    public void remove(int position){
        productList.remove(position);
        notifyItemRemoved(position);
    }

    public int getItemCount() {
        return productList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public interface RecyclerViewOnLongClickListener {
        void onLongClick(View v, int position);
    }

}
