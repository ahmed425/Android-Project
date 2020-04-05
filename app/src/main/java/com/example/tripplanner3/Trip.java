package com.example.tripplanner3;

import java.io.Serializable;
import java.util.ArrayList;


public class Trip implements Serializable {
    private int id;
    private String name;
    private String sp;
    private String slong;
    private String slat;
    private String ep;
    private String elong;
    private String elat;
    private String status;
    private String sd;
    private String ed;
    private String st;
    private String et;
    private int rep;
    private ArrayList<String> notes;
    private String user;
    private String url;
    private String syncid;
    //private int UpdateTime;


    public Trip() {
    }

    public Trip(String name, String sp, String slong, String slat, String ep, String elong, String elat, String status, String sd, String ed, String st, String et, int rep, ArrayList<String> notes, String user, String url) {
        this.name = name;
        this.sp = sp;
        this.slong = slong;
        this.slat = slat;
        this.ep = ep;
        this.elong = elong;
        this.elat = elat;
        this.status = status;
        this.sd = sd;
        this.ed = ed;
        this.st = st;
        this.et = et;
        this.rep = rep;
        this.notes = notes;
        this.user = user;
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSp() {
        return sp;
    }

    public void setSp(String sp) {
        this.sp = sp;
    }

    public String getSlong() {
        return slong;
    }

    public void setSlong(String slong) {
        this.slong = slong;
    }

    public String getSlat() {
        return slat;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public String getElong() {
        return elong;
    }

    public void setElong(String elong) {
        this.elong = elong;
    }

    public String getElat() {
        return elat;
    }

    public void setElat(String elat) {
        this.elat = elat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getEd() {
        return ed;
    }

    public void setEd(String ed) {
        this.ed = ed;
    }

    public int getRep() {
        return rep;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getEt() {
        return et;
    }

    public void setEt(String et) {
        this.et = et;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
