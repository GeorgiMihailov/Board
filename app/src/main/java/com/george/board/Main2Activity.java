package com.george.board;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.CreditStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.george.board.SecondActivity.convertDpToPixel;


public class Main2Activity extends AppCompatActivity {
    RestApi api;
    CreditStatus creditStatus;
    int companyId;
    int cardId;
    ConstraintLayout root;


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

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(this)));
        setContentView(R.layout.activity_main2);


        companyId = PreferencesManager.getCompanyId(this);
        Intent intent = getIntent();
        if (intent.hasExtra("cardId")) {
            cardId = intent.getIntExtra("cardId", 0);
        } else cardId = 0;
        root = findViewById(R.id.background_main2activity);
        Glide.with(Main2Activity.this)
                .load(PreferencesManager.getUserBackground(Main2Activity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background).format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
                .into(new SimpleTarget<Drawable>() {

                    @Override
                    public void onLoadStarted(@Nullable Drawable resource) {
                        root.setBackground(resource);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, Transition<? super Drawable> transition) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            root.setBackground(resource);
                            Drawable cardViewBackground = root.getBackground();
                            cardViewBackground.setColorFilter(0x5F000000, PorterDuff.Mode.SRC_ATOP);
                        }
                    }
                });


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


        Call<CreditStatus> call = api.getStatus(companyId, cardId);
        call.enqueue(new Callback<CreditStatus>() {
            @Override
            public void onResponse(Call<CreditStatus> call, Response<CreditStatus> response) {
                if (response.isSuccessful()) {
                    CreditStatus status = response.body();
                    int postion = Integer.valueOf(status.getId());
                    ArrayList<String> statusNames = status.getNames();
                    for (int i = 0; i < statusNames.size(); i++) {


                        //FORM VIEWS
                        View view = new View(Main2Activity.this);
                        if (postion == i) {
                            view.setBackground(getDrawable(R.drawable.status_shape_in_progress));
                        } else if (postion < statusNames.indexOf(statusNames.get(i))) {
                            view.setBackground(getDrawable(R.drawable.status_shape_to_do));
                        } else if (postion > statusNames.indexOf(statusNames.get(i)))
                            view.setBackground(getDrawable(R.drawable.status_shape_completed));


                        view.setLayoutParams(viewParams);
                        circlesLayout.addView(view);
                        if (i < statusNames.size() - 1) {
                            View line = new View(Main2Activity.this);
                            line.setBackgroundColor(getColor(R.color.colorAccent));
                            line.setLayoutParams(lineParams);
                            circlesLayout.addView(line);
                        }


                        //TEXT VIEWS
                        TextView statusText = new TextView(Main2Activity.this);
                        statusText.setGravity(Gravity.START);
                        statusText.setPadding((int) convertDpToPixel(30, Main2Activity.this), (int) convertDpToPixel(6, Main2Activity.this), 0, 0);
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



