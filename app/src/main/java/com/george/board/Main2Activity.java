package com.george.board;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kofigyan.stateprogressbar.StateProgressBar;

public class Main2Activity extends AppCompatActivity {

    String[] descriptionData = {"Details", "Status", "Photo", "Confirm"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
        stateProgressBar.setStateDescriptionData(descriptionData);
    }
}
