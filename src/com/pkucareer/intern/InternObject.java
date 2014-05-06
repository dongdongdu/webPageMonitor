package com.pkucareer.intern;

public class InternObject {

    private int id;
    private String Name;
    private String createDate;

    public InternObject() {
        super();
    }

    public InternObject(int id, String name, String createDate) {
        this.id = id;
        Name = name;
        this.createDate = createDate;
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

}
