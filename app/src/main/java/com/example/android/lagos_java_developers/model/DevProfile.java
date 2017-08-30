package com.example.android.lagos_java_developers.model;

/**
 * Created by OZMA NIG COM LTD on 28-Aug-17.
 */

public class DevProfile {

    private  String developersName;
    private  String bio;
    private  String pfollowers;
    private  String pfollowing;
    private  String plocation;
    private  String pemail;
    private  String phireable;
    private  String pRepos;

    public  DevProfile(String developersName, String bio, String repos, String followers, String following, String location, String email, String hireable ){



        this.developersName = developersName;
        this.bio = bio;
        pRepos           = repos;
        pfollowers       = followers;
        pfollowing       = following;
        plocation        = location;
        pemail           = email;
        phireable        = hireable;




    }

    public String getDevelopersName() {
        return developersName;
    }

    public String getBio() {
        return bio;
    }

    public String getpRepos() {
        return pRepos;
    }

    public String getPFollowers() {
        return pfollowers;
    }

    public String getPFollowing() {
        return pfollowing;
    }

    public String getPLocation() {
        return plocation;
    }

    public String getPEmail() {
        return pemail;
    }

    public String getPHireable() {
        return phireable;
    }


}
