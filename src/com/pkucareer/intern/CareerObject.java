package com.pkucareer.intern;

import static java.lang.System.out;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.webmoni.util.Utils;

public class CareerObject implements Comparable<CareerObject> {

    private String Title;
    private String createDate;
    private String linkURL;
    private String department;

    public CareerObject() {
        super();
    }

    public CareerObject(String title, String createDate, String linkURL) {
        this(title, createDate, linkURL, "");
    }

    public CareerObject(String title, String createDate, String linkURL, String department) {
        this.Title = title;
        this.createDate = createDate;
        this.linkURL = linkURL;
        this.department = department;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String name) {
        Title = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int compareTo(CareerObject o) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d1 = null;
        java.util.Date d2 = null;
        try {
            d1 = df.parse(this.createDate);
            d2 = df.parse(o.getCreateDate());
        } catch (ParseException e) {
            out.println("Error when parsing dateString, dateString should be in yyyy-MM-dd format, example like 2011-01-01.");
            Utils.writeToLog("Error when parsing dateString, dateString should be in yyyy-MM-dd format, example like 2011-01-01.");
            e.printStackTrace();
            Utils.writeToLog(e.getMessage());
        }
        return d1.compareTo(d2);
    }
}
