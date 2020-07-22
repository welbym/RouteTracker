package com.example.routetracker;

import java.util.ArrayList;

import com.mapbox.geojson.Point;

import java.sql.Timestamp;

class Route {

    private Timestamp start;
    private Timestamp end;
    private ArrayList<Point> routeArray;

    Route(Timestamp start, Timestamp end, ArrayList<Point> routeArray) {
        this.start = start;
        this.end = end;
        this.routeArray = routeArray;
    }

    Timestamp getStart() {
        return start;
    }

    Timestamp getEnd() {
        return end;
    }

    ArrayList<Point> getRouteArray() {
        return routeArray;
    }

}
