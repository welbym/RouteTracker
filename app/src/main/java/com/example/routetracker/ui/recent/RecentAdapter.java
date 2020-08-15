package com.example.routetracker.ui.recent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routetracker.R;
import com.example.routetracker.Route;

import java.util.ArrayList;

public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder> {

    private ArrayList<Route> routeArray;

    RecentAdapter(ArrayList<Route> routeArray) {
        this.routeArray = routeArray;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecentAdapter.RecentViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        return new RecentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_layout, parent, false));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecentViewHolder holder, int position) {
        holder.date.setText(routeArray.get(position).getDate().toString());
        String distanceString = routeArray.get(position).getDistance() + "";
        holder.distance.setText(distanceString);
        String timeString = routeArray.get(position).getTotalTime() + "";
        holder.time.setText(timeString);

    }

    @Override
    public int getItemCount() {
        return routeArray.size();
    }

    static class RecentViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView date, distance, time;
        RecentViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.recent_date);
            distance = itemView.findViewById(R.id.recent_distance);
            time = itemView.findViewById(R.id.recent_time);
        }
    }

}
