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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.appAuth.AuthStateManager;
import com.george.board.appAuth.Configuration;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.ExpandableListAdapter;
import com.george.board.helper.MyAccountAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;
import com.george.board.model.MyAccountActivity;
import com.george.board.model.MyAccountActivityDetails;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthorizationService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyActivity_activity extends AppCompatActivity {

    int companyId;
    int userId;
    private long expiry;
    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private static String ACCESS_TOKEN = "";
    private Menues menu;
    private ArrayList<Menues> menues;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private ExpandableListAdapter mMenuAdapter;
    private ImageView navigationDrawerLogo;
    private ImageView logoImg;
    private ConstraintLayout holderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(this)));
        setContentView(R.layout.activity_my_activity);

        NavigationView navigationView = findViewById(R.id.nav_view2);
        View view = navigationView.getHeaderView(0);
        TextView navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        navigationDrawerLogo = view.findViewById(R.id.navigation_view_logo);
        logoImg = findViewById(R.id.logo_img);
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        View view2 = findViewById(R.id.top_vire);
        ImageView menuBtn = view2.findViewById(R.id.menu_btn);
        holderLayout = findViewById(R.id.first_layer_backgroud);
        ExpandableListView expandableList = findViewById(R.id.navigationmenu);
        expandableList.setGroupIndicator(null);
        String color = PreferencesManager.getPrimaryColor(this);
        navigationDraweAccentTitle.setBackgroundColor(Color.parseColor(color));
        RestApi api = new RestApi(MyActivity_activity.this);
        expandableList.setOnChildClickListener((expandableListView, view1, i, i1, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
            String url = item.getUrl();
            mDrawerLayout.closeDrawers();
            finish();
            startActivity(new Intent(MyActivity_activity.this, FormsActivity.class).putExtra("url", url));
            return false;
        });
        expandableList.setOnGroupClickListener((expandableListView, view12, i, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
            if (eListAdapter.getChildrenCount(i) == 0){
                String url = item.getUrl();
                if(item.getUrl().isEmpty()){
                    mDrawerLayout.closeDrawers();
                }
                else {
                    finish();
                    startActivity(new Intent(MyActivity_activity.this, FormsActivity.class).putExtra("url", url));
                }


            }


            return false;
        });

        CustomViewTarget<ConstraintLayout, Drawable> target = new CustomViewTarget<ConstraintLayout, Drawable>(holderLayout) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                holderLayout.setBackground(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                holderLayout.setBackground(resource);
            }
        };
        GlideApp.with(MyActivity_activity.this)
                .load(PreferencesManager.getUserBackground(MyActivity_activity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(target);

        GlideApp.with(MyActivity_activity.this)
                .load(PreferencesManager.getLogo(MyActivity_activity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(navigationDrawerLogo);
        GlideApp.with(MyActivity_activity.this)
                .load(PreferencesManager.getLogo(MyActivity_activity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(logoImg);

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
                        mMenuAdapter = new ExpandableListAdapter(MyActivity_activity.this, listDataHeader, listDataChild, expandableList);
                        expandableList.setAdapter(mMenuAdapter);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                }
            });
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view_profile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyActivity_activity.this);

        if (PreferencesManager.getTokenExpiry(MyActivity_activity.this) != 0) {
            expiry = PreferencesManager.getTokenExpiry(MyActivity_activity.this);
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (expiry <= currentTime && expiry != 0) {
            Configuration config = Configuration.getInstance(MyActivity_activity.this);
            mStateManager = AuthStateManager.getInstance(MyActivity_activity.this);
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



        userId = PreferencesManager.getUserId(MyActivity_activity.this);
        companyId = PreferencesManager.getCompanyId(MyActivity_activity.this);

        Call<ArrayList<MyAccountActivity>> call = api.getMyActivity(companyId,userId);
        call.enqueue(new Callback<ArrayList<MyAccountActivity>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<MyAccountActivity>> call, @NonNull Response<ArrayList<MyAccountActivity>> response) {
                if(response.isSuccessful()){
                    ArrayList<MyAccountActivity> accountActivity = response.body();
                    ArrayList<MyAccountActivity> main = new ArrayList<>();
                    if (accountActivity != null) {
                        for (int i = 0; i < accountActivity.size();i++){
                            ArrayList<MyAccountActivityDetails> detailsArrayList = new ArrayList<>();



                            for(int e =0; e < accountActivity.get(i).getCards().size();e++){
                                MyAccountActivityDetails details = new MyAccountActivityDetails();
                                details.setStatus(accountActivity.get(i).getCards().get(e).getStatus());
                                details.setName(accountActivity.get(i).getCards().get(e).getName());
                                details.setIcon(accountActivity.get(i).getCards().get(e).getIcon());
                                details.setDate(accountActivity.get(i).getCards().get(e).getDate());
                                details.setId(accountActivity.get(i).getCards().get(e).getId());
                                detailsArrayList.add(details);
                            }
                            MyAccountActivity myAct = new MyAccountActivity(accountActivity.get(i).getName(), detailsArrayList);
                            myAct.setIcon(accountActivity.get(i).getIcon());
                            myAct.setCards(detailsArrayList);
                            myAct.setSize(accountActivity.get(i).getSize());
                            myAct.setName(accountActivity.get(i).getName());

                            main.add(myAct);

                        }
                    }
                    MyAccountAdapter adapter = new MyAccountAdapter(MyActivity_activity.this, main);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<MyAccountActivity>> call, @NonNull Throwable t) {
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
