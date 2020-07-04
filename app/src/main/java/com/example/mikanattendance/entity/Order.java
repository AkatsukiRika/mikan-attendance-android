package com.example.mikanattendance.entity;

import java.io.Serializable;

public class Order implements Serializable {
    private Integer ID;

    private Integer userID;

    private String orderSide;

    private String product;

    private Integer submitDate;

    private Integer endDate;

    private String remark;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(String orderSide) {
        this.orderSide = orderSide;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Integer submitDate) {
        this.submitDate = submitDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
