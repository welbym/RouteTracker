package com.example.routetracker.ui.map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.routetracker.R;
import com.example.routetracker.MapReceiver;
import com.github.dhaval2404.colorpicker.util.ColorUtil;
import com.github.dhaval2404.colorpicker.util.SharedPref;
import com.mapbox.mapboxsdk.maps.MapView;

public class MapFragment extends Fragment {

    private MapView mapView;
    private MapReceiver receiver;
    private boolean tracking;
    private ImageButton trackingButton;
    private ImageButton landmarkButton;

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
        receiver.fetchMapView(mapView);
        tracking = false;
        int accentColor = ContextCompat.getColor(requireContext(), R.color.colorAccent);
        int savedColor = new SharedPref(requireContext()).getRecentColor(accentColor);
        landmarkButton = view.findViewById(R.id.landmark_button);
        setButtonBackground(landmarkButton, savedColor);
        landmarkButton.setOnClickListener(v -> {
            receiver.recordLandmark();
            Log.v("MapFragment","Clicking marker button");
        });
        trackingButton = view.findViewById(R.id.tracking_button);
        setButtonBackground(trackingButton, savedColor);
        trackingButton.setOnClickListener(v -> {
            if (!tracking) {
                trackingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop, null));
                tracking = true;
            } else {
                trackingButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_start, null));
                tracking = false;
            }
            receiver.track(tracking);
        });
        return view;
    }

    private void setButtonBackground(ImageButton btn, int color) {
        if (ColorUtil.isDarkColor(color)) {
            btn.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            btn.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        }
        btn.setBackgroundTintList(ColorStateList.valueOf(color));
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