package com.batson.reliefweb.Config;

import com.batson.reliefweb.Model.JobCategoryModel;
import com.batson.reliefweb.Model.JobModel;
import com.batson.reliefweb.Model.DetailsModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Titus Batson
 * Website: titusbatson.com
 * Date: 10/03/2020
 */

public interface Rest {

    @GET("jobs?appname=mike&profile=list&preset=latest&fields[include][]=career_categories&fields[include][]=experience&fields[include][]=theme&limit=5")
    public Call<JobModel> getReportLatest();

    @GET("jobs?appname=mike&profile=list&preset=latest&fields[include][]=career_categories&fields[include][]=experience&fields[include][]=theme&limit=500")
    public Call<JobModel> getReportList();



    @GET("jobs/{reportId}")
    public Call<DetailsModel> getReportInfo(@Path("reportId") String reportid);

}
