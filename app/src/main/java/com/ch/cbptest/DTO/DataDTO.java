package com.ch.cbptest.DTO;

public class DataDTO {
    private String id;
    private int SBP, DBP, MBP;

    public DataDTO() {}
    public DataDTO(int SBP, int DBP, int MBP) {
        this.SBP = SBP;
        this.DBP = DBP;
        this.MBP = MBP;
    }

    public void setSBP(int SBP) {
        this.SBP = SBP;
    }

    public void setMBP(int MBP) {
        this.MBP = MBP;
    }

    public void setDBP(int DBP) {
        this.DBP = DBP;
    }

    public int getDBP() {
        return DBP;
    }

    public int getMBP() {
        return MBP;
    }

    public int getSBP() {
        return SBP;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
