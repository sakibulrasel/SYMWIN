package com.edisonit.symwin.model;

public class LiftingDetails {

    public String ShortName;
    public String IMEI1;
    public String LiftingDate;


    public LiftingDetails() {
        this.ShortName = ShortName;
        this.IMEI1 = IMEI1;
        this.LiftingDate = LiftingDate;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setShortName(String shortName) {
        ShortName = shortName;
    }

    public String getIMEI1() {
        return IMEI1;
    }

    public void setIMEI1(String IMEI1) {
        this.IMEI1 = IMEI1;
    }

    public String getLiftingDate() {
        return LiftingDate;
    }

    public void setLiftingDate(String liftingDate) {
        LiftingDate = liftingDate;
    }
}
