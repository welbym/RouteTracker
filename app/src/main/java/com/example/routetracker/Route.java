package com.example.routetracker;

import androidx.annotation.NonNull;
import java.util.ArrayList;

import com.mapbox.geojson.Point;

import java.sql.Date;
import java.sql.Timestamp;

public class Route {

    private Date date;
    private double distance;
    private long totalTime;
    private ArrayList<Timestamp> timestampArrayList;
    private ArrayList<Point> pointArray;

    /**
     * Default constructor
     * @param currentTime current time taken from system clock
     */
    Route(long currentTime) {
        date = new Date(currentTime);
        Timestamp timestamp = new Timestamp(currentTime);
        timestampArrayList = new ArrayList<>();
        timestampArrayList.add(timestamp);
    }

    public Date getDate() { return date; }

    public void addTimestamp(Timestamp timestamp) { timestampArrayList.add(timestamp); }
    /**
     * Constructor used for creating Routes from JSON
     */
    Route(String Json) {
        String[] brokenJson = Json.split(", ");
        date = Date.valueOf(brokenJson[0]);
        distance = Double.parseDouble(brokenJson[1]);
        totalTime = Integer.parseInt(brokenJson[2]);
    }

    public ArrayList<Timestamp> getTimeArray() { return timestampArrayList; }

        // get estimated total time from timestamps
    public long getTotalTime() {
        totalTime = 21;
        return totalTime;
    }

    void setPointArray(ArrayList<Point> pointArray) { this.pointArray = pointArray; }

    public ArrayList<String> getPointArrayString() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (Point point : pointArray) {
            stringArrayList.add(point.toJson());
        }
        return stringArrayList;
    }

        // get estimated total distance
    public double getDistance() {
        distance = 6.9;
        return distance;
    }

    public String toSave() {
        return date.toString() + ", " + distance + ", " + totalTime;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{"
                + "Points=" + getPointArrayString()
                + ", Date=" + date.toString()
                + ", Distance=" + distance
                + ", Time=" + totalTime
                + "}";
    }
}
