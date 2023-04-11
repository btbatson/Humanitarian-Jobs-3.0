package com.batson.reliefweb.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.batson.reliefweb.R;
import com.batson.reliefweb.api.ApiModel;
import com.batson.reliefweb.api.ApiInterface;
import com.batson.reliefweb.dataClasses.JobModel;
import com.batson.reliefweb.dataClasses.ReportInfo;
import com.batson.reliefweb.recyclerview.RecyclerAdaptor;
import com.batson.reliefweb.recyclerview.RecyclerItemClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReportInfoActivity extends AppCompatActivity {

    public static final String REPORT_ID = "com.batson.reliefweb.Activities.REPORT_ID";

    private ReportInfo reportInfo;

    private TextView textInfoTitle;
    private TextView textInfoSource;
    private TextView textInfoDate;
    private WebView text_info_howto,textInfoBody;
    private String reportId;
    private FloatingActionButton fab;
    private ConstraintLayout layoutInfoSource;

    ApiInterface apiInterface;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info);
        AdView adView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeViews();
        fetchData();
    }

    private void initializeViews() {
        textInfoTitle = (TextView) findViewById(R.id.text_info_title);
        textInfoSource = (TextView) findViewById(R.id.text_info_source);
        Intent intent = getIntent();
        reportId = intent.getStringExtra(REPORT_ID);
         text_info_howto = findViewById(R.id.text_info_howto);
         textInfoBody = findViewById(R.id.text_info_body);
        textInfoBody.setFocusable(true);
        textInfoBody.setFocusableInTouchMode(true);
        textInfoBody.getSettings().setJavaScriptEnabled(true);
        textInfoBody.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        textInfoBody.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        textInfoBody.getSettings().setDomStorageEnabled(true);
        textInfoBody.getSettings().setDatabaseEnabled(true);
        textInfoBody.getSettings().setAppCacheEnabled(true);
        textInfoBody.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        layoutInfoSource = (ConstraintLayout) findViewById(R.id.layout_info_source);


    }



    private void fetchData() {
        apiInterface = ApiModel.getApiClient().create(ApiInterface.class);
        Call<ReportInfo> call = apiInterface.getReportInfo(reportId);
        call.enqueue(new Callback<ReportInfo>() {
            @Override
            public void onResponse(Call<ReportInfo> call, Response<ReportInfo> response) {
                if(response.isSuccessful()) {
                    reportInfo = response.body();
                    populateData();
                } else {
                    Toast.makeText(ReportInfoActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReportInfo> call, Throwable t) {
                Toast.makeText(ReportInfoActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateData() {
        if(reportInfo != null) {
            textInfoTitle.setText(reportInfo.getData().get(0).getFields().getTitle());
            textInfoSource.setText(reportInfo.getData().get(0).getFields().getSource().get(0).getShortname());
            textInfoBody.loadData(reportInfo.getData().get(0).getFields().getBody().replace("&","and").replace("#"," No."), "text/html; charset=utf-8", "UTF-8");
            text_info_howto.loadData(reportInfo.getData().get(0).getFields().gethow_to_apply().replace("&","and").replace("#"," No."), "text/html; charset=utf-8", "UTF-8");


        }
    }

    public void openUrl(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_web:
                if(reportInfo != null)
                    openUrl(reportInfo.getData().get(0).getFields().getUrl());
            case R.id.action_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,"You might be interested in this job position "+ reportInfo.getData().get(0).getFields().getUrl());
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Job Post!");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
