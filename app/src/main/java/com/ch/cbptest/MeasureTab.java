package com.ch.cbptest;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ch.cbptest.databinding.FragmentMeasureBinding;

public class MeasureTab extends Fragment {
    private static final String TAG = MeasureTab.class.getSimpleName();
    private FragmentMeasureBinding mBinding;

    public static Fragment newInstance() {
        MeasureTab measureTab = new MeasureTab();

        return measureTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_measure, container, false);

        return mBinding.getRoot();
    }
}
