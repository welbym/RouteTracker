package com.example.routetracker;

import android.location.Location;
import android.util.SparseArray;

import java.sql.Timestamp;

public class Route {

    private Timestamp start;
    private Timestamp end;
    private SparseArray<Location> routeArray;

    public Route(Timestamp start, Timestamp end, SparseArray<Location> routeArray) {
        this.start = start;
        this.end = end;
        this.routeArray = routeArray;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEnd() {
        return end;
    }
}
