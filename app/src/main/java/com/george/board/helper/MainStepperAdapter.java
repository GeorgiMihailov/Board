//package com.george.board.helper;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.liefery.android.vertical_stepper_view.VerticalStepperAdapter;
//import com.liefery.android.vertical_stepper_view.VerticalStepperItemView;
//
//import java.util.ArrayList;
//
//
//public class MainStepperAdapter extends VerticalStepperAdapter {
//
//    ArrayList<VerticalStepperItemView> items;
//
//    public MainStepperAdapter( Context context ) {
//
//        super( context );
//    }
//
//
//    @NonNull
//    @Override
//    public CharSequence getTitle( int position ) {
//        return "Title " + position;
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getSummary( int position ) {
//        return "Summary " + position;
//    }
//
//    @Override
//    public boolean isEditable( int position ) {
//        return position == 1;
//    }
//
//    @Override
//    public int getCount() {
//        return 7;
//    }
//
//    @Override
//    public Void getItem( int position ) {
//        return null;
//    }
//
//    @NonNull
//    @Override
//    public View onCreateContentView( Context context, int position ) {
//        View content = new MainItemView( context );
//        jumpTo(position);
//
//
//
//        return content;
//    }
//}