package com.example.attendanceqr.AdminReport;

public class AttendanceModel {

    String uid, name, date, time;

    public AttendanceModel() {}

    public AttendanceModel(String uid, String name, String date, String time) {
        this.uid = uid;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
