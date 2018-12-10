package com.george.board;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.DisplayMetrics;
import android.view.SubMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.george.board.api.RestApi;
import com.george.board.helper.ExpandableListAdapter;
import com.george.board.helper.OnExpandableListElementClick;
import com.george.board.model.BoardCard;
import com.george.board.model.BoardCardList;
import com.george.board.model.ConfigForms;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnExpandableListElementClick {

    private RestApi api;
    private int subItem;
    ArrayList<Menues> menues;
    private Menues menu;
    private NavigationView navigationView;
    TextView credit;
    TextView card;
    TextView accounts;
    TextView calc;
    TextView excahne;
    TextView atm;
    TextView deposit;
    TextView add;
    ConstraintLayout rootView;
    TextView cardText;
    int cardId;
    int boardId;
    ArrayList<BoardCard> boardCards;
    BoardCard BoardCard;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private DrawerLayout mDrawerLayout;
    ConstraintLayout constraintLayout;
    int angle = 0;
    int radius = 130;
    ConstraintSet c;


    private TextView creditText;
    private CircleImageView centerImage;
    private ConstraintLayout parentLayouy;
    private TextView icontext;
    private Typeface ta;
    private Typeface tf;
    private RelativeLayout.LayoutParams params;
    private ConstraintSet constraintSet;
    private String g;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//
        api = new RestApi(this);
        boardCards = new ArrayList<>();
        constraintLayout = findViewById(R.id.circle_view_holder);
        rootView = findViewById(R.id.root);
        centerImage = findViewById(R.id.sun_image);
        expandableList = findViewById(R.id.navigationmenu);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        expandableList.setGroupIndicator(null);

        constraintSet = new ConstraintSet();


        ta = ResourcesCompat.getFont(this, R.font.bankicon);
        tf = ResourcesCompat.getFont(this, R.font.raleway_regular);
        params = new RelativeLayout.LayoutParams(
                210, 210);


        prepareListData();


        // setting list adapter

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


    public void onItemClick(View view) {
        Object tag = view.getTag();
        switch ((String) tag) {
            case "1":
                ArrayList<Integer> url = new ArrayList<>();
                url.add(boardId);
                url.add(cardId);
                Bundle ids = new Bundle();
                ids.putIntegerArrayList("ids", url);
                Bundle extraBundle = new Bundle();
                BoardCardList list = new BoardCardList();
                list.setList(boardCards);
                extraBundle.putSerializable("arraylist", list);
                startActivity(new Intent(MainActivity.this, SecondActivity.class).putExtras(extraBundle).putExtras(ids));
                break;
            case "2":
                startActivity(new Intent(MainActivity.this, CircleConstraintsActivity.class));
                break;

        }
    }

    private void prepareListData() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        constraintSet.constrainWidth(R.id.sun_image, (int) convertDpToPixel(130, MainActivity.this));
        constraintSet.constrainHeight(R.id.sun_image, (int) convertDpToPixel(130, MainActivity.this));

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
                                    String v = (menues.get(i).getIcon());
                                    g = new String(Character.toChars(Integer.parseInt(
                                            v, 16)));
                                } else {
                                    iconHolder.setPadding((int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), (int) convertDpToPixel(6, MainActivity.this), 0);
                                    iconHolder.setTextSize(32);
                                    ta = ResourcesCompat.getFont(MainActivity.this, R.font.fontawesome_webfont);
                                    iconHolder.setTypeface(ta);
                                    String v = (menues.get(i).getIcon());
                                    g = new String(Character.toChars(Integer.parseInt(
                                            v, 16)));
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
                                    listDataHeader.add(item1);
                                    listDataChild.put(listDataHeader.get(i), new ArrayList<>());

                                }
                            }
                            mMenuAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild, expandableList);
                            expandableList.setAdapter(mMenuAdapter);
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
        Toast.makeText(MainActivity.this, "YEAAAH", Toast.LENGTH_LONG).show();

    }
}


