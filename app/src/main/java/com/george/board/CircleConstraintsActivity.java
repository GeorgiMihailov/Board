package com.george.board;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.MyRecyclerViewAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.Menues;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircleConstraintsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    public MyRecyclerViewAdapter adapter;
    ArrayList<Menues> menues;
    RestApi api;

    ConstraintLayout root;
    private int parentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));



        root = findViewById(R.id.background1);
        CustomViewTarget<ConstraintLayout, Drawable> target = new CustomViewTarget<ConstraintLayout, Drawable>(root) {
            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                root.setBackground(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                root.setBackground(resource);
            }

            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }
        } ;


        GlideApp.with(CircleConstraintsActivity.this)
                .load(PreferencesManager.getUserBackground(CircleConstraintsActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(target);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        api = new RestApi(this);
        Intent intent = getIntent();
        if (intent.hasExtra("parentId")){
            parentId = intent.getIntExtra("parentId",0);
            api.checkInternet(() -> {
                Call<ArrayList<Menues>> call = api.getMenues();
                call.enqueue(new Callback<ArrayList<Menues>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<Menues>> call, @NonNull Response<ArrayList<Menues>> response) {
                        if (response.isSuccessful()) {
                            menues = response.body();
                            assert menues != null;
                            for (int i = 0; i <menues.size();i++){
                                if (menues.get(i).getId() == parentId){
                                    adapter = new MyRecyclerViewAdapter(CircleConstraintsActivity.this, menues.get(i).getSubmenu());
                                    recyclerView.setAdapter(adapter);

                                    adapter.setClickListener(CircleConstraintsActivity.this);
                                }
                            }


                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                    }
                });
            });
                    }


    }


    @Override
    public void onItemClick(View view, int position,Menues menue) {

        String url = menue.getUrl();
        startActivity(new Intent(CircleConstraintsActivity.this, FormsActivity.class).putExtra("url", url));

    }
}