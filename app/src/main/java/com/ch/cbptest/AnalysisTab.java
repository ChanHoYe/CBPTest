package com.ch.cbptest;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.ch.cbptest.Binding.AnalysisBind;
import com.ch.cbptest.DTO.DataDTO;
import com.ch.cbptest.databinding.FragmentAnalysisBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mobi.gspd.segmentedbarview.Segment;

public class AnalysisTab extends Fragment {
    private static final String TAG = AnalysisTab.class.getSimpleName();
    private FragmentAnalysisBinding mBinding;
    private ArrayList<DataDTO> mDatalist = new ArrayList<>();
    private static int minWidth, maxWidth;
    int value = 80;

    public static Fragment newInstance() {
        AnalysisTab analysisTab = new AnalysisTab();

        return analysisTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_analysis, container, false);

        DisplayMetrics disp = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        minWidth = (int)(disp.widthPixels * 0.5) - Utils.dpToPx(getContext(),5);
        maxWidth = (int)(disp.widthPixels * 0.75) - Utils.dpToPx(getContext(),15) - minWidth;

        mBinding.analysisCal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                setLayout(Utils.calToString(c));
            }
        });

        List<Segment> segments = new ArrayList<>();
        Segment segment = new Segment(0, 80, "저혈압", Color.BLUE);
        segments.add(segment);
        Segment segment2 = new Segment(80, 120, "정상혈압", Color.GREEN);
        segments.add(segment2);
        Segment segment3 = new Segment(120, 160, "고혈압", Color.RED);
        segments.add(segment3);

        mBinding.analysisMbpProgress.setSegments(segments);
        mBinding.analysisMbpProgress.setUnit("mmHg");
        mBinding.analysisMbpProgress.setValue(120f);

        //mBinding.setData(new AnalysisBind(70,100,110, minWidth, maxWidth));

        //mBinding.invalidateAll();

        return mBinding.getRoot();
    }

    private void setLayout(String value) {
        if(!mDatalist.isEmpty()) {
            DataDTO toDisplay = null;
            boolean isExist = false;

            for(DataDTO dataDTO : mDatalist) {
                if(dataDTO.getId().equals(value)) {
                    toDisplay = dataDTO;
                    isExist = true;
                    break;
                }
            }

            if(toDisplay != null && isExist) {
                mBinding.setData(new AnalysisBind(toDisplay.getSBP(),toDisplay.getMBP(),toDisplay.getDBP(), minWidth, maxWidth));
                mBinding.analysisEmptyLayout.setVisibility(View.GONE);
                mBinding.analysisValueLayout.setVisibility(View.VISIBLE);
            } else {
                mBinding.analysisEmptyLayout.setVisibility(View.VISIBLE);
                mBinding.analysisValueLayout.setVisibility(View.GONE);
            }

            mBinding.invalidateAll();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull ArrayList<DataDTO> mDatalist) {
        this.mDatalist = mDatalist;
        Log.e(TAG, "onEvent: list updated");
    }
}
