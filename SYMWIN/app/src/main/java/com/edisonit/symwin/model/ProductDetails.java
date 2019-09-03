package com.edisonit.symwin.model;

public class ProductDetails {

    public String ShortName;
    public String IMEI1;
    public String SalesDate;


    public ProductDetails() {
        this.ShortName = ShortName;
        this.IMEI1 = IMEI1;
        this.SalesDate = SalesDate;
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

    public String getSalesDate() {
        return SalesDate;
    }

    public void setSalesDate(String salesDate) {
        SalesDate = salesDate;
    }
}
