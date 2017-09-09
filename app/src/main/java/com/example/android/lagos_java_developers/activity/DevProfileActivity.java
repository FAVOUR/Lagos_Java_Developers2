package com.example.android.lagos_java_developers.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.lagos_java_developers.R;
import com.example.android.lagos_java_developers.model.DevProfile;
import com.example.android.lagos_java_developers.utils.DevProfileUtil;
import com.example.android.lagos_java_developers.utils.NetworkCall;
import com.squareup.picasso.Picasso;




public class DevProfileActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<DevProfile> {
    int loader_id = 0;
    private String devUrl;
    private String userName;
    private String htmlLink;
    private RelativeLayout loading;
    private RelativeLayout noInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developerprofile);
        userName = getIntent().getStringExtra("username");
        devUrl = getIntent().getStringExtra("devUrl");
        htmlLink = getIntent().getStringExtra("devHtmlPage");
        loading = (RelativeLayout) findViewById(R.id.profile);
        noInternet = (RelativeLayout) findViewById(R.id.noInternetConnection);
        loading.setVisibility(View.VISIBLE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        if (NetworkCall.isNetworkStatusAvialable(getApplicationContext())) {

            getSupportLoaderManager().initLoader(0, null, this);

        } else {
            loading.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);

        }
    }

    //activated when the user clicks the try again
    public void trigger(View view) {
        if (!NetworkCall.isNetworkStatusAvialable(getApplicationContext())) {

            noInternet.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);

        } else {
            loading.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.GONE);
            getSupportLoaderManager().initLoader(loader_id, null, this);
        }

    }



    // Update  the  DevProfileActivity UI
    private  void updateUi(DevProfile  devProfile){



        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(userName);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
//        NavUtils.navigateUpFromSameTask(this);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DevListActivity.class));
            }
        });


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView tUserName = (TextView)findViewById(R.id.devLink);
        tUserName.setText(htmlLink);


        TextView developers_Actual_Name = (TextView) findViewById(R.id.actual_name);
        developers_Actual_Name.setText(devProfile.getDevelopersName());



        ImageView dev_Image2 =(ImageView) findViewById(R.id.background_image);
        Picasso.with(this)
                .load(getIntent().getStringExtra("image"))
                .error(R.drawable.image_download_error)
                .placeholder(R.drawable.ok1).into(dev_Image2);

        ImageView dev_Image =(ImageView) findViewById(R.id.app_bar_image);
        Picasso.with(this)
                .load(getIntent().getStringExtra("image"))
                .error(R.drawable.image_download_error)
                        .placeholder(R.drawable.avata).into( dev_Image);



        TextView developers_Bio = (TextView) findViewById(R.id.adev_bio);
        developers_Bio .setText(check(devProfile.getBio()));

        TextView developers_Repos = (TextView) findViewById(R.id.adev_repos);
        developers_Repos .setText( check(devProfile.getpRepos()));

        TextView developers_Followers = (TextView) findViewById(R.id.adev_followers);
        developers_Followers .setText( devProfile.getPFollowers());

        TextView developers_Following = (TextView) findViewById(R.id.adev_following);
        developers_Following.setText(devProfile.getPFollowing());

        TextView developers_Location  = (TextView) findViewById(R.id.adev_location);
        developers_Location .setText(devProfile.getPLocation());

        TextView developers_Email  = (TextView) findViewById(R.id.adev_email);
        developers_Email .setText(check(devProfile.getPEmail()));

        TextView developers_status  = (TextView) findViewById(R.id.adev_hirable);
        developers_status .setText(confirmifHirable(devProfile.getPHireable()));



    }


    @Override
    public Loader<DevProfile > onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<DevProfile>(this) {

            private  String  mUrl = devUrl;


            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public DevProfile loadInBackground() {

                if(mUrl==null){
                    return null;
                }
                else{
                    return DevProfileUtil.fetchProfilePageInfo(mUrl);
                }
            }
        };


    }

    @Override
    public void onLoadFinished(Loader<DevProfile> loader, DevProfile  data) {
        if (data == null) {
            loading.setVisibility(View.VISIBLE);

        }

        else{
            loading.setVisibility(View.GONE);
            updateUi(data);}
    }
    @Override
    public void onLoaderReset(Loader<DevProfile> loader) {

    }

    //Calls the share intent
    public void share (View view ){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_message) + "\n \n"+ "@" +  userName + "\n \n " + devUrl);
        sendIntent.setType("text/plain");

      if( sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(sendIntent, getText(R.string.share_header)));
        }
    }

    //Calls the website intent
    public void website (View view){

        Intent browserIntent= new Intent(Intent.ACTION_VIEW, Uri.parse(htmlLink));

        if( browserIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser( browserIntent, getText(R.string.browser_header)));
        }
    }



   //Confirms if the result is empty
    private String check(String input){

        if (input.equals("null") ) {
            return "Not Provided";
        }

        else{return input;}
    }

    //Confirm if developer is hirable
    private String confirmifHirable (String input ){

        String isNull = check(input);


        if (isNull.equals("true")) {
            return  "Yes";
        }

        if (isNull.equals("false")) {
            return "No";
        } else {
            return isNull;
        }

    }

}









