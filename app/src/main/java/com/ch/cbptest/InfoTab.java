package com.ch.cbptest;

import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ch.cbptest.DTO.InfoDTO;
import com.ch.cbptest.databinding.FragmentInfoBinding;
import com.google.firebase.database.DatabaseReference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InfoTab extends Fragment implements View.OnClickListener {
    private static final String TAG = InfoTab.class.getSimpleName();
    private FragmentInfoBinding mBinding;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
    private DatabaseReference mRef;
    private InfoDTO infoDTO;

    public static Fragment newInstance() {
        InfoTab infoTab = new InfoTab();

        return infoTab;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendar = Calendar.getInstance();
        mRef = App.getDatabase().getReference("users").child(App.getUser().getUid()).child("info");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_info, container, false);

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mBinding.infoBirth.setText(df.format(calendar.getTime()));
            }
        };

        infoDTO = ((App)(getActivity().getApplication())).getInfoDTO();
        if(infoDTO != null)
            setValue();

        mBinding.infoBirth.setOnClickListener(this);
        mBinding.infoSubmit.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull Events events) {
        this.infoDTO = events.getInfoDTO();
        setValue();
    }

    private void setValue() {
        infoDTO = ((App)(getActivity().getApplication())).getInfoDTO();

        mBinding.infoName.setText(infoDTO.getName());
        try {
            calendar.setTime(df.parse(infoDTO.getBirth()));
            Log.e(TAG, "setValue: ");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mBinding.infoBirth.setText(df.format(calendar.getTime()));
        mBinding.infoHeight.setText(infoDTO.getHeight());
        mBinding.infoWeight.setText(infoDTO.getWeight());
        if(infoDTO.getGender().equals("male"))
            mBinding.infoGender.check(R.id.info_male);
        else
            mBinding.infoGender.check(R.id.info_female);
        mBinding.getRoot().invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_birth:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                datePickerDialog.show();
                break;
            case R.id.info_submit:
                infoDTO.setName(mBinding.infoName.getText().toString());
                infoDTO.setBirth(df.format(calendar.getTime()));
                infoDTO.setHeight(mBinding.infoHeight.getText().toString());
                infoDTO.setWeight(mBinding.infoWeight.getText().toString());
                if(mBinding.infoMale.isChecked() || mBinding.infoFemale.isChecked()) {
                    if(mBinding.infoMale.isChecked())
                        infoDTO.setGender("male");
                    else
                        infoDTO.setGender("female");
                }
                mRef.setValue(infoDTO);
                break;
        }
    }
}
