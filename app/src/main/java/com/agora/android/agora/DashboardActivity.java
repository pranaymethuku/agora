package com.agora.android.agora;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.design.widget.BottomNavigationView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class DashboardActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        GoogleSignInAccount gsiAccount = getIntent().getParcelableExtra("Account");
        Toast.makeText(this, gsiAccount.getDisplayName(), Toast.LENGTH_SHORT).show();

        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });
}
}