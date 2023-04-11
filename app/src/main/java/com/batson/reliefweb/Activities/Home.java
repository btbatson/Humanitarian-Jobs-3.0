package com.batson.reliefweb.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.batson.reliefweb.R;
import com.batson.reliefweb.Config.Rest;
import com.batson.reliefweb.Config.Constants;
import com.batson.reliefweb.Model.JobModel;
import com.batson.reliefweb.Adaptor.JobsAdaptor;
import com.batson.reliefweb.Adaptor.DetailsAdaptor;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    private AdView adView;
    public static final int RC_SIGN_IN = 1;

    RecyclerView recyclerReportList;
    private RecyclerView.LayoutManager layoutManager;
    private JobsAdaptor recyclerAdaptor;
    private SwipeRefreshLayout refreshReportList;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    Rest apiInterface;

    JobModel reportList;
    List<JobModel.Data> data;

    @SuppressLint("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ImageView imgView = findViewById(R.id.imageView3);
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        initializeVars();
        attachMethods();
        //Load extra intents
        Button viawall = (Button) findViewById(R.id.viewll);

        Button saved = (Button) findViewById(R.id.saved);

        viawall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Jobs.class);
                view.getContext().startActivity(intent);}
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Saved.class);
                view.getContext().startActivity(intent);}
        });

    }
    public void initializeVars(){
        recyclerReportList = (RecyclerView) findViewById(R.id.latestjobs);
        layoutManager = new LinearLayoutManager(this);
        recyclerReportList.setLayoutManager(layoutManager);

        refreshReportList = (SwipeRefreshLayout) findViewById(R.id.refresh_report);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = firebaseDatabase.getReference().child("2020_latest");
        fetchReportList();
        attachDatabaseReadListener();
    }

    private void attachMethods() {
        recyclerReportList.addOnItemTouchListener(
                new DetailsAdaptor (Home.this, new DetailsAdaptor.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Home.this, Details.class);
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
        Call<JobModel> call = apiInterface.getReportLatest();

        call.enqueue(new Callback<JobModel>() {
            @Override
            public void onResponse(Call<JobModel> call, Response<JobModel> response) {

                if(response.isSuccessful()) {
                    reportList = response.body();
                    databaseReference.child("response").setValue(reportList);
                } else {
                    Toast.makeText(Home.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JobModel> call, Throwable t) {
                Toast.makeText(Home.this, "No Internet! Please check your connection", Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_account:
                Intent launchNewIntent = new Intent(Home.this,Settings.class);
                startActivityForResult(launchNewIntent, 0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
