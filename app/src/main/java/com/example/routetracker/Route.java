package com.example.routetracker;

import java.util.ArrayList;

import com.mapbox.geojson.Point;

import java.sql.Date;
import java.sql.Timestamp;

class Route {

    private Date date;
    private ArrayList<Timestamp> timestampArrayList;
    private ArrayList<Point> routeArray;

    Route(Date date) {
        this.date = date;
        timestampArrayList = new ArrayList<>();
    }

    void addTimestamp(Timestamp timestamp) { timestampArrayList.add(timestamp); }

    void setRouteArray(ArrayList<Point> routeArray) { this.routeArray = routeArray; }

    ArrayList<Timestamp> getTimeArray() { return timestampArrayList; }

    ArrayList<Point> getRouteArray() {
        return routeArray;
    }

}
