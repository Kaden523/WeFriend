package com.wefriend.wefriend.account;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Account {
    private String Last_name;
    private String First_name;
    private String Gender;
    private String school;
    private String major;
    private String userID;
    private Timestamp birth_day;
    private String profileImage;
    private String aboutMe;

    private ArrayList<String> likes;
    public Account(String First_name, String Last_name, String school, String Gender, String major,
                   Timestamp birth_day,String userID, String profileImage, String aboutMe){
        likes = new ArrayList<>();
        this.Last_name = Last_name;
        this.First_name = First_name;
        this.school = school;
        this.Gender = Gender;
        this.major = major;
        this.birth_day = birth_day;
        this.userID = userID;
        this.profileImage = profileImage;
        this.aboutMe = aboutMe;
    }

    public ArrayList<String> getLikes(){
        return likes;
    }
    public void setLastName(String lastName){
        Last_name = lastName;
    }
    public String getLast_name(){
        return Last_name;
    }

    public void setLast_name(String last_name) {
        Last_name = last_name;
    }

    public String getFirst_name() {
        return First_name;
    }

    public void setFirst_name(String first_name) {
        First_name = first_name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getUserID() {
        return userID;
    }

    public void setBirth_day(Timestamp birth_day) {
        this.birth_day = birth_day;
    }

    public Timestamp getBirth_day() {
        return birth_day;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getAboutMe() {
        return aboutMe;
    }
    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }
}