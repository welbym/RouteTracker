package com.example.routetracker.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.colorpicker.ColorPickerDialog;
import com.github.dhaval2404.colorpicker.ColorPickerView;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.util.ColorUtil;
import com.github.dhaval2404.colorpicker.util.SharedPref;

import com.example.routetracker.R;

public class SettingsFragment extends Fragment {

    private int mColor = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        final AppCompatButton colorPickerBtn = root.findViewById(R.id.colorPickerBtn);
        final ColorPickerView colorPickerView = root.findViewById(R.id.colorPickerView);
        colorPickerView.setVisibility(View.GONE);
        int accentColor = ContextCompat.getColor(requireContext(), R.color.colorAccent);
        mColor = new SharedPref(requireContext()).getRecentColor(accentColor);
        setButtonBackground(colorPickerBtn, mColor);
        colorPickerBtn.setOnClickListener(view -> new ColorPickerDialog.Builder(requireActivity())
                .setTitle("Pick Accent")
                .setColorShape(ColorShape.SQAURE)
                .setDefaultColor(mColor)
                .setColorListener((color, colorHex) -> {
                    // Handle Color Selection
                    mColor = color;
                    colorPickerView.setColor(color);
                    setButtonBackground(colorPickerBtn, color);
                })
                .setDismissListener(() -> Log.d("ColorPickerDialog", "Handle dismiss event"))
                .show());
        return root;
    }

    private void setButtonBackground(AppCompatButton btn, int color) {
        if (ColorUtil.isDarkColor(color)) {
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setTextColor(Color.BLACK);
        }
        btn.setBackgroundTintList(ColorStateList.valueOf(color));
    }
}
