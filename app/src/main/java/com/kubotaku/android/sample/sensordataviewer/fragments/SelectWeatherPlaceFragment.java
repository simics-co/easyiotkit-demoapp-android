/**
 * Copyright 2016 kubotaku1119 <kubotaku1119@gmail.com>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kubotaku.android.sample.sensordataviewer.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.kubotaku.android.sample.sensordataviewer.AppPreferences;
import com.kubotaku.android.sample.sensordataviewer.R;


/**
 * Fragment for settings of show weather informations.
 */
public class SelectWeatherPlaceFragment extends AbsDialogFragment {

    public interface OnPermissionCheckListener {

        void onRequestPermission(String[] permissions, int requestCode);

    }

    private OnPermissionCheckListener onPermissionCheckListener;

    private static final int REQUEST_PLACE_PICKER = 1;

    private static final int REQUEST_PERMISSIONS = 2;

    private AppPreferences.WeatherPlaceInfo weatherPlaceSettings;

    private Place pickedPlace = null;

    public static SelectWeatherPlaceFragment newInstance() {
        final SelectWeatherPlaceFragment fragment = new SelectWeatherPlaceFragment();
        return fragment;
    }

    public SelectWeatherPlaceFragment() {
        // Required empty public constructor
    }

    private boolean checkPermissions() {
        final Activity activity = getActivity();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermissions() {
        if (onPermissionCheckListener != null) {
            onPermissionCheckListener.onRequestPermission(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS);
        }
    }

    public void onRequestPermissionResult() {
        if (!checkPermissions()) {
            Toast.makeText(getContext(), "位置情報へのアクセス許可が必要です", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadWeatherPlaceSettings();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnPermissionCheckListener) {
            onPermissionCheckListener = (OnPermissionCheckListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onPermissionCheckListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_weather_place, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                this.pickedPlace = PlacePicker.getPlace(getContext(), data);
                if (this.pickedPlace != null) {
                    final CharSequence name = this.pickedPlace.getName();
                    TextView textName = (TextView) getView().findViewById(R.id.select_weather_text_place);
                    textName.setText(name);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected float getDialogWidthRatio() {
        return 0.95f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void setupViews() {
        final View view = getView();

        CheckBox checkUseWeather = (CheckBox) view.findViewById(R.id.select_weather_check_use);
        checkUseWeather.setChecked(this.weatherPlaceSettings.useWeatherInfo);

        EditText editApiKey = (EditText) view.findViewById(R.id.select_weather_edit_api_key);
        editApiKey.setText(this.weatherPlaceSettings.apiKey);

        TextView textName = (TextView) view.findViewById(R.id.select_weather_text_place);
        textName.setText(this.weatherPlaceSettings.name);

        Button btnSelectPlace = (Button) view.findViewById(R.id.select_weather_btn_select);
        btnSelectPlace.setOnClickListener(onClickSelectPlaceBtnListener);

        Button btnOk = (Button) view.findViewById(R.id.select_weather_btn_ok);
        btnOk.setOnClickListener(onClickOkCancelBtnListener);

        Button btnCancel = (Button) view.findViewById(R.id.select_weather_btn_cancel);
        btnCancel.setOnClickListener(onClickOkCancelBtnListener);
    }

    private void loadWeatherPlaceSettings() {
        final Context context = getContext();
        weatherPlaceSettings = AppPreferences.getWeatherPlaceSettings(context);
    }

    private void saveWeatherPlaceSettings() {

        if (this.pickedPlace != null) {
            this.weatherPlaceSettings.name = this.pickedPlace.getName().toString();
            this.weatherPlaceSettings.latitude = this.pickedPlace.getLatLng().latitude;
            this.weatherPlaceSettings.longitude = this.pickedPlace.getLatLng().longitude;
        }

        final View view = getView();
        CheckBox checkUseWeather = (CheckBox) view.findViewById(R.id.select_weather_check_use);
        this.weatherPlaceSettings.useWeatherInfo = checkUseWeather.isChecked();

        EditText editApiKey = (EditText) view.findViewById(R.id.select_weather_edit_api_key);
        this.weatherPlaceSettings.apiKey = editApiKey.getText().toString();

        final Context context = getContext();
        AppPreferences.saveWeatherPlaceSettings(context, this.weatherPlaceSettings);
    }

    private void callPlacePicker() {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            final Activity context = getActivity();
            startActivityForResult(builder.build(context), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener onClickSelectPlaceBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callPlacePicker();
        }
    };

    private View.OnClickListener onClickOkCancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.select_weather_btn_ok:
                    saveWeatherPlaceSettings();
                    break;
            }

            dismiss();
        }
    };
}
