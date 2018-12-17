package com.george.board;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.george.board.api.RestApi;
import com.george.board.model.CreditStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.george.board.SecondActivity.convertDpToPixel;


public class Main2Activity extends AppCompatActivity {
    RestApi api;
    CreditStatus creditStatus;


    private LinearLayout.LayoutParams formsParams;
    private LinearLayout circlesLayout;
    private LinearLayout.LayoutParams viewParams;
    private LinearLayout.LayoutParams lineParams;
    private LinearLayout textLayout;
    private LinearLayout.LayoutParams textLayoutParams;
    private LinearLayout.LayoutParams textParams;
    private ScrollView mainLayout;
    private LinearLayout holderLinearLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new RestApi(this);
        Call<CreditStatus> call = api.getStatus();
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        setContentView(R.layout.activity_main2);



        //VIEW FORMS
        circlesLayout = new LinearLayout(this);
        circlesLayout.setOrientation(LinearLayout.VERTICAL);
        lineParams = new LinearLayout.LayoutParams((int) convertDpToPixel(8, this),
                (int) convertDpToPixel(30, this));
//        lineParams.setMargins((int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this));
        lineParams.gravity = Gravity.CENTER;
        viewParams = new LinearLayout.LayoutParams((int) convertDpToPixel(40, this),
                (int) convertDpToPixel(40, this));
        formsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        formsParams.setMargins((int) convertDpToPixel(20, this),
                (int) convertDpToPixel(20, this),
                0,
                0);
        circlesLayout.setLayoutParams(formsParams);

        mainLayout = findViewById(R.id.holder_layout);
        holderLinearLayout = new LinearLayout(this);
        holderLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // TEXT LINEAR LAYOUT
        textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textLayoutParams.setMargins(0, (int) convertDpToPixel(20, this), 0, 0);
        textLayout.setLayoutParams(textLayoutParams);
        textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(40, this));
        textParams.setMargins(0, 0, 0, (int) convertDpToPixel(30, this));
        textParams.gravity = Gravity.CENTER_VERTICAL;



        call.enqueue(new Callback<CreditStatus>() {
            @Override
            public void onResponse(Call<CreditStatus> call, Response<CreditStatus> response) {
                if (response.isSuccessful()){
                  CreditStatus status = response.body();
                    ArrayList<String> statusNames = status.getNames();
                    for (int i = 0; i<statusNames.size();i++){


                        //FORM VIEWS
                        View view = new View(Main2Activity.this);
                        if (statusNames.get(i).equals(status.getId())){
                            view.setBackground(getDrawable(R.drawable.status_shape_in_progress));
                        }else view.setBackground(getDrawable(R.drawable.status_shape_completed));
                        view.setLayoutParams(viewParams);
                        circlesLayout.addView(view);
                        if (i < statusNames.size()-1){
                            View line = new View(Main2Activity.this);
                            line.setBackgroundColor(getColor(R.color.colorAccent));
                            line.setLayoutParams(lineParams);
                            circlesLayout.addView(line);
                        }


                        //TEXT VIEWS
                        TextView statusText = new TextView(Main2Activity.this);
                        statusText.setGravity(Gravity.START);
                        statusText.setPadding((int) convertDpToPixel(30,Main2Activity.this),(int) convertDpToPixel(6,Main2Activity.this),0,0);
                        statusText.setText(statusNames.get(i));
                        statusText.setTextSize(18);
                        statusText.setTextColor(getColor(R.color.colorAccent));
                        statusText.setLayoutParams(textParams);
                        textLayout.addView(statusText);


                    }
                    holderLinearLayout.addView(circlesLayout);
                    holderLinearLayout.addView(textLayout);
                    mainLayout.addView(holderLinearLayout);

                }

            }

            @Override
            public void onFailure(Call<CreditStatus> call, Throwable t) {

            }
        });







    }





    }



