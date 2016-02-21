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
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.kubotaku.android.sample.sensordataviewer.R;

/**
 * Dialog common abstraction class.
 */
public abstract class AbsDialogFragment extends DialogFragment {

    protected OnDialogFragmentDismissListener onDialogFragmentDismissListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnDialogFragmentDismissListener) {
            onDialogFragmentDismissListener = (OnDialogFragmentDismissListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onDialogFragmentDismissListener = null;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Dialog dialog = getDialog();
        dialog.setCancelable(false);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogWidth = (int) (metrics.widthPixels * getDialogWidthRatio());

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = dialogWidth;
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDialogFragmentDismissListener != null) {
            onDialogFragmentDismissListener.onDismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        setupViews();
    }

    protected abstract float getDialogWidthRatio();

    protected abstract void setupViews();
}
