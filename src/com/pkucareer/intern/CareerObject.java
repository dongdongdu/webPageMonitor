package com.pkucareer.intern;

public class CareerObject {

    private int id;
    private String Name;
    private String createDate;
    private String linkURL;

    public CareerObject() {
        super();
    }

    public CareerObject(int id, String name, String createDate, String linkURL) {
        this.id = id;
        this.Name = name;
        this.createDate = createDate;
        this.linkURL = linkURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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

}
