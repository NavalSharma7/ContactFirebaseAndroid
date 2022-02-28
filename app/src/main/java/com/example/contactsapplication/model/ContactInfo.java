package com.example.contactsapplication.model;

public class ContactInfo {

    String name, email, phone, imgString, path;

    public ContactInfo(String name, String email, String phone, String imgString,String path) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imgString = imgString;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImgString() {
        return imgString;
    }

    public void setImgString(String imgString) {
        this.imgString = imgString;
    }


    public ContactInfo() {
    }
}
