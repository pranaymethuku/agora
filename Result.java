package com.agora.android.agora;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Result extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView result=(TextView)findViewById(R.id.resultView);

            for (int i = 0; i < DashboardActivity.idMatch.size(); i++) {
                result.setText(result.getText() + " " + DashboardActivity.idMatch.get(i) + " " + DashboardActivity.nameMatch.get(i) + " \n");

            }




    }
}
