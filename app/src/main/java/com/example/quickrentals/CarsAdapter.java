package com.example.quickrentals;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private List<Cars> carsList;
    private Context context;
    private int positionIndex = 0;


    public CarsAdapter(List<Cars> carsList)
    {
        this.carsList = carsList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imagePizza1;
        TextView textPizza1;
        CardView cardView1;


        public ViewHolder(View itemView)
        {
            super(itemView);
            this.imagePizza1 = itemView.findViewById(R.id.imageViewCar);
            this.cardView1 = itemView.findViewById(R.id.cardViewItem1);
        }

    }



    @NonNull
    @Override
    public CarsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cars,parent,false);

        CarsAdapter.ViewHolder viewHolder = new CarsAdapter.ViewHolder(view);

        context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarsAdapter.ViewHolder holder, int position) {

        //final Cars carData = carsList.get(positionIndex);

        //Set car name  and image
        //Glide.with(context).load(carData.getCarImage()).into(holder.imagePizza1);

        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Move to next
                //Intent moveWithData = new Intent( context, DetailsActivity.class);


                //context.startActivity(moveWithData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
