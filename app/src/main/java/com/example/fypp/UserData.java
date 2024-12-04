package com.example.fypp;

public class UserData
{
    String username,userid,position,email,password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {return userid;}

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPosition() {return position;}

    public void setPosition(String position) {this.position = position;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public UserData(String email,String password,String position,String userid,String username) {
        this.username = username;
        this.userid = userid;
        this.position = position;
        this.email = email;
        this.password = password;
    }

    /*public UserData()
    {

    }*/

}
