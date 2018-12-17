package com.george.board;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.george.board.api.RestApi;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.BoardCardList;
import com.george.board.model.ConfigForms;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.DropdownItem;
import com.george.board.model.SendForm;
import com.thomashaertel.widget.MultiSpinner;

import java.io.File;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondActivity extends AppCompatActivity {
    private LinearLayout constraintLayout;
    private RestApi api;
    private ArrayList<EditText> list;
    private LinearLayout.LayoutParams p;
    Button button;
    private Button attachFileBtn;
    ArrayList<ConfigForms> sendList;
    ConfigForms newForm;
    ArrayList<View> editTexts;
    ArrayList<Spinner> spinners;
    SendForm sendForm;
    int a;
    int b;
    private Uri filePath;

    ConstraintLayout holder;
    private ArrayList<DropdownItem> array;

    private String docFilePath;
    private int REQUEST_CODE_DOC;
    private Typeface tf;
    private ArrayAdapter<String> adapterMulti;
    private ArrayAdapter<DropdownItem> arrayAdapter;
    private String multi;
    private ConfigForms newFormMulti;
    private int userId;
    private String userName;
    private String userLastname;
    private int selectedItemId;
    private int defaultSelectedMultiSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        BoardCardList boardCardList;
        button = findViewById(R.id.send_btn);
        attachFileBtn = findViewById(R.id.attach_file);
        sendList = new ArrayList<>();
        spinners = new ArrayList<>();
        editTexts = new ArrayList<>();
        userId = PreferencesManager.getUserId(this);
        userName = PreferencesManager.getUserName(this);
        userLastname = PreferencesManager.getUserLastname(this);
        tf = ResourcesCompat.getFont(this, R.font.raleway_regular);

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
        p.setMargins(convertPixelsToDp(150, SecondActivity.this),
                convertPixelsToDp(20, SecondActivity.this),
                convertPixelsToDp(150, SecondActivity.this),
                convertPixelsToDp(150, SecondActivity.this));
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
                                generateField2(FORM.get(i).getName(), FORM.get(i).getType(), FORM.get(i).getListItem(), FORM.get(i), i, FORM.size());
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
                    selectedItemId = selectedItem.getId();
                    sendList.get(i).setDefaultValue(String.valueOf(selectedItemId));
                    break;
                    case "MULTIDROPDOWN":
                        if (multi.isEmpty()){
                            String val = String.valueOf(arrayAdapter.getItem(i).getId());
                            sendList.get(i).setDefaultValue(String.valueOf(val));
                            }


                case "DATE":
                    EditText editTextDate = (EditText) editTexts.get(i);
                    value = editTextDate.getText().toString();
                    sendList.get(i).setDefaultValue(value);
                    break;
                case "TIME":
                    EditText editTextTime = (EditText) editTexts.get(i);
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
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Toast.makeText(SecondActivity.this, "Успешна апликација.", Toast.LENGTH_LONG).show();
                    finish();

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SecondActivity.this, "Неуспешна апликација.", Toast.LENGTH_LONG).show();
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


    public void generateField2(String fieldName, int fieldType, ArrayList<DropdownItem> listItems, ConfigForms forms, int i, int size) {


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
                text.setTextColor(getColor(R.color.colorAccent));
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
                inputNumber.setTextColor(getColor(R.color.colorAccent));
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
                array = new ArrayList<DropdownItem>();
                for (int j = 0; j < listItems.size(); j++) {
                    DropdownItem dropdownItem = new DropdownItem();
                    dropdownItem.setName(listItems.get(j).getName());
                    dropdownItem.setId(listItems.get(j).getId());
                    array.add(dropdownItem);
                }
                DropdownItem spinnerTitle = new DropdownItem();
                spinnerTitle.setName( forms.getName());
                array.add(0, spinnerTitle);
                RelativeLayout.LayoutParams paramsForText = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                RelativeLayout holder = new RelativeLayout(this);
                holder.setLayoutParams(p);
                TextView description = new TextView(this);
                description.setText("CHOOSE WISELY BITCH: ");
                description.setTypeface(tf, Typeface.BOLD);
                description.setId(View.generateViewId());
                holder.addView(description);
                Spinner spinner = new Spinner(this);
                ArrayAdapter<DropdownItem> spinnerArrayAdapter = new ArrayAdapter<DropdownItem>(this,
                        android.R.layout.simple_spinner_item, array);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setBackground(getDrawable(R.drawable.rec));
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setTag("DROPDOWN");
                holder.addView(spinner);
                paramsForText.setMargins(0, 4, 0, 0);
                paramsForText.addRule(RelativeLayout.BELOW, description.getId());
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
                multiSpinner.setTextColor(getColor(R.color.colorAccent));
                multiSpinner.setTextSize(18);
                multiSpinner.setPadding((int) convertDpToPixel(10, this),
                        (int) convertDpToPixel(4, this), 0, 0);
                arrayAdapter = new ArrayAdapter<DropdownItem>(this,
                        android.R.layout.simple_spinner_item);
                for (int j = 0; j < listItems.size(); j++) {
                    DropdownItem dropdownItem = new DropdownItem();
                    dropdownItem.setId(listItems.get(j).getId());
                    dropdownItem.setName(listItems.get(j).getName());
                    arrayAdapter.add(dropdownItem);
                }

                multiSpinner.setAdapter(arrayAdapter, true, onSelectedListener);
                boolean[] selectedItems = new boolean[arrayAdapter.getCount()];
                selectedItems[0] = true;
                 defaultSelectedMultiSpinner = arrayAdapter.getItem(0).getId();
                multiSpinner.setSelected(selectedItems);
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
                EditText inputDate = new EditText(this);
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
                inputDate.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                editTexts.add(inputDate);
                newForm.setType(forms.getType());
                newForm.setId(forms.getId());
                sendList.add(newForm);
                constraintLayout.addView(inputDate);
                break;

            //TIME
            case 7:
                newForm = new ConfigForms();
                EditText inputTime = new EditText(this);
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
                inputTime.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
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
            String multiSpinnerValues = multi.substring(0, multi.length() - 1);
            newFormMulti.setDefaultValue(multiSpinnerValues);
            sendList.add(newFormMulti);


            // Do something here with the selected items
        }
    };

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


}
