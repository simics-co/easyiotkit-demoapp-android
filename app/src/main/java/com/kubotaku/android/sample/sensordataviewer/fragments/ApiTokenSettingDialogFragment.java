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


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.kubotaku.android.sample.sensordataviewer.AppPreferences;
import com.kubotaku.android.sample.sensordataviewer.R;


/**
 * Setting Dialog of API token.
 */
public class ApiTokenSettingDialogFragment extends DialogFragment {

    private OnDialogFragmentDismissListener onDialogDismissListener;

    private String apiToken;

    private EditText editApiToken;

    /**
     * このFragmentのインスタンスを取得する。
     *
     * @return インスタンス。
     */
    public static ApiTokenSettingDialogFragment newInstance() {
        ApiTokenSettingDialogFragment fragment = new ApiTokenSettingDialogFragment();
        return fragment;
    }

    public ApiTokenSettingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadApiToken();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnDialogFragmentDismissListener) {
            this.onDialogDismissListener = (OnDialogFragmentDismissListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.onDialogDismissListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Dialog dialog = getDialog();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * 0.9);

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_apitoken_setting_dialog, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        setupViews();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (this.onDialogDismissListener != null) {
            this.onDialogDismissListener.onDismiss();
        }
    }

    private void setupViews() {
        View view = getView();

        editApiToken = (EditText) view.findViewById(R.id.settings_edit_api_token);
        editApiToken.setText(this.apiToken);

        Button btnOk = (Button) view.findViewById(R.id.settings_button_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveApiToken();
                dismiss();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.settings_button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void loadApiToken() {
        this.apiToken = AppPreferences.getApiToken(getContext());
    }

    private void saveApiToken() {
        String apiToken = this.editApiToken.getText().toString();
        AppPreferences.saveApiToken(getContext(), apiToken);
    }
}
