package com.example.fyp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.ProductActivity;
import com.example.fyp.R;
import com.example.fyp.objects.Product;
import com.example.fyp.objects.Shade;


import java.util.ArrayList;

public class RecyclerAdapterShades extends RecyclerView.Adapter<RecyclerAdapterShades.MyViewHolder> {

    private RecyclerAdapterShades.RecyclerViewClickListener listener;
    private ArrayList<Shade> shadeList;
    private int checkedPosition;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView shadeColour;
        public TextView shadeName;
        public RelativeLayout shadeLayout;

        public MyViewHolder(View itemView) {
            super(itemView);


            shadeLayout = itemView.findViewById(R.id.shade_layout);
            shadeColour = itemView.findViewById(R.id.colour_shade);
            shadeName = itemView.findViewById(R.id.name_shade);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }

    public RecyclerAdapterShades(Context context, ArrayList<Shade> shadeList, RecyclerAdapterShades.RecyclerViewClickListener listener) {
        this.shadeList = shadeList;
        this.listener = listener;
    }


    @Override
    public RecyclerAdapterShades.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shade_layout, parent, false);
        RecyclerAdapterShades.MyViewHolder viewHolder = new RecyclerAdapterShades.MyViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterShades.MyViewHolder holder, int position) {

        Shade currentShade = shadeList.get(position);
        holder.shadeName.setText(currentShade.getName());
        holder.shadeColour.setBackgroundColor(Color.parseColor(currentShade.getColour()));

        if(position == checkedPosition)
           holder.shadeName.setBackgroundResource(R.drawable.selected_shade);
        else
            holder.shadeName.setBackgroundResource(0);

    }

    public void setCheckedPosition(int position){
        checkedPosition = position;
    }

    @Override
    public int getItemCount() {
       return shadeList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

}

