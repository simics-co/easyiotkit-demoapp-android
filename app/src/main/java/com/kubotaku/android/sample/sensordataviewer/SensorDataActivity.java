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

package com.kubotaku.android.sample.sensordataviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;
import com.kubotaku.android.sample.sensordataviewer.api.ScalenicsAccessor;
import com.kubotaku.android.sample.sensordataviewer.api.WeatherAPIWrapper;
import com.kubotaku.android.sample.sensordataviewer.fragments.OnDialogFragmentDismissListener;
import com.kubotaku.android.sample.sensordataviewer.fragments.SelectStreamTimeFragment;
import com.kubotaku.android.sample.sensordataviewer.model.ChannelEntity;
import com.kubotaku.android.sample.sensordataviewer.model.StreamEntity;
import com.kubotaku.android.sample.sensordataviewer.model.StreamValueEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity for Sensor Data view.
 */
public class SensorDataActivity extends AppCompatActivity
        implements OnDialogFragmentDismissListener {

    public static final String VIEW_NAME_HEADER_IMAGE = "view_name_header_image";

    private static final String PARAM_CHANNEL = "param_channel";

    private LineChart lineChart;

    private ChannelEntity channelEntity;

    private int channel;

    private String channelName;

    private String apiToken;

    private AppPreferences.WeatherPlaceInfo weatherPlaceSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set for activity transit
        ImageView topBackImageView = (ImageView) findViewById(R.id.data_view_image_back);
        ViewCompat.setTransitionName(topBackImageView, VIEW_NAME_HEADER_IMAGE);

        lineChart = (LineChart) findViewById(R.id.sensor_data_chart);

        Intent intent = getIntent();
        if (intent != null) {

            channelEntity = intent.getParcelableExtra(PARAM_CHANNEL);

            channel = channelEntity.getChannel();

            channelName = channelEntity.getChannel_name();
            getSupportActionBar().setTitle(channelName);

            apiToken = AppPreferences.getApiToken(this);

            weatherPlaceSettings = AppPreferences.getWeatherPlaceSettings(this);

            setupTopBackImage();
            getSensorData();
        }
    }

    private void setupTopBackImage() {
        ImageView topBackImageView = (ImageView) findViewById(R.id.data_view_image_back);
        final int sensorType = channelEntity.getSensorType();
        switch (sensorType) {
            case ChannelEntity.SENSOR_TEMPERATURE: {
                topBackImageView.setImageResource(R.mipmap.ic_temperature);
            }
            break;

            case ChannelEntity.SENSOR_OCCUPANCY:
                topBackImageView.setImageResource(R.mipmap.ic_launcher);
                break;

            case ChannelEntity.SENSOR_ROCKER_SWITCH:
                topBackImageView.setImageResource(R.mipmap.ic_bell_off);
                break;

            default:
                topBackImageView.setImageResource(R.mipmap.ic_launcher);
                break;
        }
    }

    private void getSensorData() {
        final int sensorType = channelEntity.getSensorType();
        switch (sensorType) {
            case ChannelEntity.SENSOR_TEMPERATURE: {

                getWeatherInformation();

                new GetStreamDataTask(GetStreamDataTask.TASK_NEWEST).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_AVG_LAST_HOUR).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_MIN_LAST_HOUR).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_MAX_LAST_HOUR).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_LAST_HOUR).execute();
            }
            break;

            case ChannelEntity.SENSOR_OCCUPANCY:
                disableAggegateValueArea();

                new GetStreamDataTask(GetStreamDataTask.TASK_NEWEST).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_LAST_HOUR).execute();
                break;

            case ChannelEntity.SENSOR_ROCKER_SWITCH:
                disableAggegateValueArea();

                new GetStreamDataTask(GetStreamDataTask.TASK_NEWEST).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_LAST_HOUR).execute();
                break;

            default:
                disableAggegateValueArea();

                new GetStreamDataTask(GetStreamDataTask.TASK_NEWEST).execute();
                new GetStreamDataTask(GetStreamDataTask.TASK_LAST_HOUR).execute();
                break;
        }
    }

    private void getWeatherInformation() {
        if (this.weatherPlaceSettings.useWeatherInfo) {
            final String apiKey = this.weatherPlaceSettings.apiKey;
            final WeatherAPIWrapper weatherAPIWrapper = new WeatherAPIWrapper(this, apiKey, onWeahterGetListener);
            weatherAPIWrapper.getWeather(this.weatherPlaceSettings.latitude, this.weatherPlaceSettings.longitude);
        }
    }

    // --------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sensor_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_select_stream_time:
                showSelectStreamTimeDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSelectStreamTimeDialog() {
        final SelectStreamTimeFragment fragment = SelectStreamTimeFragment.newInstance();
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onDismiss() {
        getSensorData();
    }

    // --------------------------------------

    private class GetStreamDataTask extends AsyncTask<Void, Void, StreamEntity> {

        public static final int TASK_NEWEST = 0;

        public static final int TASK_LAST_HOUR = 1;

        public static final int TASK_AVG_LAST_HOUR = 2;

        public static final int TASK_MIN_LAST_HOUR = 3;

        public static final int TASK_MAX_LAST_HOUR = 4;

        private int taskType;

        public GetStreamDataTask(final int taskType) {
            this.taskType = taskType;
        }

        @Override
        protected StreamEntity doInBackground(Void... params) {

            StreamEntity entity = null;

            final int selectTimeType = AppPreferences.getSelectTimeType(SensorDataActivity.this);
            final int selectTimePresetIndex = AppPreferences.getSelectTimePresetIndex(SensorDataActivity.this);
            final int presetTime = AppPreferences.convertTimePresetIndexToTime(selectTimePresetIndex);
            final long selectTimeFrom = AppPreferences.getSelectTimeFrom(SensorDataActivity.this);
            final long selectTimeTo = AppPreferences.getSelectTimeTo(SensorDataActivity.this);

            try {
                ScalenicsAccessor scalenicsAccessor = new ScalenicsAccessor(apiToken);

                switch (taskType) {
                    case TASK_NEWEST:
                        entity = scalenicsAccessor.getNewestStreamData("" + channel);
                        break;

                    case TASK_LAST_HOUR: {
                        if (selectTimeType == AppPreferences.SELECT_TIME_TYPE_PRESET) {
                            entity = scalenicsAccessor.getLastNHourStreamData("" + channel, presetTime);
                        } else {
                            entity = scalenicsAccessor.getSelectTimeStreamData("" + channel, selectTimeFrom, selectTimeTo);
                        }
                    }
                    break;

                    case TASK_AVG_LAST_HOUR: {
                        if (selectTimeType == AppPreferences.SELECT_TIME_TYPE_PRESET) {
                            entity = scalenicsAccessor.getLastNHourAvgStreamData("" + channel, presetTime);
                        } else {
                            entity = scalenicsAccessor.getSelectTimeAvgStreamData("" + channel, selectTimeFrom, selectTimeTo);
                        }
                    }
                    break;

                    case TASK_MIN_LAST_HOUR: {
                        if (selectTimeType == AppPreferences.SELECT_TIME_TYPE_PRESET) {
                            entity = scalenicsAccessor.getLastNHourMinStreamData("" + channel, presetTime);
                        } else {
                            entity = scalenicsAccessor.getSelectTimeMinStreamData("" + channel, selectTimeFrom, selectTimeTo);
                        }
                    }
                    break;

                    case TASK_MAX_LAST_HOUR: {
                        if (selectTimeType == AppPreferences.SELECT_TIME_TYPE_PRESET) {
                            entity = scalenicsAccessor.getLastNHourMaxStreamData("" + channel, presetTime);
                        } else {
                            entity = scalenicsAccessor.getSelectTimeMaxStreamData("" + channel, selectTimeFrom, selectTimeTo);
                        }
                    }
                    break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return entity;
        }

        @Override
        protected void onPostExecute(StreamEntity streamEntity) {
            if (isFinishing()) {
                return;
            }

            switch (taskType) {
                case TASK_NEWEST:
                    showNewestStreamData(streamEntity);
                    break;

                case TASK_LAST_HOUR:
                    showLastHourStreamData(streamEntity);
                    break;

                case TASK_AVG_LAST_HOUR:
                    showAverageStreamData(streamEntity);
                    break;

                case TASK_MIN_LAST_HOUR:
                    showMinStreamData(streamEntity);
                    break;

                case TASK_MAX_LAST_HOUR:
                    showMaxStreamData(streamEntity);
                    break;
            }

        }
    }

    private void showNewestStreamData(StreamEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getStream() == null) {
            return;
        }

        StreamValueEntity newestValues = entity.getStream().get(0);

        TextView textLastValue = (TextView) findViewById(R.id.sensor_data_text_last_value);
        textLastValue.setVisibility(View.VISIBLE);

        double newestDataValue = newestValues.getValue();
        showValue(newestDataValue, textLastValue);

        String lastUpdateTime = ScalenicsAccessor.convertISO8601ToNormalDateFormatText(newestValues.getAt());
        TextView textLastUpdate = (TextView) findViewById(R.id.sensor_data_text_last_update);
        textLastUpdate.setText(lastUpdateTime);
        setShowValueAnimation(textLastUpdate);
    }

    private void showAverageStreamData(StreamEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getResult() == null) {
            return;
        }

        StreamValueEntity avgValues = entity.getResult().get(0);

        TextView textAvgValue = (TextView) findViewById(R.id.sensor_data_text_avg);
        double avgValue = avgValues.getValue();
        showValue(avgValue, textAvgValue);
    }

    private void showMinStreamData(StreamEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getResult() == null) {
            return;
        }

        StreamValueEntity minValues = entity.getResult().get(0);

        TextView textMinValue = (TextView) findViewById(R.id.sensor_data_text_min);
        double minValue = minValues.getValue();
        showValue(minValue, textMinValue);
    }

    private void showMaxStreamData(StreamEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.getResult() == null) {
            return;
        }

        StreamValueEntity maxValues = entity.getResult().get(0);

        TextView textMaxValue = (TextView) findViewById(R.id.sensor_data_text_max);
        double maxValue = maxValues.getValue();
        showValue(maxValue, textMaxValue);
    }

    private void disableAggegateValueArea() {
        TextView textAvgValue = (TextView) findViewById(R.id.sensor_data_text_avg);
        textAvgValue.setVisibility(View.GONE);

        TextView textMaxValue = (TextView) findViewById(R.id.sensor_data_text_max);
        textMaxValue.setVisibility(View.GONE);

        TextView textMinValue = (TextView) findViewById(R.id.sensor_data_text_min);
        textMinValue.setVisibility(View.GONE);

        TextView labelAvgValue = (TextView) findViewById(R.id.sensor_data_label_avg);
        labelAvgValue.setVisibility(View.GONE);

        TextView labelMaxValue = (TextView) findViewById(R.id.sensor_data_label_max);
        labelMaxValue.setVisibility(View.GONE);

        TextView labelMinValue = (TextView) findViewById(R.id.sensor_data_label_min);
        labelMinValue.setVisibility(View.GONE);
    }

    private void showValue(double value, TextView showArea) {
        if (showArea == null) {
            return;
        }

        final int sensorType = channelEntity.getSensorType();
        switch (sensorType) {
            default:
            case ChannelEntity.SENSOR_TEMPERATURE:
                showArea.setText(String.format(Locale.getDefault(),
                        "%1$.1f %2$s", value, channelEntity.getValue1_unit()));
                break;

            case ChannelEntity.SENSOR_OCCUPANCY: {
                showArea.setText((value == 1) ? "ON" : "OFF");
            }
            break;

            case ChannelEntity.SENSOR_ROCKER_SWITCH:
                showArea.setVisibility(View.GONE);
                break;

        }

        setShowValueAnimation(showArea);
    }

    private void setShowValueAnimation(View view) {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(750);
        alphaAnimation.setFillAfter(true);
        view.setAnimation(alphaAnimation);
    }


    // --------------------------------------------

    private void setupChartView() {

        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm");

        String dateFromText;
        String dateToText;

        final int selectTimeType = AppPreferences.getSelectTimeType(SensorDataActivity.this);
        if (selectTimeType == AppPreferences.SELECT_TIME_TYPE_PRESET) {
            final int selectTimePresetIndex = AppPreferences.getSelectTimePresetIndex(SensorDataActivity.this);
            final int presetTime = AppPreferences.convertTimePresetIndexToTime(selectTimePresetIndex);

            Calendar calendar = Calendar.getInstance();
            final Date now = calendar.getTime();
            dateToText = sdf.format(now);

            calendar.add(Calendar.HOUR_OF_DAY, -presetTime);
            final Date startDate = calendar.getTime();
            dateFromText = sdf.format(startDate);
        } else {
            final long selectTimeFrom = AppPreferences.getSelectTimeFrom(SensorDataActivity.this);
            final long selectTimeTo = AppPreferences.getSelectTimeTo(SensorDataActivity.this);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(selectTimeFrom));
            dateFromText = sdf.format(calendar.getTime());

            calendar.setTime(new Date(selectTimeTo));
            dateToText = sdf.format(calendar.getTime());
        }

        TextView textTime = (TextView) findViewById(R.id.sensor_data_text_time);
        textTime.setText(
                String.format(Locale.getDefault(), "%1$s - %2$s", dateFromText, dateToText));

        lineChart.setDescription("");
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDoubleTapToZoomEnabled(true);
    }

    private LineData createLineData(StreamEntity entity) {

        List<String> xVals = new ArrayList<>();
        List<Entry> yVals = new ArrayList<>();

        List<StreamValueEntity> streamList = entity.getStream();
        int index = 0;
        for (StreamValueEntity value : streamList) {
            String time = ScalenicsAccessor.convertISO8601ToShortDateFormatText(value.getAt());
            xVals.add(time);

            float dataVal = (float) value.getValue();
            Entry entry = new Entry(dataVal, index);
            yVals.add(entry);

            index++;
        }

        LineDataSet dataSet = new LineDataSet(yVals, channelEntity.getValue1_name());
        dataSet.setLineWidth(3.0f);
        dataSet.setDrawCubic(false);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.color_chart));
        dataSet.setColor(ContextCompat.getColor(this, R.color.color_chart));

        return new LineData(xVals, dataSet);
    }

    private void showLastHourStreamData(StreamEntity entity) {
        setupChartView();

        if (entity == null) {
            return;
        }

        if (entity.getStream() == null) {
            return;
        }

        LineData lineData = createLineData(entity);
        lineData.setValueTextSize(11f);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                final float scaleX = viewPortHandler.getScaleX();
                if (scaleX > 3) {
                    return String.format(Locale.getDefault(), "%.1f", value);
                }
                return "";
            }
        });

        this.lineChart.setData(lineData);
        this.lineChart.setVisibility(View.VISIBLE);
        this.lineChart.setAutoScaleMinMaxEnabled(true);
        this.lineChart.setVisibleXRangeMinimum(3f);
        this.lineChart.invalidate();
        this.lineChart.animateX(1000);
    }

    // --------------------------------------------

    private WeatherAPIWrapper.OnWeatherListener onWeahterGetListener = new WeatherAPIWrapper.OnWeatherListener() {
        @Override
        public void onGetWeatherInfo(WeatherInfo weatherInfo) {
            showWeatherInfo(weatherInfo);
        }
    };

    private void showWeatherInfo(WeatherInfo weatherInfo) {

        ViewGroup cardView = (ViewGroup) findViewById(R.id.sensor_data_card_weather);
        cardView.setVisibility(View.VISIBLE);

        final String locationName = weatherInfo.getName();
        final float currentTemp = weatherInfo.getCurrentTempCelsius();
        final Bitmap icon = weatherInfo.getIcon();
        final int humidity = weatherInfo.getHumidity();
        final int pressure = weatherInfo.getPressure();

        TextView textLocation = (TextView) findViewById(R.id.sensor_data_text_weather_location);
        textLocation.setText(locationName);

        TextView textCurrentTemp = (TextView) findViewById(R.id.sensor_data_text_weather_current_temp);
        textCurrentTemp.setText(String.format(Locale.getDefault(), "%.1f℃", currentTemp));

        TextView textMinMaxTemp = (TextView) findViewById(R.id.sensor_data_text_weather_minmax_temp);
        textMinMaxTemp.setText(String.format(Locale.getDefault(), "%d％\n%dhPa", humidity, pressure));

        ImageView imageIcon = (ImageView) findViewById(R.id.sensor_data_img_weather);
        imageIcon.setImageBitmap(icon);
    }

    // --------------------------------------------

    public static Intent createIntent(Context context, final ChannelEntity channelEntity) {
        Intent intent = new Intent(context, SensorDataActivity.class);
        intent.putExtra(PARAM_CHANNEL, channelEntity);
        return intent;
    }
}
