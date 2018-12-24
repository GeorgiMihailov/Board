package com.george.board;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.ExpandableListAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.CreditStatus;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.george.board.SecondActivity.convertDpToPixel;


public class Main2Activity extends AppCompatActivity {
    RestApi api;
    int companyId;
    int cardId;
    ConstraintLayout root;


    private LinearLayout circlesLayout;
    private LinearLayout.LayoutParams viewParams;
    private LinearLayout.LayoutParams lineParams;
    private LinearLayout textLayout;
    private LinearLayout.LayoutParams textParams;
    private ScrollView mainLayout;
    private LinearLayout holderLinearLayout;
    private Menues menu;
    private ArrayList<Menues> menues;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private ExpandableListAdapter mMenuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new RestApi(this);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(this)));
        setContentView(R.layout.activity_main2);
        NavigationView navigationView = findViewById(R.id.nav_view1);
        View view = navigationView.getHeaderView(0);
        TextView navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        navigationDraweAccentTitle.setBackgroundColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        View view2 = findViewById(R.id.status_custom_view);
        ImageView menuBtn = view2.findViewById(R.id.menu_btn);
        ExpandableListView expandableList = findViewById(R.id.navigationmenu);
        expandableList.setGroupIndicator(null);

        expandableList.setOnChildClickListener((expandableListView, view1, i, i1, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
            String url = item.getUrl();
            startActivity(new Intent(Main2Activity.this, SecondActivity.class).putExtra("url", url));
            return false;
        });
        expandableList.setOnGroupClickListener((expandableListView, view12, i, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
            if (eListAdapter.getChildrenCount(i) == 0){
                String url = item.getUrl();
                startActivity(new Intent(Main2Activity.this, SecondActivity.class).putExtra("url", url));
            }


            return false;
        });

        Field mDragger = null;//mRightDragger for right obviously
        try {
            mDragger = mDrawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mDragger != null) {
            mDragger.setAccessible(true);
        }
        ViewDragHelper draggerObj = null;
        try {
            if (mDragger != null) {
                draggerObj = (ViewDragHelper) mDragger
                        .get(mDrawerLayout);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            if (draggerObj != null) {
                mEdgeSize = draggerObj.getClass().getDeclaredField(
                        "mEdgeSize");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (mEdgeSize != null) {
            mEdgeSize.setAccessible(true);
        }
        int edge = 40;
        try {
            if (mEdgeSize != null) {
                edge = mEdgeSize.getInt(draggerObj);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            if (mEdgeSize != null) {
                mEdgeSize.setInt(draggerObj, edge * 5); //optimal value as for me, you may set any constant in dp
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        menuBtn.setOnClickListener(view13 -> {
            if (!mDrawerLayout.isDrawerOpen(mDrawerLayout.getId()))
                mDrawerLayout.openDrawer(navigationView);
        });
        api.checkInternet(() -> {
            Call<ArrayList<Menues>> call = api.getMenues();
            call.enqueue(new Callback<ArrayList<Menues>>() {
                @Override
                public void onResponse(@NonNull Call<ArrayList<Menues>> call, @NonNull Response<ArrayList<Menues>> response) {
                    if (response.isSuccessful()) {

                        listDataHeader = new ArrayList<>();
                        listDataChild = new HashMap<>();
                        menues = response.body();
                        int size = 0;
                        if (menues != null) {
                            size = menues.size();
                        }

                        for (int i = 0; i < size; i++) {
                            menu = menues.get(i);
                            if (menu.getSubmenu().size() > 0) {
                                ExpandedMenuModel item1 = new ExpandedMenuModel();
                                item1.setIconImgText(menu.getIcon());
                                item1.setUrl(menu.getUrl());
                                item1.setIconName(menu.getLabel());
                                listDataHeader.add(item1);
                                List<ExpandedMenuModel> expandedMenuModels1 = new ArrayList<>();
                                for (int e = 0; e < menu.getSubmenu().size(); e++) {
                                    String tl = menu.getSubmenu().get(e).getUrl();
                                    ExpandedMenuModel subMenuItem = new ExpandedMenuModel();
                                    String b = (menu.getSubmenu().get(e).getIcon());
                                    String c = new String(Character.toChars(Integer.parseInt(
                                            b, 16)));

                                    subMenuItem.setIconImgText(c);
                                    subMenuItem.setUrl(tl);
                                    subMenuItem.setIconName(menu.getSubmenu().get(e).getLabel());
                                    expandedMenuModels1.add(subMenuItem);
                                }
                                listDataChild.put(listDataHeader.get(i), expandedMenuModels1);
                            } else {
                                ExpandedMenuModel item1 = new ExpandedMenuModel();
                                item1.setIconImgText(menu.getIcon());
                                item1.setIconName(menu.getLabel());
                                item1.setUrl(menu.getUrl());
                                listDataHeader.add(item1);
                                listDataChild.put(listDataHeader.get(i), new ArrayList<>());

                            }

                        }
                        mMenuAdapter = new ExpandableListAdapter(Main2Activity.this, listDataHeader, listDataChild, expandableList);
                        expandableList.setAdapter(mMenuAdapter);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                }
            });
        });



        companyId = PreferencesManager.getCompanyId(this);
        Intent intent = getIntent();
        if (intent.hasExtra("cardId")) {
            cardId = intent.getIntExtra("cardId", 0);
        } else cardId = 0;
        root = findViewById(R.id.background_main2activity);

        CustomViewTarget<ConstraintLayout, Drawable> target = new CustomViewTarget<ConstraintLayout, Drawable>(root) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                root.setBackground(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                root.setBackground(resource);
            }
        };
        GlideApp.with(Main2Activity.this)
                .load(PreferencesManager.getUserBackground(Main2Activity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(target);


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
        LinearLayout.LayoutParams formsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
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
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textLayoutParams.setMargins(0, (int) convertDpToPixel(20, this), 0, 0);
        textLayout.setLayoutParams(textLayoutParams);
        textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(40, this));
        textParams.setMargins(0, 0, 0, (int) convertDpToPixel(30, this));
        textParams.gravity = Gravity.CENTER_VERTICAL;


        Call<CreditStatus> call = api.getStatus(companyId, cardId);
        call.enqueue(new Callback<CreditStatus>() {
            @Override
            public void onResponse(@NonNull Call<CreditStatus> call, @NonNull Response<CreditStatus> response) {
                if (response.isSuccessful()) {
                    CreditStatus status = response.body();
                    int postion = 0;
                    if (status != null) {
                        postion = Integer.valueOf(status.getId());
                    }
                    ArrayList<String> statusNames = null;
                    if (status != null) {
                        statusNames = status.getNames();
                    }
                    if (statusNames != null) {
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
                    }
                    holderLinearLayout.addView(circlesLayout);
                    holderLinearLayout.addView(textLayout);
                    mainLayout.addView(holderLinearLayout);

                }

            }

            @Override
            public void onFailure(@NonNull Call<CreditStatus> call, @NonNull Throwable t) {

            }
        });


    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



}



