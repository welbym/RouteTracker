package com.example.routetracker.ui.map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.routetracker.R;
import com.example.routetracker.MapReceiver;
import com.mapbox.mapboxsdk.maps.MapView;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapReceiver receiver;
    private boolean tracking;
    private Button trackingButton;

    public MapFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        receiver = (MapReceiver) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(receiver.receiveMapReadyCallback());
        tracking = false;
        trackingButton = view.findViewById(R.id.tracking_button);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tracking) {
                    trackingButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_stop, 0, 0);
                    tracking = true;
                } else {
                    trackingButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_start, 0, 0);
                    tracking = false;
                }
                receiver.track(tracking);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}