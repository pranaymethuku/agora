package com.agora.android.agora;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class BaseActivity extends AppCompatActivity {

    private Button mSignOutButton;
    private BottomNavigationView mBottomNavigationView;

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
        setContentView(R.layout.activity_base);

        final GoogleSignInAccount googleSignInAccount = getIntent()
                .getParcelableExtra(EXTRA_GOOGLE_ACCOUNT);

        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment itemFragment = getItemFragment(item, googleSignInAccount);
                fm.beginTransaction()
                        .add(R.id.fragment_container, itemFragment)
                        .commit();
                return true;
            }

        });
//
//
//        mSignOutButton = (Button) findViewById(R.id.button_sign_out);
//        mSignOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setSignOutRequested(true);
//            }
//        });
    }

    private Fragment getItemFragment(MenuItem item, GoogleSignInAccount googleSignInAccount) {
        Fragment itemFragment = null;
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                Toast.makeText(BaseActivity.this, "Dashboard is clicked!", Toast.LENGTH_SHORT).show();
                itemFragment = DashboardFragment.newInstance(googleSignInAccount);

        }
        return itemFragment;
    }

    public static Intent newIntent(Context context, GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(context, BaseActivity.class);
        intent.putExtra(EXTRA_GOOGLE_ACCOUNT, googleSignInAccount);
        return intent;
    }

    private void setSignOutRequested(boolean isSignOutRequested) {
        Intent data = new Intent();
        data.putExtra(EXTRA_SIGN_OUT, isSignOutRequested);
        setResult(RESULT_OK, data);
    }
}
