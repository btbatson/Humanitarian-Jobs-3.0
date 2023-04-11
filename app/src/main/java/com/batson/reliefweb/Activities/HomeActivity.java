package com.batson.reliefweb.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.batson.reliefweb.api.ApiInterface;
import com.batson.reliefweb.api.ApiModel;
import com.batson.reliefweb.dataClasses.JobCategoryModel;
import com.batson.reliefweb.dataClasses.JobModel;
import com.batson.reliefweb.recyclerview.JobtypeAdaptor;
import com.batson.reliefweb.recyclerview.RecyclerAdaptor;
import com.batson.reliefweb.recyclerview.RecyclerItemClickListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private AdView adView;
    public static final int RC_SIGN_IN = 1;

    RecyclerView recyclerReportList;
    private RecyclerView.LayoutManager layoutManager;
    private JobtypeAdaptor JobtypeAdaptor;
    private SwipeRefreshLayout refreshJobType;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    ApiInterface apiInterface;

    JobCategoryModel jobType;
    List<JobCategoryModel.Jobtype> jobtype;

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
        Button settings = (Button) findViewById(R.id.settings);
        Button saved = (Button) findViewById(R.id.saved);

        viawall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), JobsListActivity.class);
                view.getContext().startActivity(intent);}
        });



        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Saved.class);
                view.getContext().startActivity(intent);}
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), About.class);
                view.getContext().startActivity(intent);}
        });
    }
public void initializeVars(){
    recyclerReportList = (RecyclerView) findViewById(R.id.latestjobs);
    layoutManager = new LinearLayoutManager(this);
    recyclerReportList.setLayoutManager(layoutManager);

    refreshJobType = (SwipeRefreshLayout) findViewById(R.id.refresh_report);

    firebaseDatabase = FirebaseDatabase.getInstance();
    firebaseAuth = FirebaseAuth.getInstance();
    databaseReference = firebaseDatabase.getReference().child("2023_category");
}

    private void attachMethods() {
        recyclerReportList.addOnItemTouchListener(
                new RecyclerItemClickListener(HomeActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(HomeActivity.this, ReportInfoActivity.class);
                        intent.putExtra(Category_Filter_Activity.CATEGORY_ID, jobtype.get(position).getId());
                        startActivity(intent);
                        // openUrl(data.get(position).getFields().getUrl());

                    }
                })
        );

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser != null){
                    onSignedInInit();
                }else{
                    onSignedOutCleaner();
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setTheme(R.style.LoginTheme)
                            .setLogo(R.mipmap.ic_launcher)
                            .setIsSmartLockEnabled(false)
                            .setTosAndPrivacyPolicyUrls("https://joebirch.co/privacy.html", "https://joebirch.co/privacy.html")
                            .build(), RC_SIGN_IN);
                }

            }
        };

        refreshJobType.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchJobType();
            }
        });
    }

    public void fetchJobType() {
        refreshJobType.setRefreshing(true);

        apiInterface = ApiModel.getApiClient().create(ApiInterface.class);
        Call<JobCategoryModel> call = apiInterface.getJobType();
        call.enqueue(new Callback<JobCategoryModel>() {
            @Override
            public void onResponse(Call<JobCategoryModel> call, Response<JobCategoryModel> response) {

                if(response.isSuccessful()) {
                    jobType = response.body();
                    databaseReference.child("response").setValue(jobType);
                } else {
                    Toast.makeText(HomeActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JobCategoryModel> call, Throwable t) {
                Toast.makeText(HomeActivity.this, "No Internet! Please check your connection", Toast.LENGTH_SHORT).show();
                refreshJobType.setRefreshing(false);
            }
        });

        if(refreshJobType.isRefreshing()) {
            refreshJobType.setRefreshing(false);
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
        firebaseAuth.removeAuthStateListener(authStateListener);
        detachDatabaseReadListener();
        jobType = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void onSignedInInit(){
        fetchJobType();
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

                    JobCategoryModel reportListFirebase = dataSnapshot.getValue(JobCategoryModel.class);
                    jobtype = reportListFirebase.getJobtype();
                    JobtypeAdaptor = new JobtypeAdaptor(jobtype);
                    recyclerReportList.setAdapter(JobtypeAdaptor);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    JobCategoryModel reportListFirebase = dataSnapshot.getValue(JobCategoryModel.class);
                    jobtype = reportListFirebase.getJobtype();
                    JobtypeAdaptor = new JobtypeAdaptor(jobtype);
                    recyclerReportList.setAdapter(JobtypeAdaptor);

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
                Intent launchNewIntent = new Intent(HomeActivity.this,Profile.class);
                startActivityForResult(launchNewIntent, 0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

