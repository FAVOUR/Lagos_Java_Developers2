package com.example.android.lagos_java_developers.model;
/**
 * Created by OZMA NIG COM LTD on 04-Aug-17.
 */

public class Developers {

    private String mDeveloperName;
    private String mImageResourceId;
    private String mdevUrl;
    private String mDevMainPage;

    public Developers(String devName, String ImageResourceId, String devUrl, String  devMainPage) {

        mDeveloperName = devName;
        mImageResourceId = ImageResourceId;
        mdevUrl = devUrl;
        mDevMainPage = devMainPage;
    }


    public String getDeveloperName() {

        return mDeveloperName;
    }

    public String getImageResourceId() {

        return mImageResourceId;
    }

    public String getDevMainPage() {
        return mDevMainPage;
    }

    public String getDeveloperUrl() {
        return mdevUrl;
    }




    }






