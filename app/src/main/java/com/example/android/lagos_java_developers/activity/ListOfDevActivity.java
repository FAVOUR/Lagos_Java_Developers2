package com.example.android.lagos_java_developers.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.lagos_java_developers.R;
import com.example.android.lagos_java_developers.adapter.DevelopersAdapter;
import com.example.android.lagos_java_developers.model.Developers;
import com.example.android.lagos_java_developers.utils.DevelopersList;

import java.util.ArrayList;
import java.util.List;


public class ListOfDevActivity extends AppCompatActivity implements DevelopersAdapter.ListItemClickListiner,SwipeRefreshLayout.OnRefreshListener {

    //URL for GITHUB API
    private static final String GITHUB_SEARCH_API = "https://api.github.com/search/users?q=language:java+location:lagos&page=";

    List<Developers> listOfDevelopers;
    DevelopersAdapter createDevelopersAdapterObject;
    LinearLayoutManager linearLayoutManager;
    RecyclerView createRecyclerViewobject;
    int visibleItemCount;
    int totalItemCount;
    int firstVisibleItemPosition;
    int previousTotal = 0;
    static int pageCount = 1;
    private static final int LOADER_ID = 0;
    boolean isLoading = true;

    SwipeRefreshLayout swipeContainer;
    public String lagos_Github_Developers_Url;
    LinearLayout loadMore;
    RelativeLayout noInternet;
    RelativeLayout loading;
    TextView emptyState;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_layout);

        //Dynamic Url for query
        lagos_Github_Developers_Url = GITHUB_SEARCH_API + String.valueOf(pageCount);


        createRecyclerViewobject = (RecyclerView) findViewById(R.id.rv_members);

        listOfDevelopers = new ArrayList<>();

        createDevelopersAdapterObject = new DevelopersAdapter(this, listOfDevelopers, this);


        linearLayoutManager = new LinearLayoutManager(this);
        createRecyclerViewobject.setLayoutManager(linearLayoutManager);
        createRecyclerViewobject.setHasFixedSize(true);

        createRecyclerViewobject.setAdapter(createDevelopersAdapterObject);

//        loadmore = (LinearLayout) findViewById(R.id.linlaFooterProgress);
        loading = (RelativeLayout) findViewById(R.id.loading);

        //To display the message when there is nothing toi display
        emptyState = (TextView) findViewById(R.id.emptyState);

        //To display the message for no internet connection
        noInternet = (RelativeLayout) findViewById(R.id.noInternetConnection);

//        spins to show that it is loading
        loadMore = (LinearLayout)findViewById(R.id.footerPB);

//        refreshes when pulled down
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);




        // Display data if internet connection is available else ask user to retry
        Boolean netCheck = isNetworkStatusAvialable(getApplicationContext());
        if (!netCheck) {

            loading.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);


            Snackbar.make(createRecyclerViewobject,
                    "internet is not available", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Custom action
                            loading.setVisibility(View.VISIBLE);
                            noInternet.setVisibility(View.GONE);
                            getSupportLoaderManager().initLoader(LOADER_ID, null, new MyLoaer());
                        }
                    }).show();
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, new MyLoaer());
        swipeContainer.setOnRefreshListener(this);


        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);




    }


    class MyLoaer implements LoaderManager.LoaderCallbacks<List<Developers>>{



    @Override
    public Loader<List<Developers>> onCreateLoader(int i, Bundle bundle){
        return new AsyncTaskLoader<List<Developers>>(getApplicationContext()){

            public  String mUrl= lagos_Github_Developers_Url;


        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Developers> loadInBackground() {
            if(mUrl==null){
                return null;
            }
            return DevelopersList.fetchAllDevInfo(mUrl);

        }
        };


    }

    @Override
    public void onLoadFinished(Loader<List<Developers>> loader, List<Developers> developerss) {


        if( developerss !=null && ! developerss.isEmpty()){
              createDevelopersAdapterObject.addAll( developerss );
            emptyState.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            loadMore.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);

        createRecyclerViewobject.addOnScrollListener(new RecyclerView.OnScrollListener() {


                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                    super.onScrollStateChanged(recyclerView,newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);


                    if (dy > 0){
                        //Total number of items on the screen
                        visibleItemCount =  linearLayoutManager .getChildCount();

                        //Total numbers of items in the list
                        totalItemCount =   linearLayoutManager .getItemCount();

                        //Total number of items you have already seen
                        firstVisibleItemPosition = linearLayoutManager .findFirstVisibleItemPosition();

                        if (isLoading){
                            if (totalItemCount >  previousTotal){
                                isLoading = false ;
                                previousTotal = totalItemCount;
                            }
                        }
                        if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                            pageCount = pageCount +1;
                            if  (pageCount < 8){
                               lagos_Github_Developers_Url = GITHUB_SEARCH_API +String.valueOf(pageCount);

                        if (isNetworkStatusAvialable(getApplicationContext())){
                                loadMore.setVisibility(View.VISIBLE);
                                getSupportLoaderManager().initLoader( pageCount, null, new MyLoaer());
                                isLoading =true;}

                          else{
                            Snackbar.make(createRecyclerViewobject,
                                    "internet is not available", Snackbar.LENGTH_LONG)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Custom action
                                            loading.setVisibility(View.VISIBLE);
                                            noInternet.setVisibility(View.GONE);
                                            getSupportLoaderManager().initLoader(LOADER_ID, null, new MyLoaer());
                                        }
                                    }).show();
                            loadMore.setVisibility(View.GONE);
                        }
                        }

                        else{

                                Snackbar.make(createRecyclerViewobject,
                                        "Nothing to show", Snackbar.LENGTH_LONG)
                                        .setAction("Cancel", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {}
                                        }).show();

                            }


                    }


                }

            }});




    }
    }

    @Override
    public void onLoaderReset(Loader<List<Developers>> loader) {
        createDevelopersAdapterObject.clear();
    }
  }
      String developerUsername;
   String developerUrl;
      @Override
   public void onListItemClicked(int clickdItemIndex) {
        Intent numbersIntent = new Intent(ListOfDevActivity.this, DevProfileActivity.class);
          developerUsername = createDevelopersAdapterObject.getList().getDeveloperName();
            developerUrl = createDevelopersAdapterObject.getList().getDeveloperUrl();
            String jsonLink = createDevelopersAdapterObject.getList().getDevMainPage();
      numbersIntent.putExtra("image", createDevelopersAdapterObject.getList().getImageResourceId());
           numbersIntent.putExtra("username",developerUsername);
           numbersIntent.putExtra("devUrl",developerUrl);
           numbersIntent.putExtra("devHtmlPage",jsonLink);
           startActivity(numbersIntent);

    }

    @Override
    public void onRefresh() {

        if( isNetworkStatusAvialable (getApplicationContext())) {
            swipeContainer.setRefreshing(true);
            createDevelopersAdapterObject.clear();
            Toast.makeText(this, "Refreshing list....", Toast.LENGTH_LONG).show();

            swipeContainer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getSupportLoaderManager().initLoader(LOADER_ID, null, new MyLoaer());
                    noInternet.setVisibility(View.GONE);
                    swipeContainer.setRefreshing(false);
                }
            },3500);

        }

else {       Snackbar.make(createRecyclerViewobject,
                "No connection", Snackbar.LENGTH_LONG)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Custom action
                        loading.setVisibility(View.VISIBLE);
                        noInternet.setVisibility(View.GONE);
                        getSupportLoaderManager().initLoader(LOADER_ID, null, new MyLoaer());
                    }
                }).show();

            }
            //Disable the refreshing animation
            swipeContainer.setRefreshing(false);
        }






            public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    public  void trigger (View view){

            loading.setVisibility(View.VISIBLE);
            noInternet.setVisibility(View.GONE);
        getSupportLoaderManager().initLoader(0, null,new  MyLoaer());
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(createDevelopersAdapterObject != null) {
//            pageCount = 1;
//            this.unregisterReceiver(createDevelopersAdapterObject);
//        }
//    }

    }


