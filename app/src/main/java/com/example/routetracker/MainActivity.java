package com.example.routetracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener, WeatherReceiver, MapReceiver, RecentReceiver {

    private final String TAG = "Main Activity";

    private DrawerLayout drawer;

        // Variables used for MapFragment
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private Location location;
    private boolean firstLocationStored;
    public OnMapReadyCallback mapReadyCallback;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private boolean tracking;
    private ArrayList<Point> pointArray;

        // Variables used for WeatherFragment
    private String weatherText;
    private String degreeText;
    private String weatherIcon;
    private int weatherColor;

        // Variables for RecentFragment
    private ArrayList<Route> routes;
        // Temporary Route object
    private Route route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        setNavigation();
        mapReadyCallback = this;
        firstLocationStored = false;
        pointArray = new ArrayList<>();
        setRecentFragment();
    }

    private void setNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void setRecentFragment() {
        try {
            routes = new ArrayList<>();
            routes.add(new Route(System.currentTimeMillis()));
            Log.d(TAG,"Route date" + routes.get(0).getDate());
        } catch (Exception e) {
            Log.e(TAG,"Error with recent fragment", e);
        }
    }

        // Gets called once in onSuccess for the LocationEngine
    private void processWeatherRequest() {
        try {
                // Create API call to OpenWeather
                // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://api.openweathermap.org/data/2.5/weather?units=imperial&appid=" +
                    getString(R.string.weather_access_token) + "&lat=" + location.getLatitude() +
                    "&lon=" + location.getLongitude();

                // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        Gson gson = new Gson();
                            // turns String response into a JsonObject
                        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                        String weatherString = jsonResponse.get("weather").toString();
                        JsonObject jsonWeatherObject = gson.fromJson(weatherString.substring(1,
                                weatherString.length() - 1), JsonObject.class);
                        weatherText = jsonWeatherObject.get("main").getAsString();
                        weatherIcon = jsonWeatherObject.get("icon").getAsString();
                        int weatherID = jsonWeatherObject.get("id").getAsInt();
                        if (weatherID < 800) {
                                // Any ID below 800 is bad weather like rain, snow, thunderstorms
                            weatherColor = getResources().getColor(R.color.badWeather, null);
                        } else if (weatherID == 800) {
                                // 800 ID means weather is clear
                            weatherColor = getResources().getColor(R.color.goodWeather, null);
                        } else {
                                // Above 800 is clouds which may or may not be a problem
                            weatherColor = getResources().getColor(R.color.midWeather, null);
                        }
                        degreeText = jsonResponse.get("main").getAsJsonObject().get("temp").getAsString();
                    }, error -> {
                weatherText = getString(R.string.ApiError);
                weatherColor = Color.BLUE;
            });

                // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        // Returns false if the user is not moving
    boolean isMoving(Location lastLocation, Location currentLocation) {
        return !(lastLocation.getLongitude() == currentLocation.getLongitude()) ||
                !(lastLocation.getLatitude() == currentLocation.getLatitude());
    }

    private void drawRoute(ArrayList<Point> pointArray) {
        mapboxMap.setStyle(Style.OUTDOORS, style -> {
                // Create the LineString from the list of coordinates and then make a GeoJSON
                // FeatureCollection so we can add the line to our map as a layer.
            style.addSource(new GeoJsonSource("line-source",
                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                            LineString.fromLngLats(pointArray)
                    )})));
                // The layer properties for our line. This is where we make the line dotted, set the color, etc.
            style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                    PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineColor(getColor(R.color.colorAccent))
            ));
        });
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
        mapboxMap.setStyle(Style.OUTDOORS, style -> {
            enableLocationComponent(style);

            if (pointArray != null) {
                    // Create the LineString from the list of coordinates and then make a GeoJSON
                    // FeatureCollection so we can add the line to our map as a layer.
                style.addSource(new GeoJsonSource("line-source",
                        FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                LineString.fromLngLats(pointArray)
                        )})));
                    // The layer properties for our line. This is where we make the line dotted, set the color, etc.
                style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                        PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineColor(getColor(R.color.colorAccent))
                ));
            }
        });
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
            // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

                // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

                // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

                // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

                // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

                // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

                // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Please gib",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(this, "Not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Class used by MapBox map
     */
    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                    // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(location);
                }

                    // Store the new location for route tracing
                if (activity.tracking && activity.firstLocationStored) {
                    try {
                        Log.v(activity.TAG, activity.location + " " + location);
                        if (activity.isMoving(activity.location, location))
                            activity.pointArray.add(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
                        Log.d(activity.TAG, "routeArray size " + activity.pointArray.size());
                        activity.drawRoute(activity.pointArray);
                    } catch (Exception e) {
                        Log.e(activity.TAG, "Error in onSuccess", e);
                    }
                }

                activity.location = location;
                if (!activity.firstLocationStored) {
                    activity.firstLocationStored = true;
                        // need to get first location before getting weather info
                    activity.processWeatherRequest();
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity, exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Destroys the location engine to prevent leaks
     * If user is still tracking route info then it adds a timestamp
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        if (tracking) {
            assert route != null;
            route.addTimestamp(new Timestamp(System.currentTimeMillis()));
        }
    }

        /**
         * Triggers when tracking button is pressed
         * If there's no route for today then create a new one
         * Else add a timestamp for when the button was pressed
         * If tracking then start the location service so the user knows
         * Else stop location service and then set the route's point array and draw the route
         */
    @Override
    public void track(boolean tracking) {
        this.tracking = tracking;
        if (route == null) {
            route = new Route(System.currentTimeMillis());
        } else {
            route.addTimestamp(new Timestamp(System.currentTimeMillis()));
        }
        if (tracking) {
            startService(new Intent(this, LocationService.class));
            Log.d(TAG, "Started tracking");
        } else {
            stopService(new Intent(this, LocationService.class));
            Log.d(TAG, "Stopped tracking ");
            route.setPointArray(pointArray);
            drawRoute(pointArray);
        }
    }

    /**
     * For the map fragment (Used by MapBox)
     * @return map ready callback
     */
    @Override
    public OnMapReadyCallback receiveMapReadyCallback() {
        return mapReadyCallback;
    }

    /**
     * For weather fragment interface
     * @return weather condition
     */
    @Override
    public String receiveText() {
        return weatherText;
    }

    /**
     * For weather fragment interface
     * @return weather degrees
     */
    @Override
    public String receiveDegrees() {
        return degreeText + getString(R.string.fahren);
    }

    /**
     * For weather fragment interface
     * @return weather icon
     */
    @Override
    public String receiveIcon() {
        return weatherIcon;
    }

    /**
     * For weather fragment interface
     * @return weather fragment background color
     */
    @Override
    public int receiveColor() {
        return weatherColor;
    }

    /**
     * For recent fragment interface
     * @return ArrayList of routes to display
     */
    @Override
    public ArrayList<Route> receiveRoutes() {
        return routes;
    }

    /**
     * If back is pressed while the Navigation Drawer is open then it closes the drawer
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
