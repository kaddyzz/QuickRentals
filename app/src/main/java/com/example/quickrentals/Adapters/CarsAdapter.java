package com.example.quickrentals.Adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickrentals.CarsDetailsActivity;
import com.example.quickrentals.ModelClasses.Cars;
import com.example.quickrentals.R;

import java.util.List;

public class CarsAdapter extends RecyclerView.Adapter<CarsAdapter.ViewHolder> {

    private List<Cars> carsList;
    private Context context;
    private String startDate, endDate;

    public CarsAdapter(List<Cars> carsList, String startDate, String endDate)
    {
        this.carsList = carsList;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageCar;
        ImageView imageCarLogo;
        CardView cardViewCarBook;
        Button buttonBook;
        TextView textViewMakeModel;
        TextView textViewPrice;


        public ViewHolder(View itemView)
        {
            super(itemView);
            this.imageCar = itemView.findViewById(R.id.imageViewCarImage);
            this.imageCarLogo = itemView.findViewById(R.id.imageViewCarLogo);
            this.cardViewCarBook = itemView.findViewById(R.id.cardViewCarBook);
            this.buttonBook = itemView.findViewById(R.id.buttonBook);
            this.textViewMakeModel = itemView.findViewById(R.id.textViewCarMakeModel);
            this.textViewPrice = itemView.findViewById(R.id.textViewPrice);
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
    public void onBindViewHolder(@NonNull final CarsAdapter.ViewHolder holder, int position) {

        final Cars carData = carsList.get(position);

        //Set car details
        Glide.with(context).load(carData.getCarImage()).into(holder.imageCar);
        Glide.with(context).load(carData.getCarLogo()).into(holder.imageCarLogo);

        holder.textViewMakeModel.setText(String.format("%s %s", carData.getCarMake(), carData.getCarModel()));
        holder.textViewPrice.setText(String.format("$ %s", carData.getCarPrice()));


        holder.buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent moveWithData = new Intent( context, CarsDetailsActivity.class);

                // create the transition animation - the images in the layouts
                // of both activities are defined with android:transitionName="robot"
                ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation((Activity) context, holder.imageCar, CarsDetailsActivity.VIEW_NAME_HEADER_IMAGE);

                //Move to next
                moveWithData.putExtra("selectedCar",carData);
                moveWithData.putExtra("startDate",startDate);
                moveWithData.putExtra("endDate",endDate);

                ActivityOptions activityOptions = ActivityOptions
                        .makeSceneTransitionAnimation((Activity) context, holder.imageCar, CarsDetailsActivity.VIEW_NAME_HEADER_IMAGE);

                // Now we can start the Activity, providing the activity options as a bundle
                context.startActivity(moveWithData, activityOptions.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return carsList.size();
    }
}
