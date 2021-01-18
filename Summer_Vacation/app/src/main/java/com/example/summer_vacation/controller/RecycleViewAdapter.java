package com.example.summer_vacation.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.summer_vacation.R;
import com.example.summer_vacation.model.SummerVacationItems;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private static final String Tag = "RecycleView";
    private Context context;
    private ArrayList<SummerVacationItems> summerVacationItems;
    private static ClickListener clickListener;

    public void setOnItemClickListener(ClickListener clickListener) {
        RecycleViewAdapter.clickListener = clickListener;
    }
    public RecycleViewAdapter(Context context, ArrayList<SummerVacationItems> summerVacationItems) {
        this.context = context;
        this.summerVacationItems = summerVacationItems;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.activity_card__view, parent, false);
       return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(summerVacationItems.get(position).getName());
        if(!summerVacationItems.get(position).getRating().isEmpty()){
            String rate = summerVacationItems.get(position).getRating();
            String[] array = rate.split(":");
            holder.rating.setText("Good : " + array[0] + ", Better : " + array[1] + ", Best : " + array[2]);
        }
        holder.info.setText(summerVacationItems.get(position).getInfo());
        Glide.with(context)
                .load(summerVacationItems.get(position).getImageURL())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return summerVacationItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView name;
        TextView rating;
        TextView info;

       public ViewHolder(@NonNull View itemView) {
           super(itemView);
           imageView = itemView.findViewById(R.id.image);
           name = itemView.findViewById(R.id.name);
           rating = itemView.findViewById(R.id.rating);
           info = itemView.findViewById(R.id.info);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   clickListener.onItemClick(getAdapterPosition(), view);
               }
           });
       }
   }
}
