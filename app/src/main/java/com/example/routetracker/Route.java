package com.example.routetracker;

import android.text.GetChars;

import java.util.ArrayList;

import com.mapbox.geojson.Point;

import java.sql.Date;
import java.sql.Timestamp;

public class Route {

    private Date date;
    private ArrayList<Timestamp> timestampArrayList;
    private ArrayList<Point> pointArray;

    Route(long currentTime) {
        date = new Date(currentTime);
        Timestamp timestamp = new Timestamp(currentTime);
        timestampArrayList = new ArrayList<>();
        timestampArrayList.add(timestamp);
    }

    public Date getDate() { return date; }

    public void addTimestamp(Timestamp timestamp) { timestampArrayList.add(timestamp); }

    public ArrayList<Timestamp> getTimeArray() { return timestampArrayList; }

        // get estimated total time from timestamps
    public long getTotalTime() {
        return 21;
    }

    public void setPointArray(ArrayList<Point> routeArray) { this.pointArray = routeArray; }

    public ArrayList<Point> getPointArray() {
        return pointArray;
    }

        // get estimated total distance
    public double getDistance() {
        return 6.9;
    }
}
