package com.george.board.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.board.R;
import com.george.board.model.ExpandedMenuModel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final Typeface ta;
    private final Typeface tf;
    private Context mContext;
    //    public OnExpandableListElementClick onExpandableListElementClick;
    private List<ExpandedMenuModel> mListDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> mListDataChild;

    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listChildData, ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        ta = ResourcesCompat.getFont(context, R.font.bankicon);
        tf = ResourcesCompat.getFont(context, R.font.fontawesome_webfont);
    }
//    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<ExpandedMenuModel>> listChildData, ExpandableListView mView, OnExpandableListElementClick onExpandableListElementClick) {
//        this.mContext = context;
//        this.mListDataHeader = listDataHeader;
//        this.mListDataChild = listChildData;
//        this.onExpandableListElementClick = onExpandableListElementClick;
//        ta = ResourcesCompat.getFont(context, R.font.bankicon);
//        tf = ResourcesCompat.getFont(context, R.font.fontawesome_webfont);
//    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d("GROUPCOUNT", String.valueOf(i));
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        if (groupPosition != 2) {
            childCount = Objects.requireNonNull(this.mListDataChild.get(this.mListDataHeader.get(groupPosition)))
                    .size();
        }
        return childCount;

    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("CHILD", Objects.requireNonNull(mListDataChild.get(this.mListDataHeader.get(groupPosition)))
                .get(childPosition).toString());
        return Objects.requireNonNull(this.mListDataChild.get(this.mListDataHeader.get(groupPosition)))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listheader, null);
        }
        int childCount = getChildrenCount(groupPosition);
        ImageView dropdownArrow = convertView.findViewById(R.id.dropdown);
        TextView lblListHeader = convertView
                .findViewById(R.id.submenu);
        TextView headerIcon = convertView.findViewById(R.id.iconimage);

        if (childCount == 0) {
            dropdownArrow.setVisibility(View.GONE);
        } else if (isExpanded)
            dropdownArrow.setVisibility(View.INVISIBLE);
        else if (!isExpanded)
            dropdownArrow.setVisibility(View.VISIBLE);

        if (!headerTitle.getIconImgText().isEmpty()) {
            String b = headerTitle.getIconImgText();
            String c = new String(Character.toChars(Integer.parseInt(
                    b, 16)));
            if (headerTitle.getIconImgText().startsWith("e")) {
                headerIcon.setTypeface(ta);
                headerIcon.setText(c);
                headerIcon.setTextSize(40);
                headerIcon.setPadding((int) convertDpToPixel(20,convertView.getContext()), 0, 0, 0);
                lblListHeader.setPadding((int) convertDpToPixel(20,convertView.getContext()), 0, 0, 0);
                lblListHeader.setText(headerTitle.getIconName());
            } else if (headerTitle.getIconImgText().startsWith("f")){
                headerIcon.setTypeface(tf);
                headerIcon.setTextSize(22);
                headerIcon.setPadding((int) convertDpToPixel(30,convertView.getContext()), 0, 0, 0);
                headerIcon.setText(c);
                lblListHeader.setPadding((int) convertDpToPixel(30,convertView.getContext()), 0, 0, 0);
                lblListHeader.setText(headerTitle.getIconName());
            }

        }else
        {
            lblListHeader.setPadding((int) convertDpToPixel(30,convertView.getContext()), 0, 0, 0);
            lblListHeader.setText(headerTitle.getIconName());
        }


        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ExpandedMenuModel subMenuModel = (ExpandedMenuModel) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }


        TextView txtListChild = convertView
                .findViewById(R.id.submenu1);
        TextView txtListChildIcon = convertView
                .findViewById(R.id.childIconImage);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onExpandableListElementClick.onExpandableListElementClick(subMenuModel,childPosition);
//            }
//        });

        if (subMenuModel.getIconImgText().startsWith("e")) {
            txtListChildIcon.setTypeface(tf);
            txtListChildIcon.setText(subMenuModel.getIconImgText());
        } else {
            txtListChildIcon.setTypeface(ta);
            txtListChildIcon.setText(subMenuModel.getIconImgText());
        }
        txtListChild.setText(subMenuModel.getIconName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}