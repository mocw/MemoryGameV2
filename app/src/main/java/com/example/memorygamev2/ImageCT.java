package com.example.memorygamev2;

public class ImageCT {
    private byte[] img;

    ImageCT(byte[] image) {
        this.img = image;
    }

    public byte[] getImage() {
        return img;
    }
}
