package com.example.routetracker.ui.recent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.routetracker.R;
import com.example.routetracker.RecentReceiver;

public class RecentFragment extends Fragment {

    private RecentReceiver receiver;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        receiver = (RecentReceiver) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recent_recycler_view);
        recyclerView.setAdapter(new RecentAdapter(receiver.receiveRoutes()));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return root;
    }
}