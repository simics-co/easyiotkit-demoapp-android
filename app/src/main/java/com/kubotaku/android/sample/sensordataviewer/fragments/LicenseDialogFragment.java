package com.kubotaku.android.sample.sensordataviewer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.kubotaku.android.sample.sensordataviewer.R;

/**
 * Created by kubotaku1119 on 16/04/25.
 */
public class LicenseDialogFragment extends AbsDialogFragment {

    public static LicenseDialogFragment newInstance() {
        final LicenseDialogFragment fragment = new LicenseDialogFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_license_dialog, container, false);
    }

    @Override
    protected float getDialogWidthRatio() {
        return 0.95f;
    }

    @Override
    protected void setupViews() {
        final View view = getView();

        final WebView webView = (WebView) view.findViewById(R.id.license_webview);
        webView.loadUrl("file:///android_asset/license.html");
    }
}
