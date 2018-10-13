package com.agora.android.agora;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class DashboardActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        GoogleSignInAccount gsiAccount = getIntent().getParcelableExtra("Account");
        Toast.makeText(this, gsiAccount.getDisplayName(), Toast.LENGTH_SHORT).show();
    }
}
