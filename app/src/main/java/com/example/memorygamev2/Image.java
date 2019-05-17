package com.example.memorygamev2;

public class Image {
    public String imageAdress;
    public String UserID;
    public String name;

    public Image() {

    }

    public Image(String _img,String UID,String name) {
        this.imageAdress = _img;
        this.UserID=UID;
        this.name=name;
    }

}
