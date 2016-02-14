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


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DateTimePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int TYPE_PICKER_DATE = 0;

    private static final int TYPE_PICKER_TIME = 1;

    private static final int TIME_FROM = 0;

    private static final int TIME_TO = 1;

    private static final String ARGS_TYPE = "args_type";

    private static final String ARGS_TIME = "args_time";

    private int type;

    private int time;

    private Date selectedDate;

    private OnDialogFragmentDismissListener onDialogFragmentDismissListener;

    public static DateTimePickerFragment createDatePickerInstance(final boolean dateFrom) {
        DateTimePickerFragment fragment = new DateTimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_TYPE, TYPE_PICKER_DATE);
        args.putInt(ARGS_TIME, dateFrom ? TIME_FROM : TIME_TO);
        fragment.setArguments(args);
        return fragment;
    }

    public static DateTimePickerFragment createTimePickerInstance(final boolean dateFrom) {
        DateTimePickerFragment fragment = new DateTimePickerFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_TYPE, TYPE_PICKER_TIME);
        args.putInt(ARGS_TIME, dateFrom ? TIME_FROM : TIME_TO);
        fragment.setArguments(args);
        return fragment;
    }


    public DateTimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.type = getArguments().getInt(ARGS_TYPE);
            this.time = getArguments().getInt(ARGS_TIME);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        loadTime();

        if (type == TYPE_PICKER_DATE) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            c.setTime(this.selectedDate);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        } else {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            c.setTime(this.selectedDate);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final Fragment targetFragment = getTargetFragment();
        if ((targetFragment != null) && (targetFragment instanceof OnDialogFragmentDismissListener)) {
            this.onDialogFragmentDismissListener = (OnDialogFragmentDismissListener) targetFragment;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.onDialogFragmentDismissListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        final Calendar c = Calendar.getInstance();
        c.setTime(this.selectedDate);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.selectedDate = c.getTime();

        saveTime();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance();
        c.setTime(this.selectedDate);
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        this.selectedDate = c.getTime();

        saveTime();
    }

    private void loadTime() {
        if (time == TIME_FROM) {
            loadTimeFrom();
        } else if (time == TIME_TO) {
            loadTimeTo();
        }
    }

    private void loadTimeFrom() {
        final long timeFrom = AppPreferences.getSelectTimeFrom(getContext());
        if (timeFrom != -1) {
            this.selectedDate = new Date(timeFrom);
        } else {
            this.selectedDate = new Date();
        }
    }

    private void loadTimeTo() {
        final long timeTo = AppPreferences.getSelectTimeTo(getContext());
        if (timeTo != -1) {
            this.selectedDate = new Date(timeTo);
        } else {
            this.selectedDate = new Date();
        }
    }

    private void saveTime() {
        if (this.time == TIME_FROM) {
            saveTimeFrom();
        } else if (this.time == TIME_TO) {
            saveTimeTo();
        }

        if (onDialogFragmentDismissListener != null) {
            onDialogFragmentDismissListener.onDismiss();
        }
    }

    private void saveTimeFrom() {
        final long timeFrom = this.selectedDate.getTime();
        AppPreferences.saveSelectTimeFrom(getContext(), timeFrom);
    }

    private void saveTimeTo() {
        final long timeTo = this.selectedDate.getTime();
        AppPreferences.saveSelectTimeTo(getContext(), timeTo);
    }
}
