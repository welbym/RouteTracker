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
        final TextView degreeText = view.findViewById(R.id.degrees);
        final ImageView weatherIcon = view.findViewById(R.id.icon);
        weatherText.setText(receiver.receiveText());
        degreeText.setText(receiver.receiveDegrees());

        weatherIcon.setImageDrawable(getResources().getDrawable(getIcon(receiver.receiveIcon()), null));
        view.setBackgroundColor(receiver.receiveColor());
        return view;
    }

    private int getIcon(String icon) {
        // set ID to a default back-up value
        int ID = R.drawable.ic_stop;
        try {
            switch (icon) {
                case "01d":
                    ID = R.drawable.owi_01d;
                    break;
                case "01n":
                    ID = R.drawable.owi_01n;
                    break;
                case "02d":
                    ID = R.drawable.owi_02d;
                    break;
                case "02n":
                    ID = R.drawable.owi_02n;
                    break;
                case "03d":
                case "03n":
                case "04d":
                case "04n":
                    ID = R.drawable.owi_03_04;
                    break;
                case "09d":
                case "09n":
                    ID = R.drawable.owi_09;
                    break;
                case "10d":
                    ID = R.drawable.owi_10d;
                    break;
                case "11d":
                    ID = R.drawable.owi_11d;
                    break;
                case "13d":
                case "13n":
                    ID = R.drawable.owi_13;
                    break;
                case "50d":
                case "50n":
                    ID = R.drawable.owi_50;
                    break;
            }
        } catch (Exception e) {
            throw(e);
        }
        return ID;
    }
}