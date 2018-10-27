package com.agora.android.agora;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class DashboardActivity extends AppCompatActivity {

    private TextView mWelcomeMessage;
    private Button mSignOutButton;

    private static final String EXTRA_GOOGLE_ACCOUNT = "com.agora.android.agora.google_account";
    private static final String EXTRA_SIGN_OUT = "com.agora.android.agora.sign_out";

    public static boolean isSignOutRequested(Intent intent) {
        if (intent == null) {
            return false;
        }
        return intent.getBooleanExtra(EXTRA_SIGN_OUT, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottom_nav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return false;
            }
        });

        GoogleSignInAccount gsiAccount = getIntent()
                .getParcelableExtra(EXTRA_GOOGLE_ACCOUNT);

        mWelcomeMessage = (TextView) findViewById(R.id.welcome_message);
        mWelcomeMessage.setText(gsiAccount.getDisplayName());

        mSignOutButton = (Button) findViewById(R.id.button_sign_out);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSignOutRequested(true);
            }
        });
    }

    public static Intent newIntent(Context context, GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(EXTRA_GOOGLE_ACCOUNT, googleSignInAccount);
        return intent;
    }

    private void setSignOutRequested(boolean isSignOutRequested) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SIGN_OUT, isSignOutRequested);
        setResult(RESULT_OK, data);
    }
}
