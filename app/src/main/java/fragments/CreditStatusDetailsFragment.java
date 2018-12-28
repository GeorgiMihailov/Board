package fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.george.board.R;
import com.george.board.StatusActivity;
import com.george.board.api.RestApi;
import com.george.board.appAuth.GlideApp;
import com.george.board.helper.PreferencesManager;
import com.george.board.model.CreditStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.george.board.FormsActivity.convertDpToPixel;

public class CreditStatusDetailsFragment extends Fragment {
    Context context;

    private LinearLayout circlesLayout;
    private LinearLayout.LayoutParams viewParams;
    private LinearLayout.LayoutParams lineParams;
    private LinearLayout textLayout;
    private LinearLayout.LayoutParams textParams;
    private ScrollView mainLayout;
    private LinearLayout holderLinearLayout;
    private RestApi api;
    private int companyId;

    private int cardId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.status_fragment, container, false);
        context = v.getContext();


        api = new RestApi(context);
        companyId = PreferencesManager.getCompanyId(context);
        cardId = ((StatusActivity)getActivity()).getValue();
        //VIEW FORMS
        circlesLayout = new LinearLayout(context);
        circlesLayout.setOrientation(LinearLayout.VERTICAL);
        lineParams = new LinearLayout.LayoutParams((int) convertDpToPixel(8, context),
                (int) convertDpToPixel(30, context));
//        lineParams.setMargins((int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this),
//                (int) convertDpToPixel(3, this));
        lineParams.gravity = Gravity.CENTER;
        viewParams = new LinearLayout.LayoutParams((int) convertDpToPixel(40, context),
                (int) convertDpToPixel(40, context));
        LinearLayout.LayoutParams formsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        formsParams.setMargins((int) convertDpToPixel(20, context),
                (int) convertDpToPixel(20, context),
                0,
                0);
        circlesLayout.setLayoutParams(formsParams);

        mainLayout = v.findViewById(R.id.holder_layout);
        holderLinearLayout = new LinearLayout(context);
        holderLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // TEXT LINEAR LAYOUT
        textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        textLayoutParams.setMargins(0, (int) convertDpToPixel(20, context), 0, 0);
        textLayout.setLayoutParams(textLayoutParams);
        textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(40, context));
        textParams.setMargins(0, 0, 0, (int) convertDpToPixel(30, context));
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
                            View view = new View(context);
                            if (postion == i) {
                                view.setBackground(context.getDrawable(R.drawable.status_shape_in_progress));
                            } else if (postion < statusNames.indexOf(statusNames.get(i))) {
                                view.setBackground(context.getDrawable(R.drawable.status_shape_to_do));
                            } else if (postion > statusNames.indexOf(statusNames.get(i)))
                                view.setBackground(context.getDrawable(R.drawable.status_shape_completed));


                            view.setLayoutParams(viewParams);
                            circlesLayout.addView(view);
                            if (i < statusNames.size() - 1) {
                                View line = new View(context);
                                line.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                line.setLayoutParams(lineParams);
                                circlesLayout.addView(line);
                            }


                            //TEXT VIEWS
                            TextView statusText = new TextView(context);
                            statusText.setGravity(Gravity.START);
                            statusText.setPadding((int) convertDpToPixel(30, context), (int) convertDpToPixel(8, context), 0, 0);
                            statusText.setText(statusNames.get(i));
                            statusText.setTextSize(16);
                            statusText.setTextColor(getResources().getColor(R.color.colorAccent));
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


        return v;

    }
}


