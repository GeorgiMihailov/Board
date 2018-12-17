package com.george.board.api;

import com.george.board.model.ConfigFormsList;
import com.george.board.model.CreditStatus;
import com.george.board.model.MyAccountActivity;
import com.george.board.model.SendForm;
import com.george.board.model.Menues;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @GET("card/getmenu/")
    Call<ArrayList<Menues>> getMenues();


    @GET("card/getcardfileds/{boardId}/{cardTypeId}")
    Call<ConfigFormsList> getFields(@Path("boardId") int boardId, @Path("cardTypeId") int cardTypeId);

//    @FormUrlEncoded
    @POST("card/createcard/{userId}")
        Call<ResponseBody>  sendForms(@Path("userId") String userId,@Body SendForm sendForm);

    @GET("card/getstatus")
    Call<CreditStatus> getStatus();

    @GET("card/getactivecards/{CompanyId}/{UserId}")
    Call<ArrayList<MyAccountActivity>> getMyActivity(@Path("CompanyId") int CompanyId, @Path("UserId") int UserId);

    //TEST THIS SHIT
    @Multipart
    @POST("/api/configuration/SaveConfigPolicyFiles/{id}/{name}/")
    Call<ResponseBody> uploadFile(@Path("id") String policy_number,
                                  @Path("name") String field_id, @Part("file") MultipartBody.Part file);

}
