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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

import com.kubotaku.android.sample.sensordataviewer.AppPreferences;
import com.kubotaku.android.sample.sensordataviewer.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Fragment for select get data start time and end time.
 */
public class SelectStreamTimeFragment extends AbsDialogFragment implements OnDialogFragmentDismissListener {

    private int selectTimeType;

    private int selectTimePresetIndex;

    private long selectTimeFrom;

    private long selectTimeTo;

    private long oldSelectTimeFrom;

    private long oldSelectTimeTo;

    public static SelectStreamTimeFragment newInstance() {
        final SelectStreamTimeFragment fragment = new SelectStreamTimeFragment();
        return fragment;
    }

    public SelectStreamTimeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_stream_time, container, false);
    }

    @Override
    protected float getDialogWidthRatio() {
        return 0.95f;
    }

    @Override
    protected void setupViews() {

        loadSelectTimeSettings();

        final View v = getView();

        RadioGroup rgType = (RadioGroup) v.findViewById(R.id.select_time_rg_type);
        rgType.setOnCheckedChangeListener(onTypeCheckedChangeListener);

        switch (this.selectTimeType) {
            default:
            case AppPreferences.SELECT_TIME_TYPE_PRESET:
                rgType.check(R.id.select_time_rb_preset);
                break;

            case AppPreferences.SELECT_TIME_TYPE_DETAIL:
                rgType.check(R.id.select_time_rb_detail);
                break;
        }

        // ---------------
        // Preset

        RadioGroup rgPreset = (RadioGroup) v.findViewById(R.id.select_time_rg_preset);
        rgPreset.setOnCheckedChangeListener(onPresetCheckedChangeListener);

        switch (this.selectTimePresetIndex) {
            default:
            case AppPreferences.TIME_PRESET_1HOUR:
                rgPreset.check(R.id.select_time_rb_1hour);
                break;

            case AppPreferences.TIME_PRESET_3HOUR:
                rgPreset.check(R.id.select_time_rb_3hour);
                break;

            case AppPreferences.TIME_PRESET_6HOUR:
                rgPreset.check(R.id.select_time_rb_6hour);
                break;

            case AppPreferences.TIME_PRESET_12HOUR:
                rgPreset.check(R.id.select_time_rb_12hour);
                break;

            case AppPreferences.TIME_PRESET_24HOUR:
                rgPreset.check(R.id.select_time_rb_24hour);
                break;
        }

        // ---------------
        // Detail

        Button btnDateFrom = (Button) v.findViewById(R.id.select_time_btn_date_from);
        btnDateFrom.setOnClickListener(onClickDateTimeSelectBtnListener);

        Button btnTimeFrom = (Button) v.findViewById(R.id.select_time_btn_time_from);
        btnTimeFrom.setOnClickListener(onClickDateTimeSelectBtnListener);

        Button btnDateTo = (Button) v.findViewById(R.id.select_time_btn_date_to);
        btnDateTo.setOnClickListener(onClickDateTimeSelectBtnListener);

        Button btnTimeTo = (Button) v.findViewById(R.id.select_time_btn_time_to);
        btnTimeTo.setOnClickListener(onClickDateTimeSelectBtnListener);

        updateDateTimeBtnText();

        // ---------------

        Button btnOk = (Button) v.findViewById(R.id.select_time_btn_ok);
        btnOk.setOnClickListener(onClickOkCancelBtnListener);

        Button btnCancel = (Button) v.findViewById(R.id.select_time_btn_cancel);
        btnCancel.setOnClickListener(onClickOkCancelBtnListener);
    }

    private void updateDateTimeBtnText() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        Date dateFrom = new Date(this.selectTimeFrom);
        Date dateTo = new Date(this.selectTimeTo);

        final View v = getView();

        Button btnDateFrom = (Button) v.findViewById(R.id.select_time_btn_date_from);
        btnDateFrom.setText(sdfDate.format(dateFrom));

        Button btnTimeFrom = (Button) v.findViewById(R.id.select_time_btn_time_from);
        btnTimeFrom.setText(sdfTime.format(dateFrom));

        Button btnDateTo = (Button) v.findViewById(R.id.select_time_btn_date_to);
        btnDateTo.setText(sdfDate.format(dateTo));

        Button btnTimeTo = (Button) v.findViewById(R.id.select_time_btn_time_to);
        btnTimeTo.setText(sdfTime.format(dateTo));
    }

    private RadioGroup.OnCheckedChangeListener onTypeCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                case R.id.select_time_rb_preset:
                    changeType(AppPreferences.SELECT_TIME_TYPE_PRESET);
                    break;

                case R.id.select_time_rb_detail:
                    changeType(AppPreferences.SELECT_TIME_TYPE_DETAIL);
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener onPresetCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                default:
                case R.id.select_time_rb_1hour:
                    changePreset(AppPreferences.TIME_PRESET_1HOUR);
                    break;

                case R.id.select_time_rb_3hour:
                    changePreset(AppPreferences.TIME_PRESET_3HOUR);
                    break;

                case R.id.select_time_rb_6hour:
                    changePreset(AppPreferences.TIME_PRESET_6HOUR);
                    break;

                case R.id.select_time_rb_12hour:
                    changePreset(AppPreferences.TIME_PRESET_12HOUR);
                    break;

                case R.id.select_time_rb_24hour:
                    changePreset(AppPreferences.TIME_PRESET_24HOUR);
                    break;
            }
        }
    };


    private View.OnClickListener onClickDateTimeSelectBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.select_time_btn_date_from:
                    showDatePickerDialog(true);
                    break;

                case R.id.select_time_btn_date_to:
                    showDatePickerDialog(false);
                    break;

                case R.id.select_time_btn_time_from:
                    showTimePickerDialog(true);
                    break;

                case R.id.select_time_btn_time_to:
                    showTimePickerDialog(false);
                    break;

            }
        }
    };

    private void showDatePickerDialog(final boolean dateFrom) {
        final DateTimePickerFragment datePickerInstance
                = DateTimePickerFragment.createDatePickerInstance(dateFrom);
        datePickerInstance.setTargetFragment(this, 0);
        datePickerInstance.show(getFragmentManager(), null);
    }

    private void showTimePickerDialog(final boolean dateFrom) {
        final DateTimePickerFragment timePickerInstance
                = DateTimePickerFragment.createTimePickerInstance(dateFrom);
        timePickerInstance.setTargetFragment(this, 0);
        timePickerInstance.show(getFragmentManager(), null);
    }

    private void changeType(final int type) {
        final View view = getView();

        ViewGroup vgPreset = (ViewGroup) view.findViewById(R.id.select_time_rg_preset);
        ViewGroup vgDetail = (ViewGroup) view.findViewById(R.id.select_time_gl_detail);

        selectTimeType = type;
        switch (type) {
            case AppPreferences.SELECT_TIME_TYPE_PRESET:
                vgPreset.setVisibility(View.VISIBLE);
                vgDetail.setVisibility(View.GONE);
                break;

            case AppPreferences.SELECT_TIME_TYPE_DETAIL:
                vgPreset.setVisibility(View.GONE);
                vgDetail.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changePreset(final int preset) {
        selectTimePresetIndex = preset;
    }

    private void loadSelectTimeSettings() {
        final Context context = getContext();
        selectTimeType = AppPreferences.getSelectTimeType(context);
        selectTimePresetIndex = AppPreferences.getSelectTimePresetIndex(context);

        selectTimeFrom = AppPreferences.getSelectTimeFrom(context);
        if (selectTimeFrom == -1) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, -1);
            selectTimeFrom = c.getTime().getTime();
        }

        selectTimeTo = AppPreferences.getSelectTimeTo(context);
        if (selectTimeTo == -1) {
            Calendar c = Calendar.getInstance();
            selectTimeTo = c.getTime().getTime();
        }

        oldSelectTimeFrom = selectTimeFrom;
        oldSelectTimeTo = selectTimeTo;
    }

    private void saveSelectTimeSettings() {
        final Context context = getContext();
        AppPreferences.saveSelectTimeType(context, this.selectTimeType);
        AppPreferences.saveSelectTimePresetIndex(context, this.selectTimePresetIndex);
        AppPreferences.saveSelectTimeFrom(context, this.selectTimeFrom);
        AppPreferences.saveSelectTimeTo(context, this.selectTimeTo);
    }

    private void restoreTimeSettings() {
        final Context context = getContext();
        AppPreferences.saveSelectTimeFrom(context, this.oldSelectTimeFrom);
        AppPreferences.saveSelectTimeTo(context, this.oldSelectTimeTo);
    }

    private View.OnClickListener onClickOkCancelBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            switch (id) {
                case R.id.select_time_btn_ok:
                    saveSelectTimeSettings();
                    dismiss();
                    break;

                case R.id.select_time_btn_cancel:
                    restoreTimeSettings();
                    dismiss();
                    break;
            }

        }
    };

    @Override
    public void onDismissDialog(boolean needUpdate) {
        final Context context = getContext();
        selectTimeFrom = AppPreferences.getSelectTimeFrom(context);
        selectTimeTo = AppPreferences.getSelectTimeTo(context);
        updateDateTimeBtnText();
    }
}
