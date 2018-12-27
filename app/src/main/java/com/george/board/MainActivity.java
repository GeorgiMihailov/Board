package com.george.board;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.george.board.helper.PreferencesManager;
import com.george.board.model.BoardCard;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import de.hdodenhof.circleimageview.CircleImageView;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RestApi api;

    ArrayList<Menues> menues;
    private Menues menu;
    TextView card;



    private String colorPrimary;
    private String colorPrimaryDark;
    private String colorAccent;

    TextView add;
    TextView navigationDraweAccentTitle;
    ConstraintLayout rootView;
    ImageView logoImg;
    ImageView navigationDrawerLogo;
    ArrayList<BoardCard> boardCards;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    ConstraintLayout constraintLayout;
    int angle = 0;
    int radius = 130;



    private CircleImageView centerImage;
    private Typeface ta;
    private Typeface tf;
    private RelativeLayout.LayoutParams params;
    private ConstraintSet constraintSet;
    private String g;

    //APP AUTH INIT
    private static final String TAG = "TokenActivity";

    private static final String KEY_USER_INFO = "userInfo";

    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    private ExecutorService mExecutor;
    private Configuration mConfiguration;
    private static String ACCESS_TOKEN = "";
    public int userId;
    public Configuration config;
    private String name;
    private String profilePicture;
    public int companyId;
    private String lastName;
    private URL url;
    private String logo;
    ImageView menuBtn;
    private Window window;
    private RelativeLayout pb;
    private LinearLayout.LayoutParams paramsForIconAndTextHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        config = Configuration.getInstance(this);
        mStateManager = AuthStateManager.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();
        mConfiguration = Configuration.getInstance(this);
        if (config.hasConfigurationChanged()) {
            Toast.makeText(
                    this,
                    "Configuration change detected",
                    Toast.LENGTH_SHORT)
                    .show();
            signOut();

        }
        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());

        api = new RestApi(this);
        boardCards = new ArrayList<>();
        constraintSet = new ConstraintSet();
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.circle_view_holder);
        rootView = findViewById(R.id.root);

        centerImage = findViewById(R.id.sun_image);
        pb = findViewById(R.id.progress_bar_holder);
        menuBtn = findViewById(R.id.menu_btn);
        expandableList = findViewById(R.id.navigationmenu);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        logoImg = findViewById(R.id.logo_img);
        navigationDrawerLogo = view.findViewById(R.id.navigation_view_logo);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        expandableList.setGroupIndicator(null);
        centerImage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MyActivity_activity.class)));
        ta = ResourcesCompat.getFont(this, R.font.bankicon);
        tf = ResourcesCompat.getFont(this, R.font.fontawesome_webfont);
        params = new RelativeLayout.LayoutParams(
                (int)convertDpToPixel(65,this),   (int)convertDpToPixel(65,this));
        paramsForIconAndTextHolder = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsForIconAndTextHolder.gravity = Gravity.CENTER_HORIZONTAL;
        expandableList.setOnChildClickListener((expandableListView, view1, i, i1, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
            String url = item.getUrl();
            mDrawerLayout.closeDrawers();
            startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("url", url));
            return false;
        });
        expandableList.setOnGroupClickListener((expandableListView, view12, i, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
            if (eListAdapter.getChildrenCount(i) == 0){
                String url = item.getUrl();
                mDrawerLayout.closeDrawers();
                if(item.getUrl().isEmpty()){
                    mDrawerLayout.closeDrawers();
                    startActivity(new Intent(MainActivity.this, MyActivity_activity.class));
                }
               else startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("url", url));
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
        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mDrawerLayout.isDrawerOpen(mDrawerLayout.getId()))
                    mDrawerLayout.openDrawer(navigationView);
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





    private void prepareListData() {

                listDataHeader = new ArrayList<>();
                listDataChild = new HashMap<>();
                constraintSet.constrainWidth(R.id.sun_image, (int) convertDpToPixel(130, MainActivity.this));
                constraintSet.constrainHeight(R.id.sun_image, (int) convertDpToPixel(130, MainActivity.this));
                CustomViewTarget<ConstraintLayout, Drawable> target = new CustomViewTarget<ConstraintLayout, Drawable>(rootView) {
                    @Override
                    protected void onResourceCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        rootView.setBackground(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        rootView.setBackground(resource);
                    }
                };
        CustomViewTarget<CircleImageView, Drawable> logo = new CustomViewTarget<CircleImageView, Drawable>(centerImage) {
            @Override
            protected void onResourceCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                centerImage.setBackground(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                centerImage.setImageDrawable(resource);
            }
        };

                api.checkInternet(() -> {
                    Call<ArrayList<Menues>> call = api.getMenues();
                    call.enqueue(new Callback<ArrayList<Menues>>() {
                        @Override
                        public void onResponse(@NonNull Call<ArrayList<Menues>> call, @NonNull Response<ArrayList<Menues>> response) {
                            if (response.isSuccessful()) {
                                menues = response.body();
                                int size = 0;
                                if (menues != null) {
                                    size = menues.size();
                                }
                                double angleSize = 360 / size;
                                for (int i = 0; i < size; i++) {
                                    if (i <= 7){
                                        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                                        linearLayout.setId(View.generateViewId());
                                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                                        linearLayout.setGravity(Gravity.CENTER);
                                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

                                        TextView iconHolder = new TextView(MainActivity.this);
                                        iconHolder.setWidth((int) convertDpToPixel(60, MainActivity.this));
                                        iconHolder.setId(View.generateViewId());
                                        iconHolder.setHeight((int) convertDpToPixel(60, MainActivity.this));

                                        if (menues.get(i).getIcon().startsWith("e")) {
                                            iconHolder.setPadding((int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), 0);
                                            iconHolder.setTextSize(60);
                                            ta = ResourcesCompat.getFont(MainActivity.this, R.font.bankicon);
                                            iconHolder.setTypeface(ta);
                                            if (!menues.get(i).getIcon().isEmpty()) {
                                                String v = (menues.get(i).getIcon());
                                                g = new String(Character.toChars(Integer.parseInt(
                                                        v, 16)));
                                            }
                                        } else {
                                            iconHolder.setPadding((int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(15, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), 0);
                                            iconHolder.setTextSize(32);
                                            ta = ResourcesCompat.getFont(MainActivity.this, R.font.fontawesome_webfont);
                                            iconHolder.setTypeface(ta);
                                            if (!menues.get(i).getIcon().isEmpty()) {
                                                String v = (menues.get(i).getIcon());
                                                g = new String(Character.toChars(Integer.parseInt(
                                                        v, 16)));
                                            }

                                        }


                                        iconHolder.setText(g);
                                        iconHolder.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        iconHolder.setTextColor(Color.parseColor("#9E9E9E"));
                                        iconHolder.setBackground(getDrawable(R.drawable.rounded_textview));
                                        iconHolder.setElevation(convertDpToPixel(8,MainActivity.this));
                                        iconHolder.setLayoutParams(params);
                                        int finalI = i;
                                        int finalI1 = i;
                                        iconHolder.setOnClickListener(view -> {

                                            if (menues.get(finalI).getUrl()!=null) {
                                                String url1 =  menues.get(finalI).getUrl();
                                                startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("url", url1));
                                            }
                                            else {
                                                int parent = menues.get(finalI1).getId();
                                                startActivity(new Intent(MainActivity.this, CircleConstraintsActivity.class).putExtra("parentId", parent));
                                            }
                                        });
                                        linearLayout.addView(iconHolder);

                                        RelativeLayout.LayoutParams paramsForText = new RelativeLayout.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        paramsForText.addRule(RelativeLayout.BELOW, iconHolder.getId());



                                        TextView iconText = new TextView(MainActivity.this);
                                        iconText.setId(View.generateViewId());
                                        iconText.setTextSize(18);
                                        iconText.setTypeface(tf, Typeface.NORMAL);
                                        iconText.setTextColor(getResources().getColor(android.R.color.white));
                                        if (menues.get(i).getLabel().length() <= 10) {
                                            iconText.setText(menues.get(i).getLabel());
                                        } else {
                                            String substring = menues.get(i).getLabel().substring(0, 6);
                                            String text = substring + "...";
                                            iconText.setText(text);
                                        }
                                        iconText.setLayoutParams(paramsForText);
                                        linearLayout.addView(iconText);
                                        linearLayout.setLayoutParams(paramsForIconAndTextHolder);
                                        constraintLayout.addView(linearLayout);


                                        constraintSet.clone(constraintLayout);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.BOTTOM, R.id.circle_view_holder, ConstraintSet.BOTTOM, (int)convertDpToPixel(20, MainActivity.this));
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.TOP, R.id.circle_view_holder, ConstraintSet.TOP, 0);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.START, R.id.circle_view_holder, ConstraintSet.START, 0);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.END, R.id.circle_view_holder, ConstraintSet.END, 0);
                                        constraintSet.setVerticalBias(R.id.sun_image, 0.77f);
                                        constraintSet.constrainCircle(linearLayout.getId(), R.id.sun_image, (int) convertDpToPixel(118, MainActivity.this), angle);
                                        angle += angleSize;

                                        constraintSet.applyTo(constraintLayout);

                                    }

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
                                GlideApp.with(MainActivity.this)
                                        .load(PreferencesManager.getUserBackground(MainActivity.this))
                                        .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                                                .override(Target.SIZE_ORIGINAL))
                                        .into(target);

                                GlideApp.with(MainActivity.this)
                                        .load(PreferencesManager.getUserPicture(MainActivity.this))
                                        .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                                                .override(Target.SIZE_ORIGINAL))
                                        .into(logo);
                                GlideApp.with(MainActivity.this)
                                        .load(PreferencesManager.getLogo(MainActivity.this))
                                        .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                                                .override(Target.SIZE_ORIGINAL))
                                        .into(navigationDrawerLogo);
                                GlideApp.with(MainActivity.this)
                                        .load(PreferencesManager.getLogo(MainActivity.this))
                                        .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                                                .override(Target.SIZE_ORIGINAL))
                                        .into(logoImg);

                                mMenuAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild, expandableList);
                                expandableList.setAdapter(mMenuAdapter);
                                window = getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(MainActivity.this)));
                                centerImage.setVisibility(View.VISIBLE);
                                navigationDraweAccentTitle.setBackgroundColor(Color.parseColor(colorPrimary));
                                pb.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                        }
                    });
                });





    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    //APP AUTH IMPLEMENTATION

    @MainThread
    private void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);
        userId = 0;
        PreferencesManager.setUserId(this, userId);


        this.finishAffinity();
    }


    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {

        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);

    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
//            displayNotAuthorized("Client authentication method is unsupported");
            return;
        }


        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }


    @SuppressLint("WrongThread")
    @WorkerThread
    private void handleCodeExchangeResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        long expiresAt = 0;
        if (tokenResponse != null && tokenResponse.accessTokenExpirationTime != null) {
            expiresAt = tokenResponse.accessTokenExpirationTime;
        }
        PreferencesManager.addTokenExpiry(MainActivity.this, expiresAt);

        if (!mStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
//            runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            AuthState state = mStateManager.getCurrent();
            AuthorizationServiceDiscovery discoveryDoc =
                    state.getAuthorizationServiceConfiguration().discoveryDoc;

         fetchUserInfo();


        }
    }

    @MainThread
    public void fetchUserInfo() {
        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }


    @MainThread
    public void fetchUserInfo(String accessToken, String idToken, AuthorizationException ex) {
        if (ex != null) {
            Log.e(TAG, "Token refresh failed when fetching user info");
            mUserInfoJson.set(null);
        }
        AuthorizationServiceDiscovery discovery =
                mStateManager.getCurrent()
                        .getAuthorizationServiceConfiguration()
                        .discoveryDoc;

        URL userInfoEndpoint = null;
        try {
            if (discovery != null) {
                userInfoEndpoint =
                        mConfiguration.getUserInfoEndpointUri() != null
                                ? new URL(mConfiguration.getUserInfoEndpointUri().toString())
                                : new URL(discovery.getUserinfoEndpoint().toString());
            }
        } catch (MalformedURLException urlEx) {
            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
        }

        URL finalUserInfoEndpoint = userInfoEndpoint;
        mExecutor.submit(() -> {
            try {
                HttpURLConnection conn =
                        null;
                if (finalUserInfoEndpoint != null) {
                    conn = (HttpURLConnection) finalUserInfoEndpoint.openConnection();
                }
                if (conn != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                }
                if (conn != null) {
                    conn.setInstanceFollowRedirects(false);
                }
                String response = null;
                if (conn != null) {
                    response = Okio.buffer(Okio.source(conn.getInputStream()))
                            .readString(Charset.forName("UTF-8"));
                }
                mUserInfoJson.set(new JSONObject(response));
                ACCESS_TOKEN = accessToken;
                companyId = mUserInfoJson.get().getInt("CompanyId");
                PreferencesManager.setCompanyId(companyId, MainActivity.this);
                userId = mUserInfoJson.get().getInt("id");
                PreferencesManager.setUserId(MainActivity.this, userId);
                profilePicture = mUserInfoJson.get().getString("picture");
                PreferencesManager.setUserPicture(profilePicture, MainActivity.this);
                name = mUserInfoJson.get().getString("given_name");
                PreferencesManager.setUserName(name, MainActivity.this);
                lastName = mUserInfoJson.get().getString("family_name");
                PreferencesManager.setUserLastname(lastName, MainActivity.this);
                PreferencesManager.addAccessToken(ACCESS_TOKEN, MainActivity.this);
                url = new URL(mUserInfoJson.get().getString("BackgroundPicture"));
                PreferencesManager.setUserBackground(MainActivity.this, url.toString());
                colorPrimary = mUserInfoJson.get().getString("ColorPrimary");
                colorPrimaryDark = mUserInfoJson.get().getString("ColorPrimaryDark");
                colorAccent = mUserInfoJson.get().getString("ColorAccent");
                logo = mUserInfoJson.get().getString("Logo");
                PreferencesManager.setLogo(MainActivity.this,logo);
                PreferencesManager.setPrimaryColor(MainActivity.this,colorPrimary);
                PreferencesManager.setPrimaryDarkColor(MainActivity.this,colorPrimaryDark);
                PreferencesManager.setAccentColor(MainActivity.this,colorAccent);
                prepareListData();


            } catch (IOException ioEx) {
                Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
                showSnackbar("Fetching user info failed");

            } catch (JSONException jsonEx) {
                Log.e(TAG, "Failed to parse userinfo response");
                showSnackbar("Failed to parse user info");
            }
        });
    }

    @MainThread
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.drawer_layout),
                message,
                Snackbar.LENGTH_LONG)
                .show();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        if (mStateManager.getCurrent().isAuthorized()) {
          fetchUserInfo();

            return;
        }



        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            mStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);



        }


    }


    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAuthService.dispose();
        mExecutor.shutdownNow();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

    }
    }


