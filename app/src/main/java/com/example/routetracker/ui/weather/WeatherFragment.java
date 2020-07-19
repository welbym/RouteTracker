package com.example.routetracker.ui.weather;

import android.media.Image;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.routetracker.R;
import com.squareup.picasso.Picasso;

public class WeatherFragment extends Fragment {

    public WeatherFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        final TextView weatherText = view.findViewById(R.id.weather);
        final ImageView weatherIcon = view.findViewById(R.id.icon);

        // Create API call to OpenWeather
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://api.openweathermap.org/data/2.5/weather?appid=" + getString(R.string.weather_access_token) + "&lat=41.668041&lon=-87.799004";

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