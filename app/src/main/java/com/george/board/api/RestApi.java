package com.george.board.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.george.board.BuildConfig;
import com.george.board.helper.CheckConnection;
import com.george.board.helper.LoggingInterceptor;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.CreditStatus;
import com.george.board.model.MyAccountActivity;
import com.george.board.model.SendForm;
import com.george.board.model.Menues;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApi {
    public static final int request_max_time_in_seconds = 20;
    private Context activity;

    public RestApi(Context activity) {
        this.activity = activity;
    }

    public Retrofit getRetrofitInstance() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .readTimeout(request_max_time_in_seconds, TimeUnit.SECONDS)
                .connectTimeout(request_max_time_in_seconds, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    public ApiService request() {
        return getRetrofitInstance().create(ApiService.class);
    }

    public Call<ArrayList<Menues>> getMenues() {
        return request().getMenues();
    }
    public Call<ConfigFormsList> getForms(int cardTypeId, int boardId){
        return request().getFields(cardTypeId,boardId);
    }
    public Call<ResponseBody> UploadFile(String policy_number, String field_id, MultipartBody.Part file) {
        return request().uploadFile(policy_number, field_id, file);
    }
    public Call<ResponseBody> sendForms(String userId,SendForm sendForm){
        return request().sendForms(userId,sendForm);
    }
    public Call<CreditStatus> getStatus (){
        return request().getStatus();
    }
    public Call<ArrayList<MyAccountActivity>> getMyActivity (int CompanyId, int UserId){
        return request().getMyActivity(CompanyId,UserId);
    }
    public void checkInternet(Runnable callback){
        if (CheckConnection.CheckInternetConnectivity(activity, true, callback )){
            callback.run();
        }
    }
    public void checkInternet (Runnable callback, boolean showConnectionDialog){
        if (CheckConnection.CheckInternetConnectivity(activity,showConnectionDialog,callback))
            callback.run();
        else {
            Toast.makeText(activity, "Connection failed, please check your connection in settings", Toast.LENGTH_LONG).show();
        }
    }

    public  void  checkInternet (Runnable callback, boolean showConnetionDialog, String message){
        if (CheckConnection.CheckInternetConnectivity(activity,showConnetionDialog,callback))
            callback.run();
        else {
            if (showConnetionDialog)
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
            else
                Log.d("Connection failed", "" + message);
        }
    }
}
