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
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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
import com.george.board.helper.OnExpandableListElementClick;
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

public class MainActivity extends AppCompatActivity implements OnExpandableListElementClick {

    private RestApi api;
    private int subItem;
    ArrayList<Menues> menues;
    private Menues menu;
    private NavigationView navigationView;
    TextView card;



    private String colorPrimary;
    private String colorPrimaryDark;
    private String colorAccent;

    TextView add;
    TextView navigationDraweAccentTitle;
    ConstraintLayout rootView;
    int cardId;
    int boardId;
    ArrayList<BoardCard> boardCards;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private DrawerLayout mDrawerLayout;
    ConstraintLayout constraintLayout;
    int angle = 0;
    int radius = 130;
    private long expiry;


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
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        expandableList.setGroupIndicator(null);
        centerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MyActivity_activity.class));
            }
        });
        ta = ResourcesCompat.getFont(this, R.font.bankicon);
        tf = ResourcesCompat.getFont(this, R.font.fontawesome_webfont);
        params = new RelativeLayout.LayoutParams(
                210, 210);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
                ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
                String url = item.getUrl();
                startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("url", url));
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
                ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
                if (eListAdapter.getChildrenCount(i) == 0){
                    String url = item.getUrl();
                    startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtra("url", url));
                }

                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });

        Field mDragger = null;//mRightDragger for right obviously
        try {
            mDragger = mDrawerLayout.getClass().getDeclaredField(
                    "mLeftDragger");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mDragger.setAccessible(true);
        ViewDragHelper draggerObj = null;
        try {
            draggerObj = (ViewDragHelper) mDragger
                    .get(mDrawerLayout);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Field mEdgeSize = null;
        try {
            mEdgeSize = draggerObj.getClass().getDeclaredField(
                    "mEdgeSize");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        mEdgeSize.setAccessible(true);
        int edge = 40;
        try {
            edge = mEdgeSize.getInt(draggerObj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            mEdgeSize.setInt(draggerObj, edge * 5); //optimal value as for me, you may set any constant in dp
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




    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(0, Menu.FIRST, Menu.NONE, "TEST ITEM").setIcon(R.drawable.ic_add_ticket);
        SubMenu sub = menu.addSubMenu(0, Menu.FIRST, Menu.NONE, "SUB ITEM");
        subItem = sub.getItem(2).getItemId();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == 2)
            startActivity(new Intent(this, SecondActivity.class));

        return super.onOptionsItemSelected(item);
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

                api.checkInternet(() -> {
                    Call<ArrayList<Menues>> call = api.getMenues();
                    call.enqueue(new Callback<ArrayList<Menues>>() {
                        @Override
                        public void onResponse(Call<ArrayList<Menues>> call, Response<ArrayList<Menues>> response) {
                            if (response.isSuccessful()) {
                                menues = response.body();
                                int size = menues.size();
                                double angleSize = 360 / size;
                                if (menues != null) {
                                    for (int i = 0; i < size; i++) {
                                        RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                                        relativeLayout.setId(View.generateViewId());
                                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

                                        TextView iconHolder = new TextView(MainActivity.this);
                                        iconHolder.setWidth((int) convertDpToPixel(70, MainActivity.this));
                                        iconHolder.setId(View.generateViewId());
                                        iconHolder.setHeight((int) convertDpToPixel(70, MainActivity.this));

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
                                            iconHolder.setPadding((int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), 0);
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
                                        iconHolder.setTextColor(getColor(R.color.colorAccent));
                                        iconHolder.setBackground(getDrawable(R.drawable.rounded_textview));
                                        iconHolder.setLayoutParams(params);
                                        relativeLayout.addView(iconHolder);

                                        RelativeLayout.LayoutParams paramsForText = new RelativeLayout.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        paramsForText.addRule(RelativeLayout.BELOW, iconHolder.getId());
                                        paramsForText.addRule(RelativeLayout.CENTER_HORIZONTAL);

                                        TextView iconText = new TextView(MainActivity.this);
                                        iconText.setLayoutParams(paramsForText);
                                        iconText.setId(View.generateViewId());
                                        iconText.setTextSize(18);
                                        iconText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        iconText.setTypeface(tf, Typeface.BOLD);
                                        iconText.setTextColor(getColor(R.color.colorAccent));
                                        if (menues.get(i).getLabel().length() <= 10) {
                                            iconText.setText(menues.get(i).getLabel());
                                        } else {
                                            String substring = menues.get(i).getLabel().substring(0, 6);
                                            String text = substring + "...";
                                            iconText.setText(text);
                                        }

                                        relativeLayout.addView(iconText);
                                        constraintLayout.addView(relativeLayout);


                                        constraintSet.clone(constraintLayout);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.BOTTOM, R.id.circle_view_holder, ConstraintSet.BOTTOM, 0);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.TOP, R.id.circle_view_holder, ConstraintSet.TOP, 0);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.START, R.id.circle_view_holder, ConstraintSet.START, 0);
                                        constraintSet.connect(R.id.sun_image, ConstraintSet.END, R.id.circle_view_holder, ConstraintSet.END, 0);
                                        constraintSet.setVerticalBias(R.id.sun_image, 0.77f);
                                        constraintSet.constrainCircle(relativeLayout.getId(), R.id.sun_image, (int) convertDpToPixel(120, MainActivity.this), angle);
                                        angle += angleSize;

                                        constraintSet.applyTo(constraintLayout);

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
                                    GlideApp.with(MainActivity.this)
                                            .load(PreferencesManager.getUserBackground(MainActivity.this))
                                            .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                                                    .override(Target.SIZE_ORIGINAL))
                                            .into(target);
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
                        }

                        @Override
                        public void onFailure(Call<ArrayList<Menues>> call, Throwable t) {

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

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public void onExpandableListElementClick(ExpandedMenuModel model, int pos) {

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
        if (tokenResponse.accessTokenExpirationTime != null) {
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
            userInfoEndpoint =
                    mConfiguration.getUserInfoEndpointUri() != null
                            ? new URL(mConfiguration.getUserInfoEndpointUri().toString())
                            : new URL(discovery.getUserinfoEndpoint().toString());
        } catch (MalformedURLException urlEx) {
            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
        }

        URL finalUserInfoEndpoint = userInfoEndpoint;
        mExecutor.submit(() -> {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) finalUserInfoEndpoint.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setInstanceFollowRedirects(false);
                String response = Okio.buffer(Okio.source(conn.getInputStream()))
                        .readString(Charset.forName("UTF-8"));
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



        } else if (ex != null) {
//            displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
//            displayNotAuthorized("No authorization state retained - reauthorization required");
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
        if (PreferencesManager.getTokenExpiry(MainActivity.this) != 0) {
            expiry = PreferencesManager.getTokenExpiry(MainActivity.this);
        }
        super.onResume();

    }

    @Override
    public void onPause() {

        super.onPause();

    }
    }


