package com.ch.cbptest;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ch.cbptest.Binding.AnalysisBind;
import com.ch.cbptest.DTO.DataDTO;
import com.ch.cbptest.databinding.FragmentHomeBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class HomeTab extends Fragment implements View.OnClickListener {
    private static final String TAG = HomeTab.class.getSimpleName();
    private FragmentHomeBinding mBinding;
    private ArrayList<DataDTO> mDatalist = new ArrayList<>();
    private static int minWidth, maxWidth;

    public static Fragment newInstance() {
        HomeTab homeTab = new HomeTab();

        return homeTab;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        mBinding.homeUid.setText(App.getUser().getDisplayName());

        DisplayMetrics disp = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        minWidth = (int)(disp.widthPixels * 0.5) - Utils.dpToPx(getContext(),5);
        maxWidth = (int)(disp.widthPixels * 0.75) - Utils.dpToPx(getContext(),15) - minWidth;

        setLayout();

        return mBinding.getRoot();
    }

    private void setLayout() {
        if(!mDatalist.isEmpty()) {
            DataDTO latest = mDatalist.get(0);

            for(DataDTO dataDTO : mDatalist) {
                if(Utils.compareDate(latest.getId(), dataDTO.getId()) < 0) {
                    latest = dataDTO;
                }
            }

            mBinding.setData(new AnalysisBind(latest.getSBP(), latest.getMBP(), latest.getDBP(), minWidth, maxWidth));

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
        setLayout();
    }

    @Override
    public void onClick(View v) {

    }
}