package com.example.mikanattendance.entity;

import java.io.Serializable;

public class User implements Serializable {
    private Integer id; // 要小写ID

    private String realName;

    private String userPass;

    private String phone;

    private String email;

    // 枚举类型，可取"PM", "HR", "SALES", "OTHERS"
    private String userType;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getId() {
        return id;
    }

    public String getRealName() {
        return realName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }
}
