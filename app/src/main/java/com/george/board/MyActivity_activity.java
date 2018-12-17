package com.george.board;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.george.board.api.RestApi;
import com.george.board.appAuth.AuthStateManager;
import com.george.board.appAuth.Configuration;
import com.george.board.helper.MyAccountAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.MyAccountActivity;
import com.george.board.model.MyAccountActivityDetails;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyActivity_activity extends AppCompatActivity {

   private RestApi api;

    int companyId;
    int userId;
    private long expiry;
    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private ExecutorService mExecutor;
    private Configuration mConfiguration;
    private static String ACCESS_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setContentView(R.layout.activity_my_activity);

        api=new RestApi(MyActivity_activity.this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_profile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyActivity_activity.this);

        if (PreferencesManager.getTokenExpiry(MyActivity_activity.this) != 0) {
            expiry = PreferencesManager.getTokenExpiry(MyActivity_activity.this);
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (expiry <= currentTime && expiry != 0) {
            Configuration config = Configuration.getInstance(MyActivity_activity.this);
            mStateManager = AuthStateManager.getInstance(MyActivity_activity.this);
            mExecutor = Executors.newSingleThreadExecutor();
            mConfiguration = Configuration.getInstance(MyActivity_activity.this);
            if (mAuthService == null) {
                mAuthService = new AuthorizationService(MyActivity_activity.this, new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());
            }
            mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, (accessToken, idToken, ex) -> {
                if (ex != null) {
                    return;
                }
                ACCESS_TOKEN = accessToken;
                PreferencesManager.addAccessToken(ACCESS_TOKEN,MyActivity_activity.this);
                if (mStateManager.getCurrent().getAccessTokenExpirationTime() != null) {
                    expiry = mStateManager.getCurrent().getAccessTokenExpirationTime();
                    PreferencesManager.addTokenExpiry(MyActivity_activity.this, expiry);
                }
            });
        }


        companyId = PreferencesManager.getCompanyId(MyActivity_activity.this);
        userId = PreferencesManager.getUserId(MyActivity_activity.this);


        Call<ArrayList<MyAccountActivity>> call = api.getMyActivity(companyId,userId);
        call.enqueue(new Callback<ArrayList<MyAccountActivity>>() {
            @Override
            public void onResponse(Call<ArrayList<MyAccountActivity>> call, Response<ArrayList<MyAccountActivity>> response) {
                ArrayList<MyAccountActivity> accountActivity = response.body();
                ArrayList<MyAccountActivity> main = new ArrayList<>();
                for (int i = 0; i < accountActivity.size();i++){
                    ArrayList<MyAccountActivityDetails> detailsArrayList = new ArrayList<>();



                    for(int e =0; e < accountActivity.get(i).getCards().size();e++){
                        MyAccountActivityDetails details = new MyAccountActivityDetails();
                        details.setStatus(accountActivity.get(i).getCards().get(e).getStatus());
                        details.setName(accountActivity.get(i).getCards().get(e).getName());
                        details.setIcon(accountActivity.get(i).getCards().get(e).getIcon());
                        detailsArrayList.add(details);
                    }
                    MyAccountActivity myAct = new MyAccountActivity(accountActivity.get(i).getName(), detailsArrayList);
                    myAct.setIcon(accountActivity.get(i).getIcon());
                    myAct.setCards(detailsArrayList);
                    myAct.setName(accountActivity.get(i).getName());

                    main.add(myAct);

                }
                MyAccountAdapter adapter = new MyAccountAdapter(MyActivity_activity.this, main);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onFailure(Call<ArrayList<MyAccountActivity>> call, Throwable t) {
                Toast.makeText(MyActivity_activity.this, "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
            }
        });

//
//        ArrayList<MyAccountActivityDetails> activityDetails = new ArrayList<>();
//        MyAccountActivityDetails myAccountActivityDetails = new MyAccountActivityDetails();
//        myAccountActivityDetails.setName("AAAA");
//        myAccountActivityDetails.setStatus("AAAAAAAA");
//
//        activityDetails.add(myAccountActivityDetails);
//
////        activityDetails.add(accountActivityDetails);
//        MyAccountActivity accountActivity = new MyAccountActivity("TEST LIST", activityDetails);
//        accountActivity.setIcon("\ue901");
//        accountActivity.setName("YEAAAH");
//        MyAccountActivity accountActivity1 = new MyAccountActivity("TEST LIST", activityDetails);
//        accountActivity1.setIcon("\ue901");
//        accountActivity1.setName("YEAAAH1");
//        MyAccountActivity accountActivity2 = new MyAccountActivity("TEST LIST", activityDetails);
//        accountActivity2.setIcon("\ue901");
//        accountActivity2.setCards(activityDetails);
//        accountActivity2.setName("YEAAAH2");
//        ArrayList<MyAccountActivity> genres = new ArrayList<>();
//        genres.add(accountActivity);
//        genres.add(accountActivity1);
//        genres.add(accountActivity2);
//
//        //instantiate your adapter with the list of genres

    }
}
