package com.george.board;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.api.RestApi;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.ExpandableListAdapter;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.ConfigForms;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.DropdownItem;
import com.george.board.model.ExpandedMenuModel;
import com.george.board.model.Menues;
import com.george.board.model.SendForm;
import com.thomashaertel.widget.MultiSpinner;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {
    private LinearLayout constraintLayout;
    private RestApi api;
    private LinearLayout.LayoutParams p;
    final Calendar myCalendar = Calendar.getInstance();
    Button button;
    ArrayList<ConfigForms> sendList;
    ConfigForms newForm;
    ArrayList<View> editTexts;
    ArrayList<Spinner> spinners;
    SendForm sendForm;
    int a;
    int b;

    ConstraintLayout holder;


    private Typeface tf;
    private ArrayAdapter<DropdownItem> arrayAdapter;
    private String multi;
    private ConfigForms newFormMulti;
    private int userId;
    private String userName;
    private String userLastname;
    RelativeLayout root;

    private Menues menu;
    private ArrayList<Menues> menues;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listDataChild;
    private ExpandableListAdapter mMenuAdapter;
    private ImageView navigationDrawerLogo;
    private ImageView logoImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor(PreferencesManager.getAccentColor(this)));
        button = findViewById(R.id.send_btn);
        Button attachFileBtn = findViewById(R.id.attach_file);
        sendList = new ArrayList<>();
        spinners = new ArrayList<>();
        editTexts = new ArrayList<>();
        root = findViewById(R.id.background_second_activity);
        userId = PreferencesManager.getUserId(this);
        userName = PreferencesManager.getUserName(this);
        userLastname = PreferencesManager.getUserLastname(this);
        tf = ResourcesCompat.getFont(this, R.font.raleway_regular);
        api = new RestApi(this);

        NavigationView navigationView = findViewById(R.id.nav_view2);
        View view = navigationView.getHeaderView(0);
        TextView navigationDraweAccentTitle = view.findViewById(R.id.drawerAccent);
        navigationDrawerLogo = view.findViewById(R.id.navigation_view_logo);
        logoImg = findViewById(R.id.logo_img);
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        View view2 = findViewById(R.id.custom_view);
        ImageView menuBtn = view2.findViewById(R.id.menu_btn);
        ExpandableListView expandableList = findViewById(R.id.navigationmenu);
        expandableList.setGroupIndicator(null);
        String color = PreferencesManager.getPrimaryColor(this);
        navigationDraweAccentTitle.setBackgroundColor(Color.parseColor(color));

        expandableList.setOnChildClickListener((expandableListView, view1, i, i1, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) (eListAdapter.getChild(i, i1));
            String url = item.getUrl();
            mDrawerLayout.closeDrawers();
            finish();
            startActivity(new Intent(SecondActivity.this, SecondActivity.class).putExtra("url", url));
            return false;
        });
        expandableList.setOnGroupClickListener((expandableListView, view12, i, l) -> {
            android.widget.ExpandableListAdapter eListAdapter = expandableListView.getExpandableListAdapter();
            ExpandedMenuModel item = (ExpandedMenuModel) eListAdapter.getGroup(i);
            if (eListAdapter.getChildrenCount(i) == 0) {
                String url = item.getUrl();
                if(item.getUrl().isEmpty()){
                    mDrawerLayout.closeDrawers();
                    startActivity(new Intent(SecondActivity.this, MyActivity_activity.class));
                }
                else {
                    finish();
                    startActivity(new Intent(SecondActivity.this, SecondActivity.class).putExtra("url", url));
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
                        mMenuAdapter = new ExpandableListAdapter(SecondActivity.this, listDataHeader, listDataChild, expandableList);
                        expandableList.setAdapter(mMenuAdapter);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ArrayList<Menues>> call, @NonNull Throwable t) {

                }
            });
        });

        CustomViewTarget<RelativeLayout, Drawable> target = new CustomViewTarget<RelativeLayout, Drawable>(root) {
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
        };


        GlideApp.with(SecondActivity.this)
                .load(PreferencesManager.getUserBackground(SecondActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(target);
        Drawable background = button.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        }
        GlideApp.with(SecondActivity.this)
                .load(PreferencesManager.getLogo(SecondActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(navigationDrawerLogo);
        GlideApp.with(SecondActivity.this)
                .load(PreferencesManager.getLogo(SecondActivity.this))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background)
                        .override(Target.SIZE_ORIGINAL))
                .into(logoImg);
        button.setOnClickListener(v -> sendForm());

        Drawable backgroundAttach = attachFileBtn.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) backgroundAttach).getPaint().setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) backgroundAttach).setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) backgroundAttach).setColor(Color.parseColor(PreferencesManager.getPrimaryColor(this)));
        }
        attachFileBtn.setOnClickListener(v -> uploadFileClicked());

        final Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String[] urls = url.split("/");
        a = Integer.valueOf(urls[0]);
        b = Integer.valueOf(urls[1]);
        constraintLayout = findViewById(R.id.layout);
        holder = findViewById(R.id.holder_toolbar);
        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins((int) convertDpToPixel(6, SecondActivity.this),
                (int) convertDpToPixel(6, SecondActivity.this),
                (int) convertDpToPixel(6, SecondActivity.this),
                (int) convertDpToPixel(6, SecondActivity.this));

        getForms(a, b);


    }


    private void getForms(int boardId, int cardId) {
        api.checkInternet(() -> {
            Call<ConfigFormsList> call = api.getForms(boardId, cardId);
            call.enqueue(new Callback<ConfigFormsList>() {
                @Override
                public void onResponse(@NonNull Call<ConfigFormsList> call, @NonNull Response<ConfigFormsList> response) {
                    if (response.isSuccessful()) {
                        ConfigFormsList forms = new ConfigFormsList();
                        ArrayList<ConfigForms> FORM = new ArrayList<>();
                        if (response.body() != null) {
                            forms = response.body();
                            FORM = forms.getForms();
                            if (FORM != null) {
                                for (int i = 0; i < FORM.size(); i++) {
                                    generateField2(FORM.get(i).getType(), FORM.get(i).getListItem(), FORM.get(i), i, FORM.size());
                                }
                            }

                        }

                    }
                }

                @Override
                public void onFailure(@NonNull Call<ConfigFormsList> call, Throwable t) {

                }
            });
        });


    }

    public void uploadFileClicked() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select pdf"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            String path = filePath.getPath();
            File file = new File(path);

        }
    }


    private void sendForm() {
        String value;

        for (int i = 0; i < sendList.size(); i++) {

            String text = editTexts.get(i).getTag().toString();
            switch (text) {
                case "TEXTAREA":
                    EditText editTextTextArea = (EditText) editTexts.get(i);
                    value = editTextTextArea.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                case "TEXT":
                    EditText editTextText = (EditText) editTexts.get(i);
                    value = editTextText.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                case "NUMERIC":
                    EditText editTextNumeric = (EditText) editTexts.get(i);
                    value = editTextNumeric.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                case "DROPDOWN":
                    Spinner spinner = (Spinner) editTexts.get(i);
                    DropdownItem selectedItem = (DropdownItem) spinner.getSelectedItem();
                    int selectedItemId = selectedItem.getId();
                    sendList.get(i).setDefaultValue(String.valueOf(selectedItemId));
                    break;
                case "MULTIDROPDOWN":
                    if (multi.isEmpty()) {
                        String val = String.valueOf(arrayAdapter.getItem(i).getId());
                        sendList.get(i).setDefaultValue(String.valueOf(val));
                    }


                case "DATE":
                    TextView editTextDate = (TextView) editTexts.get(i);
                    value = editTextDate.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                case "TIME":
                    TextView editTextTime = (TextView) editTexts.get(i);
                    value = editTextTime.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                default:
                    break;
            }

        }
        sendForm = new SendForm();
        sendForm.setCardId(b);
        sendForm.setBoardId(a);
        sendForm.setDescription(String.valueOf(userName + " " + userLastname));
        sendForm.setForms(sendList);
        api.checkInternet(() -> {
            Call<ResponseBody> send = api.sendForms(String.valueOf(userId), sendForm);
            send.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(SecondActivity.this, "Успешна апликација.", Toast.LENGTH_LONG).show();
                        finish();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(SecondActivity.this, "Неуспешна апликација.", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });


    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160);
    }


    public void generateField2(int fieldType, ArrayList<DropdownItem> listItems, ConfigForms forms, int i, int size) {


        switch (fieldType) {

            // TEXT
            case 1:
                newForm = new ConfigForms();

                EditText text = new EditText(SecondActivity.this);
                text.setPadding(55, 20, 0, 0);
                text.setHint(forms.getName());
                text.setGravity(Gravity.START);
                text.setBackground(getDrawable(R.drawable.rec));
                text.setTag("TEXT");
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                text.setLayoutParams(p);
                text.setId(i);
                if (i == size - 1) {
                    text.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else
                    text.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                text.setInputType(InputType.TYPE_CLASS_TEXT);
                editTexts.add(text);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(text);

                break;

            // TEXT AREA
            case 2:
                newForm = new ConfigForms();
                EditText inputTextArea = new EditText(this);
                inputTextArea.setTextColor(Color.BLACK);
                inputTextArea.setGravity(Gravity.START);
                inputTextArea.setPadding(55, 20, 0, 0);
                inputTextArea.setTextSize(16);
                inputTextArea.setHint(forms.getName());
                inputTextArea.setLayoutParams(p);
                inputTextArea.setId(i);
                inputTextArea.setTag("TEXTAREA");
                inputTextArea.setBackground(getDrawable(R.drawable.rec));
                if (i == size) {
                    inputTextArea.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else
                    inputTextArea.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                inputTextArea.setInputType(InputType.TYPE_CLASS_TEXT);
                editTexts.add(inputTextArea);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputTextArea);

                break;
            //NUMERIC
            case 3:
                newForm = new ConfigForms();
                EditText inputNumber = new EditText(this);
                inputNumber.setPadding(55, 20, 0, 0);
                inputNumber.setHint(forms.getName());
                inputNumber.setGravity(Gravity.START);
                inputNumber.setBackground(getDrawable(R.drawable.rec));
                inputNumber.setTag("NUMERIC");
                inputNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                inputNumber.setLayoutParams(p);
                inputNumber.setId(i);
                if (i == size) {
                    inputNumber.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else
                    inputNumber.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                editTexts.add(inputNumber);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputNumber);
                break;
//            DROPDOWN
            case 4:
                newForm = new ConfigForms();
                ArrayList<DropdownItem> array = new ArrayList<>();
                for (int j = 0; j < listItems.size(); j++) {
                    DropdownItem dropdownItem = new DropdownItem();
                    dropdownItem.setName(listItems.get(j).getName());
                    dropdownItem.setId(listItems.get(j).getId());
                    array.add(dropdownItem);
                }
                DropdownItem spinnerTitle = new DropdownItem();
                spinnerTitle.setName(forms.getName());
                array.add(0, spinnerTitle);
                RelativeLayout.LayoutParams paramsForText = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                RelativeLayout holder = new RelativeLayout(this);
                holder.setLayoutParams(p);
                Spinner spinner = new Spinner(this);
                ArrayAdapter<DropdownItem> spinnerArrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, array);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setBackground(getDrawable(R.drawable.rec));
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setTag("DROPDOWN");
                holder.addView(spinner);
                paramsForText.setMargins(0, 4, 0, 0);
                spinner.setLayoutParams(paramsForText);
                editTexts.add(spinner);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(holder);
                break;

            // MULTISELECT
            case 5:

                newFormMulti = new ConfigForms();
                MultiSpinner multiSpinner = new MultiSpinner(this);
                multiSpinner.setBackground(getDrawable(R.drawable.rec));
                multiSpinner.setLayoutParams(p);
                multiSpinner.setTextColor(getResources().getColor(R.color.colorAccent));
                multiSpinner.setTextSize(18);
                multiSpinner.setPadding((int) convertDpToPixel(10, this),
                        (int) convertDpToPixel(4, this), 0, 0);
                arrayAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item);
                for (int j = 0; j < listItems.size(); j++) {
                    DropdownItem dropdownItem = new DropdownItem();
                    dropdownItem.setId(listItems.get(j).getId());
                    dropdownItem.setName(listItems.get(j).getName());
                    arrayAdapter.add(dropdownItem);
                }

                multiSpinner.setAdapter(arrayAdapter, true, onSelectedListener);
                if (arrayAdapter.getCount() != 0) {
                    boolean[] selectedItems = new boolean[arrayAdapter.getCount()];
                    selectedItems[0] = true;
                    multiSpinner.setSelected(selectedItems);
                }
                multiSpinner.setDefaultText("CHOOSE WISELY");
                multiSpinner.setTag("MULTIDROPDOWN");
                editTexts.add(multiSpinner);
                newFormMulti.setType(forms.getType());
                newFormMulti.setId(forms.getId());
                constraintLayout.addView(multiSpinner);
                break;

            //DATE
            case 6:
                newForm = new ConfigForms();


                TextView inputDate = new TextView(this);

                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(inputDate);
                    }

                };  inputDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(SecondActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
                inputDate.setPadding(55, 20, 0, 0);
                inputDate.setTextColor(Color.BLACK);
                inputDate.setTextSize(16);
                inputDate.setId(i);
                inputDate.setTag("DATE");
                inputDate.setLayoutParams(p);
                inputDate.setGravity(Gravity.START);
                inputDate.setHint(forms.getName());
                inputDate.setBackground(getDrawable(R.drawable.rec));
                if (i == size) {
                    inputDate.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else
                    inputDate.setImeOptions(EditorInfo.IME_ACTION_NEXT);
//                inputDate.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                editTexts.add(inputDate);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputDate);
                break;

            //TIME
            case 7:
                newForm = new ConfigForms();
                TextView inputTime = new TextView(this);

                inputTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(SecondActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            inputTime.setText( selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();


                }
            });

                inputTime.setTextColor(Color.BLACK);
                inputTime.setTextSize(16);
                inputTime.setId(i);
                inputTime.setTag("TIME");
                inputTime.setLayoutParams(p);
                inputTime.setGravity(Gravity.START);
                if (i == size) {
                    inputTime.setImeOptions(EditorInfo.IME_ACTION_DONE);
                } else
                    inputTime.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                inputTime.setPadding(55, 20, 0, 0);
                inputTime.setHint(forms.getName());
                inputTime.setBackground(getDrawable(R.drawable.rec));
                editTexts.add(inputTime);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputTime);
                break;


        }
    }

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(arrayAdapter.getItem(i).getId()).append(",");
                }

            }
            multi = builder.toString();
            if (!multi.isEmpty()) {
                String multiSpinnerValues = multi.substring(0, multi.length() - 1);
                newFormMulti.setDefaultValue(multiSpinnerValues);
                sendList.add(newFormMulti);
            }

        }
    };

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void updateLabel(TextView editText) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editText.setText(sdf.format(myCalendar.getTime()));
    }


}
