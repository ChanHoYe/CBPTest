package com.ch.cbptest;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ch.cbptest.databinding.FragmentSettingBinding;

public class SettingTab extends Fragment {
    private static final String TAG = SettingTab.class.getSimpleName();
    private FragmentSettingBinding mBinding;

    public static Fragment newInstance() {
        SettingTab settingTab = new SettingTab();

        return settingTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);

        return mBinding.getRoot();
    }

}
