package com.example.routetracker;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public interface MapReceiver {
    void recordLandmark();
    void fetchMapView(MapView mapView);
    void track(boolean tracking);
    OnMapReadyCallback receiveMapReadyCallback();
}
