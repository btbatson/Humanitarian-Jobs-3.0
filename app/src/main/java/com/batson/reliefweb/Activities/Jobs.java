package com.batson.reliefweb.Activities;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.batson.reliefweb.R;
import com.batson.reliefweb.Config.Constants;
import com.batson.reliefweb.Config.Rest;
import com.batson.reliefweb.Model.JobModel;
import com.batson.reliefweb.Adaptor.JobsAdaptor;
import com.batson.reliefweb.Adaptor.DetailsAdaptor;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */
public class Jobs extends AppCompatActivity {
    public static final int RC_SIGN_IN = 1;

    RecyclerView recyclerReportList;
    private RecyclerView.LayoutManager layoutManager;
    private JobsAdaptor recyclerAdaptor;
    private SwipeRefreshLayout refreshReportList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;


    Rest apiInterface;

    JobModel reportList;
    List<JobModel.Data> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initializeVars();

        attachMethods();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public void initializeVars(){
        recyclerReportList = (RecyclerView) findViewById(R.id.recycler_repost_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerReportList.setLayoutManager(layoutManager);

        refreshReportList = (SwipeRefreshLayout) findViewById(R.id.refresh_report);

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child("2023_jobs");
        fetchReportList();
        attachDatabaseReadListener();
    }

    private void attachMethods() {
        recyclerReportList.addOnItemTouchListener(
                new DetailsAdaptor(Jobs.this, new DetailsAdaptor.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Jobs.this, Details.class);
                        intent.putExtra(Details.REPORT_ID, data.get(position).getId());
                        startActivity(intent);
                       // openUrl(data.get(position).getFields().getUrl());

                    }
                })
        );


        refreshReportList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchReportList();
            }
        });
    }

    public void fetchReportList() {
        refreshReportList.setRefreshing(true);
        apiInterface = Constants.getApiClient().create(Rest.class);
        Call<JobModel> call = apiInterface.getReportList();
        call.enqueue(new Callback<JobModel>() {
            @Override
            public void onResponse(Call<JobModel> call, Response<JobModel> response) {

                if(response.isSuccessful()) {
                    reportList = response.body();
                    databaseReference.child("response").setValue(reportList);
                } else {
                    Toast.makeText(Jobs.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JobModel> call, Throwable t) {
                Toast.makeText(Jobs.this, "No Internet! Please check your connection", Toast.LENGTH_SHORT).show();
                refreshReportList.setRefreshing(false);
            }
        });

        if(refreshReportList.isRefreshing()) {
            refreshReportList.setRefreshing(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        detachDatabaseReadListener();
        reportList = null;
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    private void onSignedInInit(){
        fetchReportList();
        attachDatabaseReadListener();
    }

    private void onSignedOutCleaner(){
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener(){
        if(childEventListener==null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    JobModel reportListFirebase = dataSnapshot.getValue(JobModel.class);
                    data = reportListFirebase.getData();
                    recyclerAdaptor = new JobsAdaptor(data);
                    recyclerReportList.setAdapter(recyclerAdaptor);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    JobModel reportListFirebase = dataSnapshot.getValue(JobModel.class);
                    data = reportListFirebase.getData();
                    recyclerAdaptor = new JobsAdaptor(data);
                    recyclerReportList.setAdapter(recyclerAdaptor);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(childEventListener!=null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerAdaptor.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    public void openUrl(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
