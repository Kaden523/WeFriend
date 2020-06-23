package com.wefriend.wefriend.Main;

import org.jetbrains.annotations.NotNull;

class Profiles {
    private String name;
    private String college;
    private String major;
    private String schoolYear;
    private String source;
    public static Profiles[] profiles;

    public Profiles(@NotNull String name, @NotNull String college, @NotNull String major, @NotNull String schoolYear, String source) {
        this.college = college;
        this.major = major;
        this.name = name;
        this.schoolYear = schoolYear;
        this.source = source;
    }


    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCollege() {
        return college;
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public static Profiles[] getProfiles() {
        return profiles;
    }

    public static void setProfiles(Profiles[] profiles) {
        Profiles.profiles = profiles;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {

        return "Profile{" +
                "Name='" + name + '\'' +
                ", college='" + college + '\'' +
                '}';
    }
}
