package com.example.memorygamev2;

public class UserStats {
    public String userID;
    public String BoardSize;
    public String email;
    public Double Time;

    public UserStats(String uID,String BSize,String mail,Double Tm) {
        userID=uID;
        BoardSize = BSize;
        email=mail;
        Time = Tm;
    }
}
