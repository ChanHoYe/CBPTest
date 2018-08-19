package com.ch.cbptest;

import android.support.annotation.NonNull;

import com.ch.cbptest.DTO.DataDTO;
import com.ch.cbptest.DTO.InfoDTO;

import java.util.ArrayList;

public class Events {
    private final InfoDTO infoDTO;
    private final ArrayList<DataDTO> dataList;

    public Events(@NonNull InfoDTO infoDTO, @NonNull ArrayList<DataDTO> dataList) {
        this.infoDTO = infoDTO;
        this.dataList = dataList;
    }

    public InfoDTO getInfoDTO() {
        return infoDTO;
    }

    public ArrayList<DataDTO> getDataList() {
        return dataList;
    }
}
