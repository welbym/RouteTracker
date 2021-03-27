package com.example.routetracker;

import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

public interface MapReceiver {
    void recordLandmark();
    void track(boolean tracking);
    OnMapReadyCallback receiveMapReadyCallback();
}
