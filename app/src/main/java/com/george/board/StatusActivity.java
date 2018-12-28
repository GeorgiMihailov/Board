package com.george.board;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.ExpandableListAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.helper.ViewPagerAdapter;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fragments.CreditDetailsFragment;
import fragments.CreditStatusDetailsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class StatusActivity extends FragmentActivity {
    RestApi api;
    int companyId;
    int cardId;
    ConstraintLayout root;

    ImageView logoImg;
    ImageView navigationDrawerLogo;

    private Menues menu;
    private ArrayList<Menues> menues;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private ExpandableListAdapter mMenuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(this)));
        setContentView(R.layout.status_activity);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setBackgroundColor(Color.parseColor(PreferencesManager.getPrimaryColor(StatusActivity.this)));
        tabLayout.setAlpha(0.6f);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(PreferencesManager.getAccentColor(StatusActivity.this)));


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        adapter.addFrag(new CreditStatusDetailsFragment(), "Status");
        adapter.addFrag(new CreditDetailsFragment(), "Details");

        tabLayout.setupWithViewPager(pager);

        pager.setAdapter(adapter);


        pager.setAdapter(adapter);
        api = new RestApi(this);
        NavigationView navigationView = findViewById(R.id.nav_view1);
        View view = navigationView.getHeaderView(0);
        TextView navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        navigationDraweAccentTitle.setBackgroundColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        View view2 = findViewById(R.id.status_custom_view);
        logoImg = findViewById(R.id.logo_img);
        navigationDrawerLogo = view.findViewById(R.id.navigation_view_logo);
        ImageView menuBtn = view2.findViewById(R.id.menu_btn);
        ExpandableListView expandableList = findViewById(R.id.navigationmenu);
        expandableList.setGroupIndicator(null);

        Intent intent = getIntent();
        if (intent.hasExtra("cardId")) {
            cardId = intent.getIntExtra("cardId", 0);
        } else
            cardId = 0;


        CustomViewTarget<DrawerLayout, Drawable> target = new CustomViewTarget<DrawerLayout, Drawable>(mDrawerLayout) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                mDrawerLayout.setBackground(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mDrawerLayout.setBackground(resource);
            }
        };
        GlideApp.with(StatusActivity.this)
                .load(PreferencesManager.getUserBackground(StatusActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(target);
        GlideApp.with(StatusActivity.this)
                .load(PreferencesManager.getLogo(StatusActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(logoImg);
        GlideApp.with(StatusActivity.this)
                .load(PreferencesManager.getLogo(StatusActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(navigationDrawerLogo);



        expandableList.setOnChildClickListener((expandableListView, view1, i, i1, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
            String url = item.getUrl();
            mDrawerLayout.closeDrawers();
            finish();
            startActivity(new Intent(StatusActivity.this, FormsActivity.class).putExtra("url", url));
            return false;
        });
        expandableList.setOnGroupClickListener((expandableListView, view12, i, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
            if (eListAdapter.getChildrenCount(i) == 0){
                String url = item.getUrl();
                if(item.getUrl().isEmpty()){
                    mDrawerLayout.closeDrawers();
                    startActivity(new Intent(StatusActivity.this, MyActivity_activity.class));
                }
                else {
                    finish();
                    startActivity(new Intent(StatusActivity.this, FormsActivity.class).putExtra("url", url));
                }
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
                        ExpandedMenuModel myProfile = new ExpandedMenuModel();
                        myProfile.setIconName("My Profile");
                        listDataHeader.add(myProfile);
                        listDataChild.put(listDataHeader.get(listDataHeader.size()-1), new ArrayList<>());
                        mMenuAdapter = new ExpandableListAdapter(StatusActivity.this, listDataHeader, listDataChild, expandableList);
                        expandableList.setAdapter(mMenuAdapter);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                }
            });
        });

//
//
//
//        companyId = PreferencesManager.getCompanyId(this);

//        root = findViewById(R.id.background_main2activity);
//
//        CustomViewTarget<ConstraintLayout, Drawable> target = new CustomViewTarget<ConstraintLayout, Drawable>(root) {
//            @Override
//            protected void onResourceCleared(@Nullable Drawable placeholder) {
//
//            }
//
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                root.setBackground(errorDrawable);
//            }
//
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                root.setBackground(resource);
//            }
//        };
//        GlideApp.with(StatusActivity.this)
//                .load(PreferencesManager.getUserBackground(StatusActivity.this))
//                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
//                        .override(Target.SIZE_ORIGINAL))
//                .into(target);
//        GlideApp.with(StatusActivity.this)
//                .load(PreferencesManager.getLogo(StatusActivity.this))
//                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
//                        .override(Target.SIZE_ORIGINAL))
//                .into(navigationDrawerLogo);
//        GlideApp.with(StatusActivity.this)
//                .load(PreferencesManager.getLogo(StatusActivity.this))
//                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
//                        .override(Target.SIZE_ORIGINAL))
//                .into(logoImg);
//
//
//
//        //VIEW FORMS
//        circlesLayout = new LinearLayout(this);
//        circlesLayout.setOrientation(LinearLayout.VERTICAL);
//        lineParams = new LinearLayout.LayoutParams((int) convertDpToPixel(8, this),
//                (int) convertDpToPixel(30, this));
////        lineParams.setMargins((int) convertDpToPixel(3, this),
////                (int) convertDpToPixel(3, this),
////                (int) convertDpToPixel(3, this),
////                (int) convertDpToPixel(3, this));
//        lineParams.gravity = Gravity.CENTER;
//        viewParams = new LinearLayout.LayoutParams((int) convertDpToPixel(40, this),
//                (int) convertDpToPixel(40, this));
//        LinearLayout.LayoutParams formsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        formsParams.setMargins((int) convertDpToPixel(20, this),
//                (int) convertDpToPixel(20, this),
//                0,
//                0);
//        circlesLayout.setLayoutParams(formsParams);
//
//        mainLayout = findViewById(R.id.holder_layout);
//        holderLinearLayout = new LinearLayout(this);
//        holderLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//
//        // TEXT LINEAR LAYOUT
//        textLayout = new LinearLayout(this);
//        textLayout.setOrientation(LinearLayout.VERTICAL);
//        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        textLayoutParams.setMargins(0, (int) convertDpToPixel(20, this), 0, 0);
//        textLayout.setLayoutParams(textLayoutParams);
//        textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(40, this));
//        textParams.setMargins(0, 0, 0, (int) convertDpToPixel(30, this));
//        textParams.gravity = Gravity.CENTER_VERTICAL;
//
//
//        Call<CreditStatus> call = api.getStatus(companyId, cardId);
//        call.enqueue(new Callback<CreditStatus>() {
//            @Override
//            public void onResponse(@NonNull Call<CreditStatus> call, @NonNull Response<CreditStatus> response) {
//                if (response.isSuccessful()) {
//                    CreditStatus status = response.body();
//                    int postion = 0;
//                    if (status != null) {
//                        postion = Integer.valueOf(status.getId());
//                    }
//                    ArrayList<String> statusNames = null;
//                    if (status != null) {
//                        statusNames = status.getNames();
//                    }
//                    if (statusNames != null) {
//                        for (int i = 0; i < statusNames.size(); i++) {
//
//
//
        companyId = PreferencesManager.getCompanyId(this);



    }
    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
//        }
    }

    public int getValue()
    {
        return cardId;
    }



}



