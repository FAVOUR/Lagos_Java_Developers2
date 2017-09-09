package com.example.android.lagos_java_developers.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.android.lagos_java_developers.R;
import com.example.android.lagos_java_developers.adapter.DevelopersAdapter;
import com.example.android.lagos_java_developers.model.Developers;
import com.example.android.lagos_java_developers.utils.DevListUtil;
import com.example.android.lagos_java_developers.utils.NetworkCall;

import java.util.ArrayList;
import java.util.List;


public class DevListActivity extends AppCompatActivity implements DevelopersAdapter.ListItemClickListiner, SwipeRefreshLayout.OnRefreshListener {

    //URL for GITHUB API
    private static final String GITHUB_SEARCH_API = "https://api.github.com/search/users?q=language:java+location:lagos&page=";
    private static final int LOADER_ID = 0;
    static int pageIndex = 1;
    public String searchUrl;
    List<Developers> listOfDevelopers;
    DevelopersAdapter mAdapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView mRecyclerView;
    int visibleItemCount;
    int totalItemCount;
    int firstVisibleItemPosition;
    int previousTotal = 0;
    boolean isLoading = true;
    SwipeRefreshLayout swipeContainer;
    LinearLayout loadMore;
    RelativeLayout noInternet;
    RelativeLayout loading;
    RelativeLayout emptyState;
    String developerUsername;
    String developerUrl;
    Boolean loadingContents = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rv_layout);

        //Dynamic Url for query
        searchUrl = GITHUB_SEARCH_API + String.valueOf(pageIndex);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_members);

        listOfDevelopers = new ArrayList<>();

        mAdapter = new DevelopersAdapter(this, listOfDevelopers, this);


        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);


        loading = (RelativeLayout) findViewById(R.id.loading);

        //To display the message when there is nothing to display
        emptyState = (RelativeLayout) findViewById(R.id.emptyState);

        //To display the message for no internet connection
        noInternet = (RelativeLayout) findViewById(R.id.noInternetConnection);

        //spins to show that it is loading
        loadMore = (LinearLayout) findViewById(R.id.footerPB);

        //refreshes when pulled down
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeContainer.setOnRefreshListener(this);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Display data if internet connection is available else ask user to retry
        Boolean netCheck = NetworkCall.isNetworkStatusAvialable(getApplicationContext());
        if (!netCheck) {

            loading.setVisibility(View.GONE);
            noInternet.setVisibility(View.VISIBLE);


        } else {

            getSupportLoaderManager().initLoader(LOADER_ID, null, new OkLoader());
        }
    }

    @Override
    public void onListItemClicked(int clickdItemIndex) {
        Intent numbersIntent = new Intent(DevListActivity.this, DevProfileActivity.class);
        developerUsername = mAdapter.getList().getDeveloperName();
        developerUrl = mAdapter.getList().getDeveloperUrl();
        String jsonLink = mAdapter.getList().getDevMainPage();
        numbersIntent.putExtra("image", mAdapter.getList().getImageResourceId());
        numbersIntent.putExtra("username", developerUsername);
        numbersIntent.putExtra("devUrl", developerUrl);
        numbersIntent.putExtra("devHtmlPage", jsonLink);
        startActivity(numbersIntent);

    }

    @Override
    public void onRefresh() {

        if (NetworkCall.isNetworkStatusAvialable(getApplicationContext())) {
            mAdapter.clear();
            swipeContainer.setRefreshing(true);
            noInternet.setVisibility(View.GONE);
            Toast.makeText(this, "Refreshing list....", Toast.LENGTH_LONG).show();


            swipeContainer.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getSupportLoaderManager().initLoader(LOADER_ID, null, new OkLoader());


                }
            }, 3500);

        } else {
            Snackbar.make(mRecyclerView,
                    "No connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Custom action
                            loading.setVisibility(View.VISIBLE);
                            noInternet.setVisibility(View.GONE);
                            getSupportLoaderManager().initLoader(LOADER_ID, null, new OkLoader());
                        }
                    }).show();

        }
        //Disable the refreshing animation
        swipeContainer.setRefreshing(false);
    }

    public void trigger(View view) {
        getSupportLoaderManager().initLoader(LOADER_ID, null, new OkLoader());
        loading.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);

    }

    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SEARCH_RESULTS_RAW", getPageIndex());

    }

    class OkLoader implements LoaderManager.LoaderCallbacks<List<Developers>> {


        {
        }
        @Override
        public Loader<List<Developers>> onCreateLoader(int i, Bundle bundle) {
            return new AsyncTaskLoader<List<Developers>>(getApplicationContext()) {

                public String mUrl = searchUrl;




                @Override
                protected void onStartLoading() {
                    forceLoad();
                }

                @Override
                public List<Developers> loadInBackground() {
                    if (mUrl == null) {
                        return null;
                    }
                    return DevListUtil.fetchAllDevInfo(mUrl);

                }
            };


        }

        @Override
        public void onLoadFinished(Loader<List<Developers>> loader, List<Developers> developers) {


            if (developers != null && !developers.isEmpty() && NetworkCall.isNetworkStatusAvialable(getApplicationContext())) {
                mAdapter.addAll(developers);
                mAdapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                loadMore.setVisibility(View.GONE);
                noInternet.setVisibility(View.GONE);

                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);


                        if (dy > 0) {
                            //Total number of items on the screen
                            visibleItemCount = linearLayoutManager.getChildCount();

                            //Total numbers of items in the list
                            totalItemCount = linearLayoutManager.getItemCount();

                            //Total number of items you have already seen
                            firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                            if (isLoading) {
                                if (totalItemCount > previousTotal) {
                                    isLoading = false;
                                    previousTotal = totalItemCount;
                                }
                            }
                            if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                                pageIndex += 1;
                                if (pageIndex <= 8 && NetworkCall.isNetworkStatusAvialable(getApplicationContext())) {

                                    searchUrl = GITHUB_SEARCH_API + String.valueOf(pageIndex);

                                        loadMore.setVisibility(View.VISIBLE);
                                        getSupportLoaderManager().initLoader(pageIndex, null, new OkLoader());
                                        isLoading = true;
                                    } else {

                                    Snackbar.make(mRecyclerView,
                                            "End of List", Snackbar.LENGTH_LONG)
                                            .setAction("Cancel", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                }
                                            }).show();

                                }


                            }


                        }

                    }
                });


            }


        }

        @Override
        public void onLoaderReset(Loader<List<Developers>> loader) {
            mAdapter.clear();
        }


    }

}


