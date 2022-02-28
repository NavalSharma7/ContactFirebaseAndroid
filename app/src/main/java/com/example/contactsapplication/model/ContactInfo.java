package com.example.contactsapplication.model;


import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class ContactInfo implements Serializable {

    String name, email, phone, imgString, path;


    String id;

    public ContactInfo(String name, String email, String phone, String imgString,String path,String id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.imgString = imgString;
        this.path = path;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
