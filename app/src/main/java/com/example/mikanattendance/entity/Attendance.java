package com.example.mikanattendance.entity;

public class Attendance {
    private Integer ID;

    // 外键，指向User表
    private Integer userID;

    private Integer attendanceTime;

    // 枚举类型，可取"ON_TIME", "LATE", "OUT_OF_RANGE"
    private String attendanceStatus;

    // 0是上班，1是下班，不能出现其他值
    private Short attendanceType;

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

    public Integer getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(Integer attendanceTime) {
        this.attendanceTime = attendanceTime;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public Short getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(Short attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
