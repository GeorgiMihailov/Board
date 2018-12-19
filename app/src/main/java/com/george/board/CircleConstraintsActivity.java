package com.george.board;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.helper.MyRecyclerViewAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.Menues;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircleConstraintsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {
    public MyRecyclerViewAdapter adapter;
    ArrayList<Menues> menues;
    RestApi api;
    private Menues menu;
    private int subItem;
    ConstraintLayout root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        root = findViewById(R.id.background1);
        Glide.with(CircleConstraintsActivity.this)
                .load(PreferencesManager.getUserBackground(CircleConstraintsActivity.this))
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


        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        api = new RestApi(this);


        api.checkInternet(new Runnable() {
            @Override
            public void run() {
                Call<ArrayList<Menues>> call = api.getMenues();
                call.enqueue(new Callback<ArrayList<Menues>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Menues>> call, Response<ArrayList<Menues>> response) {
                        if (response.isSuccessful()) {
                            menues = response.body();
                            for (int i = 0; i < menues.size(); i++) {
                                menu = menues.get(i);


                            }
                            adapter = new MyRecyclerViewAdapter(CircleConstraintsActivity.this, menues.get(0).getSubmenu());
                            recyclerView.setAdapter(adapter);

                            adapter.setClickListener(CircleConstraintsActivity.this);
                        }

                    }

                    @Override
                    public void onFailure(Call<ArrayList<Menues>> call, Throwable t) {

                    }
                });
            }
        });


    }


    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position).getLabel() + " on row number " + position, Toast.LENGTH_SHORT).show();

    }
}