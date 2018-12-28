package fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.george.board.FormsActivity;
import com.george.board.R;
import com.george.board.StatusActivity;
import com.george.board.api.RestApi;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.ConfigForms;
import com.george.board.model.ConfigFormsList;
import com.george.board.model.DropdownItem;
import com.thomashaertel.widget.MultiSpinner;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.george.board.FormsActivity.convertDpToPixel;

public class CreditDetailsFragment  extends Fragment {
    Context context;
    ConfigForms newForm;
    ArrayList<View> editTexts;
    private ArrayAdapter<DropdownItem> arrayAdapter;
    private LinearLayout constraintLayout;
    private RestApi api;
    private LinearLayout.LayoutParams p;
    private ConfigForms newFormMulti;
    private int companyId;
    private int cardId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.details_fragment, container, false);
        context = v.getContext();
        api = new RestApi(context);
        cardId = ((StatusActivity)getActivity()).getValue();
        companyId = PreferencesManager.getCompanyId(context);
        constraintLayout = v.findViewById(R.id.layout);
        p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins((int) convertDpToPixel(6, context),
                (int) convertDpToPixel(6, context),
                (int) convertDpToPixel(6, context),
                (int) convertDpToPixel(6, context));
        getForms(companyId,cardId);
        return v;
    }

    public void generateField2(int fieldType, ArrayList<DropdownItem> listItems, ConfigForms forms, int i, int size) {


        switch (fieldType) {

            // TEXT
            case 1:

                EditText text = new EditText(context);
                text.setPadding(55, 20, 0, 0);
                text.setHint(forms.getName());
                text.setText(forms.getDefaultValue());
                text.setGravity(Gravity.START);
                text.setBackground(context.getDrawable(R.drawable.rec));
                text.setTag("TEXT");
                text.setTextColor(getResources().getColor(R.color.colorAccent));
                text.setLayoutParams(p);
                text.setId(i);
                text.setClickable(false);
                text.setFocusable(false);
                constraintLayout.addView(text);

                break;

            // TEXT AREA
            case 2:

                EditText inputTextArea = new EditText(context);
                inputTextArea.setTextColor(Color.BLACK);
                inputTextArea.setGravity(Gravity.START);
                inputTextArea.setPadding(55, 20, 0, 0);
                inputTextArea.setTextSize(16);
                inputTextArea.setHint(forms.getName());
                inputTextArea.setLayoutParams(p);
                inputTextArea.setId(i);
                inputTextArea.setTag("TEXTAREA");
                inputTextArea.setBackground(context.getDrawable(R.drawable.rec));
                inputTextArea.setText(forms.getDefaultValue());
                inputTextArea.setClickable(false);
                inputTextArea.setFocusable(false);

                constraintLayout.addView(inputTextArea);

                break;
            //NUMERIC
            case 3:

                EditText inputNumber = new EditText(context);
                inputNumber.setPadding(55, 20, 0, 0);
                inputNumber.setHint(forms.getName());
                inputNumber.setGravity(Gravity.START);
                inputNumber.setBackground(context.getDrawable(R.drawable.rec));
                inputNumber.setTag("NUMERIC");
                inputNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                inputNumber.setLayoutParams(p);
                inputNumber.setId(i);
                inputNumber.setClickable(false);
                inputNumber.setFocusable(false);
                inputNumber.setText(forms.getDefaultValue());
                constraintLayout.addView(inputNumber);
                break;
//            DROPDOWN
            case 4:

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

                RelativeLayout holder = new RelativeLayout(context);
                holder.setLayoutParams(p);
                Spinner spinner = new Spinner(context);
                ArrayAdapter<DropdownItem> spinnerArrayAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_item, array);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setBackground(context.getDrawable(R.drawable.rec));
                spinner.setAdapter(spinnerArrayAdapter);
//                int selected = Integer.valueOf(forms.getDefaultValue());
//                spinner.setSelection(forms.getListItem().get(selected-1).getId()-1);

                spinner.setTag("DROPDOWN");
                holder.addView(spinner);
                paramsForText.setMargins(0, 4, 0, 0);
                spinner.setLayoutParams(paramsForText);


                constraintLayout.addView(holder);
                break;

            // MULTISELECT
//            case 5:
//                newFormMulti = new ConfigForms();
//                MultiSpinner multiSpinner = new MultiSpinner(context);
//                multiSpinner.setBackground(context.getDrawable(R.drawable.rec));
//                multiSpinner.setLayoutParams(p);
//                multiSpinner.setTextColor(getResources().getColor(R.color.colorAccent));
//                multiSpinner.setTextSize(18);
//                multiSpinner.setPadding((int) convertDpToPixel(10, context),
//                        (int) convertDpToPixel(4, context), 0, 0);
//                arrayAdapter = new ArrayAdapter<>(context,
//                        android.R.layout.simple_spinner_item);
//                for (int j = 0; j < listItems.size(); j++) {
//                    DropdownItem dropdownItem = new DropdownItem();
//                    dropdownItem.setId(listItems.get(j).getId());
//                    dropdownItem.setName(listItems.get(j).getName());
//                    arrayAdapter.add(dropdownItem);
//                }
//
//                multiSpinner.setAdapter(arrayAdapter, true, onSelectedListener);
//                if (arrayAdapter.getCount() != 0) {
//                    boolean[] selectedItems = new boolean[arrayAdapter.getCount()];
//                    selectedItems[0] = true;
//                    multiSpinner.setSelected(selectedItems);
//                }
//                multiSpinner.setDefaultText("CHOOSE WISELY");
//                multiSpinner.setTag("MULTIDROPDOWN");
//                editTexts.add(multiSpinner);
//                newFormMulti.setType(forms.getType());
//                newFormMulti.setId(forms.getId());
//                constraintLayout.addView(multiSpinner);
//                break;

            //DATE
            case 6:
                TextView inputDate = new TextView(context);
                inputDate.setPadding(55, 20, 0, 0);
                inputDate.setTextColor(Color.BLACK);
                inputDate.setTextSize(16);
                inputDate.setId(i);
                inputDate.setTag("DATE");
                inputDate.setLayoutParams(p);
                inputDate.setGravity(Gravity.START);
                inputDate.setHint(forms.getName());
                inputDate.setBackground(context.getDrawable(R.drawable.rec));
                inputDate.setClickable(false);
                inputDate.setFocusable(false);
                inputDate.setText(forms.getDefaultValue());
                constraintLayout.addView(inputDate);
                break;

            //TIME
            case 7:
                TextView inputTime = new TextView(context);


                inputTime.setTextColor(Color.BLACK);
                inputTime.setTextSize(16);
                inputTime.setId(i);
                inputTime.setTag("TIME");
                inputTime.setLayoutParams(p);
                inputTime.setGravity(Gravity.START);
                inputTime.setClickable(false);
                inputTime.setFocusable(false);
                inputTime.setPadding(55, 20, 0, 0);
                inputTime.setHint(forms.getName());
                inputTime.setBackground(context.getDrawable(R.drawable.rec));
                inputTime.setText(forms.getDefaultValue());
                constraintLayout.addView(inputTime);
                break;


        }
    }
    private void getForms(int companyId, int cardId) {
        api.checkInternet(() -> {
            Call<ConfigFormsList> call = api.getCardDetails(companyId, cardId);
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

    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {

        }
    };



}
