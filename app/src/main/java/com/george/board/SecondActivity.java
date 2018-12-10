package com.george.board;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.george.board.api.RestApi;
//import com.george.board.helper.CircleMenu;
import com.george.board.model.BoardCard;
import com.george.board.model.BoardCardList;
import com.george.board.model.ConfigForms;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.DropdownItem;
import com.george.board.model.SendForm;
import com.george.board.model.Menues;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {
    private LinearLayout constraintLayout;
    private RestApi api;
    private ArrayList<Menues> users;
    private ArrayList<EditText> list;
    private LinearLayout.LayoutParams p;
    private Button btn;
    private static final int BUTTON = 0;
    private static final int EDITTEXT = 1;
    private static final int DROPDOWN = 2;
    private static final int MULTIPLE_DROPDOWN = 3;
    private static final int DATE = 4;
    private static final int TIME = 5;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ArrayList<BoardCard> boardCards;
    Button button;
    private Button attachFileBtn;
    ArrayList<ConfigForms> sendList;
    ConfigForms newForm;
    ArrayList<EditText> editTexts;
    SendForm sendForm;
    int a;
    int b;
    private Uri filePath;

    ConstraintLayout holder;
    private ArrayList<String> array;

    private String docFilePath;
    private int REQUEST_CODE_DOC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        BoardCardList boardCardList;
        button = findViewById(R.id.send_btn);
        attachFileBtn = findViewById(R.id.attach_file);
        sendList = new ArrayList<>();
        editTexts = new ArrayList<>();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForm();
            }
        });

        attachFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             uploadFileClicked();
            }
        });

        final Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String[] urls = url.split("/");
        a = Integer.valueOf(urls[0]);
        b = Integer.valueOf(urls[1]);
        constraintLayout = findViewById(R.id.layout);
        holder = findViewById(R.id.holder_toolbar);
        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(convertPixelsToDp(150, SecondActivity.this), convertPixelsToDp(20, SecondActivity.this), convertPixelsToDp(150, SecondActivity.this), convertPixelsToDp(150, SecondActivity.this));
        api = new RestApi(this);
        list = new ArrayList<>();
        getForms(a, b);


    }




    private void getForms(int boardId, int cardId) {
        api.checkInternet(new Runnable() {
            @Override
            public void run() {
                Call<ConfigFormsList> call = api.getForms(boardId, cardId);
                call.enqueue(new Callback<ConfigFormsList>() {
                    @Override
                    public void onResponse(Call<ConfigFormsList> call, Response<ConfigFormsList> response) {
                        if (response.isSuccessful()) {
                            ConfigFormsList forms = new ConfigFormsList();
                            ArrayList<ConfigForms> FORM = new ArrayList<>();
                            forms = response.body();
                            FORM = forms.getForms();
                            for (int i = 0; i < FORM.size(); i++) {
                                generateField2(FORM.get(i).getName(), FORM.get(i).getType(), FORM.get(i).getListItem(), FORM.get(i), i);
//                                if (FORM.get(i).getType() == 1 || FORM.get(i).getType() == 2 ||FORM.get(i).getType() == 3){
//                                    newForm = new ConfigForms();
//                                    EditText text = new EditText(SecondActivity.this);
//                                    text.setPadding(55,20,0,0);
//                                    text.setHint(forms.getForms().get(i).getName());
//                                    text.setGravity(Gravity.START);
//                                    text.setBackground(getDrawable(R.drawable.rec));
//                                    text.setTag(FORM.get(i).getType());
//                                    text.setTextColor(getColor(R.color.colorAccent));
//                                    text.setLayoutParams(p);
//                                    text.setId(i);
//                                    editTexts.add(text);
//                                    newForm.setType(FORM.get(i).getType());
//                                    newForm.setId(FORM.get(i).getId());
//                                    sendList.add(newForm);
//                                    constraintLayout.addView(text);
//                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ConfigFormsList> call, Throwable t) {

                    }
                });
            }
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
            filePath = data.getData();
            String path = filePath.getPath();
            File file = new File(path);
            file.exists();

        }
    }



    private void sendForm() {
        for (int i = 0; i < sendList.size(); i++) {
            String text = editTexts.get(i).getText().toString();
            sendList.get(i).setDefaultValue(text);
        }
        sendForm = new SendForm();
        sendForm.setCardId(b);
        sendForm.setBoardId(a);
        sendForm.setForms(sendList);

        api.checkInternet(() -> {
            Call<SendForm> send = api.sendForms(sendForm);
            send.enqueue(new Callback<SendForm>() {
                @Override
                public void onResponse(Call<SendForm> call, Response<SendForm> response) {

                    Toast.makeText(SecondActivity.this,"Успешна апликација.",Toast.LENGTH_LONG).show();
                    finish();

                }

                @Override
                public void onFailure(Call<SendForm> call, Throwable t) {
                    Toast.makeText(SecondActivity.this,"Неуспешна апликација.",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        });


    }

    public static int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = px / (metrics.densityDpi / 160);
        return dp;
    }


    public void generateField2(String fieldName, int fieldType, ArrayList<DropdownItem> listItems, ConfigForms forms,int i) {


        switch (fieldType) {
            // TEXT
            case 1:
                newForm = new ConfigForms();
                EditText text = new EditText(SecondActivity.this);
                text.setPadding(55, 20, 0, 0);
                text.setHint(forms.getName());
                text.setGravity(Gravity.START);
                text.setBackground(getDrawable(R.drawable.rec));
                text.setTag(forms.getType());
                text.setTextColor(getColor(R.color.colorAccent));
                text.setLayoutParams(p);
                text.setId(i);
                editTexts.add(text);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(text);

//                EditText inputText = new EditText(this);
//                inputText.setInputType(InputType.TYPE_CLASS_TEXT);
//                inputText.setTextColor(Color.BLACK);
//                inputText.setTextSize(16);
//                inputText.setEnabled(false);
//                constraintLayout.addView(inputText);

                break;
            // TEXT AREA
            case 2:
                newForm = new ConfigForms();
                EditText inputTextArea = new EditText(this);
                inputTextArea.setInputType(InputType.TYPE_CLASS_TEXT);
                inputTextArea.setTextColor(Color.BLACK);
                inputTextArea.setTextSize(16);
                inputTextArea.setEnabled(false);
                constraintLayout.addView(inputTextArea);

                break;
            //NUMERIC
            case 3:
                newForm = new ConfigForms();
                EditText inputNumber = new EditText(this);
                inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputNumber.setPadding(55, 20, 0, 0);
                inputNumber.setHint(forms.getName());
                inputNumber.setGravity(Gravity.START);
                inputNumber.setBackground(getDrawable(R.drawable.rec));
                inputNumber.setTag(forms.getType());
                inputNumber.setTextColor(getColor(R.color.colorAccent));
                inputNumber.setLayoutParams(p);
                inputNumber.setId(i);
                editTexts.add(inputNumber);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputNumber);
                break;
            //DROPDOWN
            case 4:
                array = new ArrayList<String>();
                for (int j = 0; j < listItems.size(); j++) {
                    array.add(listItems.get(j).getName());
                }
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array); //selected item will look like a spinner set from XML
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setPopupBackgroundDrawable(new ColorDrawable(Color.WHITE));
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setEnabled(false);
                constraintLayout.addView(spinner);

                break;

//            case "radio":
//                final RadioButton[] rb = new RadioButton[5];
//                RadioGroup rg = new RadioGroup(this);
//                rg.setOrientation(RadioGroup.HORIZONTAL);
//                for(int k = 0; k < listItems.size(); k++){
//                    rb[k]  = new RadioButton(this);
//                    rb[k].setText(listItems.get(k));
//                    rb[k].setTextColor(Color.BLACK);
//                    rg.addView(rb[k]);
//                }
//                linearLayout.addView(rg);
//                id++;
//                break;
            //TIME
            case 7:
                EditText inputTime = new EditText(this);
                inputTime.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
                inputTime.setTextColor(Color.BLACK);
                inputTime.setTextSize(16);
                inputTime.setFocusableInTouchMode(false);
//                inputTime.setOnClickListener(this);
                inputTime.setEnabled(false);
                constraintLayout.addView(inputTime);

                break;
            //DATE
            case 6:
                EditText inputDate = new EditText(this);
                inputDate.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                inputDate.setTextColor(Color.BLACK);
                inputDate.setTextSize(16);
                inputDate.setFocusableInTouchMode(false);
//                inputDate.setOnClickListener(this);
                inputDate.setEnabled(false);
                constraintLayout.addView(inputDate);

                break;
        }
    }






}
