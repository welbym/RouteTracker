package com.example.routetracker.ui.weather;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.routetracker.R;
import com.example.routetracker.WeatherReceiver;
import com.squareup.picasso.Picasso;

public class WeatherFragment extends Fragment {

    private WeatherReceiver receiver;

    public WeatherFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        receiver = (WeatherReceiver) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_weather, container, false);
        final TextView weatherText = view.findViewById(R.id.weather);
        final ImageView weatherIcon = view.findViewById(R.id.icon);
        weatherText.setText(receiver.receiveText());
        Picasso.get().load(receiver.receiveIcon()).into(weatherIcon);
        view.setBackgroundColor(receiver.receiveColor());
        return view;
    }
}