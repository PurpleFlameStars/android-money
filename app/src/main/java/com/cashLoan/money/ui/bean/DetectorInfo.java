package com.cashLoan.money.ui.bean;

import com.ai.ocr.identity.utils.GsonUtils;

import java.io.Serializable;

public class DetectorInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private boolean isSuccess;
    private String imageFilePath;
    private String name1;
    private String name2;
    private String name3;
    private String curp;
    private String clave;
    private String brightness;
    private String sharpness;
    private String brightnessThreshold;
    private String sharpnessThreshold;


    private String frontImgUrl;
    private String backImgUrl;

    private String frontImgFilePath;
    private String backImgFilePath;




    public DetectorInfo() {
    }

    public String getFrontImgFilePath() {
        return frontImgFilePath;
    }

    public void setFrontImgFilePath(String frontImgFilePath) {
        this.frontImgFilePath = frontImgFilePath;
    }

    public String getBackImgFilePath() {
        return backImgFilePath;
    }

    public void setBackImgFilePath(String backImgFilePath) {
        this.backImgFilePath = backImgFilePath;
    }

    public String getFrontImgUrl() {
        return frontImgUrl;
    }

    public void setFrontImgUrl(String frontImgUrl) {
        this.frontImgUrl = frontImgUrl;
    }

    public String getBackImgUrl() {
        return backImgUrl;
    }

    public void setBackImgUrl(String backImgUrl) {
        this.backImgUrl = backImgUrl;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String var1) {
        this.message = var1;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public void setSuccess(boolean var1) {
        this.isSuccess = var1;
    }

    public String getImageFilePath() {
        return this.imageFilePath;
    }

    public void setImageFilePath(String var1) {
        this.imageFilePath = var1;
    }

    public String getName() {
        return this.name1 + " " + this.name2 + " " + this.name3;
    }

    public String getName1() {
        return this.name1;
    }

    public String getName2() {
        return this.name2;
    }

    public String getName3() {
        return this.name3;
    }

    public String getIdnum() {
        return this.curp;
    }

    public String getBrightness() {
        return this.brightness;
    }

    public String getSharpness() {
        return this.sharpness;
    }

    public String getBrightnessThreshold() {
        return this.brightnessThreshold;
    }

    public String getSharpnessThreshold() {
        return this.sharpnessThreshold;
    }

    public String toString() {
        return GsonUtils.a().toJson(this);
    }
}
