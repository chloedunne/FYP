package com.example.fyp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;
import com.example.fyp.objects.ShadeMatch;

import java.util.ArrayList;

public class RecyclerAdapterShadeMatches extends RecyclerView.Adapter<RecyclerAdapterShadeMatches.MyViewHolder> {

    private ArrayList<ShadeMatch> list;

    public RecyclerAdapterShadeMatches(ArrayList<ShadeMatch>  list){
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTxt, brandNameTxt, shadeTxt;


        public MyViewHolder(View itemView) {
            super(itemView);

            productNameTxt = itemView.findViewById(R.id.productTextViewSM);
            brandNameTxt = itemView.findViewById(R.id.brandTextViewSM);
            shadeTxt = itemView.findViewById(R.id.shadeTextView);
        }
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shade_match_layout, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);
        return viewHolder;
    }


    public void onBindViewHolder(MyViewHolder holder, int position) {
        ShadeMatch current = list.get(position);

        holder.productNameTxt.setText(current.getProduct());
        holder.brandNameTxt.setText(current.getBrand());
        holder.shadeTxt.setBackgroundColor(Color.parseColor(current.getHex()));

    }

    public int getItemCount() {
        return list.size();
    }

    public void addItemtoEnd(ShadeMatch shadeMatch){
        list.add(shadeMatch);
        notifyItemInserted(list.size());
    }


}