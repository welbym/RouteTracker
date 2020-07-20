package com.example.routetracker.ui.weather;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.routetracker.LocationReceiver;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.routetracker.R;
import com.squareup.picasso.Picasso;

public class WeatherFragment extends Fragment {

    private LocationReceiver receiver;

    public WeatherFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        receiver = (LocationReceiver) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_weather, container, false);
        final TextView weatherText = view.findViewById(R.id.weather);
        final ImageView weatherIcon = view.findViewById(R.id.icon);
        Location location = receiver.receiveLocation();

        // Create API call to OpenWeather
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?appid=" + getString(R.string.weather_access_token) +
                "&lat=" + location.getLatitude() + "&lon=" + location.getLongitude();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        // turns String response into a JsonObject
                        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);
                        String weatherString = jsonResponse.get("weather").toString();
                        JsonObject jsonWeatherObject = gson.fromJson(weatherString.substring(1,
                                weatherString.length() - 1), JsonObject.class);
                        weatherText.setText(jsonWeatherObject.get("main").getAsString());
                        Picasso.get().load("https://openweathermap.org/img/wn/" +
                                jsonWeatherObject.get("icon").getAsString() + "@2x.png").into(weatherIcon);
                        if (jsonWeatherObject.get("id").getAsInt() < 800) {
                            // Any ID below 800 is bad weather like rain, snow, thunderstorms
                            view.setBackgroundColor(getResources().getColor(R.color.badWeather,null));
                        } else if (jsonWeatherObject.get("id").getAsInt() == 800) {
                            // 800 ID means weather is clear
                            view.setBackgroundColor(getResources().getColor(R.color.goodWeather,null));
                        } else {
                            // Above 800 is clouds which may or may not be a problem
                            view.setBackgroundColor(getResources().getColor(R.color.midWeather,null));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                weatherText.setText(getString(R.string.ApiError));
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return view;
    }
}