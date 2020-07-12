package com.example.myfirebaseauthenticationdatabase;

public class User
{
    public String name, phone, email, profilePicUri;

    public User(){}

    public User(String name, String phone, String email, String profilePicUri) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profilePicUri = profilePicUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }
}
