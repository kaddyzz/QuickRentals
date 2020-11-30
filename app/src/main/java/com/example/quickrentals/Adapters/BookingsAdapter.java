package com.example.quickrentals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickrentals.ModelClasses.Booking;
import com.example.quickrentals.R;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private Context context;
    private List<Booking> bookingsList;
    private boolean isVendor = false;

    public BookingsAdapter(List<Booking> bookingsList, boolean isVendor) {
        this.bookingsList = bookingsList;
        this.isVendor = isVendor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Create a view from layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bookings,parent,false);

        BookingsAdapter.ViewHolder viewHolder = new BookingsAdapter.ViewHolder(view);

        context = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Booking bookingData = bookingsList.get(position);

        holder.buttonMultiTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Move to next

            }
        });

        holder.textViewCarMakeModel.setText(String.format("%s %s",bookingData.getCarMake(), bookingData.getCarModel()));
        holder.textViewBookingID.setText(String.format("#%s",bookingData.getUserID().substring(0,6)));

        holder.textViewStartDate.setText(bookingData.getPickUpDate());
        holder.textViewReturnDate.setText(bookingData.getReturnDate());

        holder.textViewPrice.setText(String.format("$%s",bookingData.getFinalPrice()));

        if (bookingData.getPaymentStatus().equals("Payment Pending"))
        {
            holder.imageViewPaymentStatus.setImageResource(R.drawable.paymentailed);
        }
        else
        {
            holder.imageViewPaymentStatus.setImageResource(R.drawable.p1);
        }

        if (isVendor)
        {
            if (bookingData.getBookingStatus().equals("0") && bookingData.getPaymentStatus().equals("Payment Pending"))
            {
                holder.textViewBookingStatus.setText(R.string.upcoming);
                holder.viewStatus.setBackgroundColor(0xFF03DAC5);
                holder.buttonMultiTask.setText("TAKE PAYMENT");
            }
            else if (bookingData.getBookingStatus().equals("0") && bookingData.getPaymentStatus().equals("Payment Complete"))
            {
                holder.textViewBookingStatus.setText(R.string.upcoming);
                holder.viewStatus.setBackgroundColor(0xFF03DAC5);
                holder.buttonMultiTask.setText("ISSUE CAR");
            }
            else if (bookingData.getBookingStatus().equals("1"))
            {
                holder.textViewBookingStatus.setText(R.string.ongoing);
                holder.viewStatus.setBackgroundColor(0xFF4CAF50);
                holder.buttonMultiTask.setText("PROCESS RETURN");
            }
            else
            {
                holder.textViewBookingStatus.setText("");
                holder.viewStatus.setBackgroundColor(0xFF9CA59C);
                holder.buttonMultiTask.setText("HELP");
            }
        }
        else
        {
            if (bookingData.getBookingStatus().equals("0"))
            {
                holder.textViewBookingStatus.setText(R.string.upcoming);
                holder.viewStatus.setBackgroundColor(0xFF03DAC5);
                holder.buttonMultiTask.setText("CANCEL");
            }
            else if (bookingData.getBookingStatus().equals("1"))
            {
                holder.textViewBookingStatus.setText(R.string.ongoing);
                holder.viewStatus.setBackgroundColor(0xFF4CAF50);
                holder.buttonMultiTask.setText("HELP");
            }
            else
            {
                holder.textViewBookingStatus.setText("");
                holder.viewStatus.setBackgroundColor(0xFF9CA59C);
                holder.buttonMultiTask.setText("HELP");
            }
        }

    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //Instantiate all view holder items
        TextView textViewCarMakeModel;
        TextView textViewBookingID;
        TextView textViewStartDate;
        TextView textViewReturnDate;
        TextView textViewPrice;
        TextView textViewBookingStatus;

        ImageView imageViewPaymentStatus;

        Button buttonMultiTask;

        View viewStatus;

        public ViewHolder(View itemView)
        {
            super(itemView);

            this.textViewCarMakeModel = itemView.findViewById(R.id.textViewCarMakeModel);
            this.textViewBookingID = itemView.findViewById(R.id.textViewBookingID);
            this.textViewStartDate = itemView.findViewById(R.id.textViewStart);
            this.textViewReturnDate = itemView.findViewById(R.id.textViewReturnDate);
            this.textViewPrice = itemView.findViewById(R.id.textViewPrice);
            this.textViewBookingStatus = itemView.findViewById(R.id.textViewBookingStatus);

            this.imageViewPaymentStatus = itemView.findViewById(R.id.imageViewPaymentStatus);

            this.buttonMultiTask = itemView.findViewById(R.id.buttonMultiTask);

            this.viewStatus = itemView.findViewById(R.id.viewStatus);
        }

    }


}
