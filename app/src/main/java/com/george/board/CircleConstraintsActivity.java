package com.george.board;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.george.board.api.RestApi;
import com.george.board.helper.MyRecyclerViewAdapter;
import com.george.board.model.Menues;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CircleConstraintsActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener  {
    public MyRecyclerViewAdapter adapter;
    ArrayList<Menues> menues;
    RestApi api;
    private Menues menu;
    private int subItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_layout);
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        api=new RestApi(this);



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